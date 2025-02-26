package connect.event.participant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import connect.event.entity.*;
import connect.event.exception.ResourceNotFoundException;
import connect.event.participant.DTO.*;
import connect.event.participant.entity.BilletAcheter;
import connect.event.participant.repository.BilletAcheterRepository;
import connect.event.repository.BilletRepository;
import connect.event.repository.EvenementRepository;
import connect.event.repository.UtilisateurRepository;
import connect.event.utils.JwtUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Service
public class BilletAcheterService {

    private static final Logger LOGGER = Logger.getLogger(BilletAcheterService.class.getName());

    @Autowired
    private PayDunyaService payDunyaService;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private BilletRepository billetRepository;

    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private BilletAcheterRepository billetAcheterRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<FactureResponse> acheterBillet(BilletSelectionDTO billetDTO) {
        try {
            LOGGER.info("Début de l'achat du billet. Données reçues: " + billetDTO);

            Utilisateur participant = utilisateurRepository.findById(billetDTO.getParticipantId())
                    .orElseThrow(() -> new RuntimeException("Participant non trouvé, ID : " + billetDTO.getParticipantId()));

            Evenement evenement = evenementRepository.findById(billetDTO.getEvenementId())
                    .orElseThrow(() -> new RuntimeException("Événement non trouvé, ID : " + billetDTO.getEvenementId()));

            if (billetDTO.getBillets() == null || billetDTO.getBillets().isEmpty()) {
                throw new RuntimeException("Aucun billet sélectionné.");
            }

            List<BilletAcheter> billetsAchetes = new ArrayList<>();
            BigDecimal montantTotal = BigDecimal.ZERO;

            String referenceTransaction = "PD-" + UUID.randomUUID();
            int totalQuantity = 0;

            for (BilletItemDTO item : billetDTO.getBillets()) {
                if (item.getBilletId() == null) {
                    throw new RuntimeException("❌ Billet ID est NULL ! Vérifie le frontend.");
                }

                Billet billet = billetRepository.findById(item.getBilletId())
                        .orElseThrow(() -> new RuntimeException("Billet non trouvé: " + item.getBilletId()));

                LOGGER.info("Billet sélectionné - ID: " + item.getBilletId() + ", Quantité: " + item.getQuantite());


                if (billet.getQuantite() < item.getQuantite()) {
                    throw new RuntimeException("Stock insuffisant pour le billet: " + billet.getTypeBillet());
                }


                BigDecimal sousTotal = billet.getPrix().multiply(BigDecimal.valueOf(item.getQuantite()));
                totalQuantity += item.getQuantite(); // Ajouter la quantité
                montantTotal = montantTotal.add(sousTotal);

                BilletAcheter billetAcheter = new BilletAcheter();
                billetAcheter.setQuantite(item.getQuantite());
                billetAcheter.setMontantTotal(sousTotal);
                billetAcheter.setStatutPaiement("PAYE");
                billetAcheter.setParticipant(participant);
                billetAcheter.setBillet(billet);
                billetAcheter.setEvenement(evenement);
                billetAcheter.setReferenceTransaction(referenceTransaction);

                billetsAchetes.add(billetAcheter);
                montantTotal = montantTotal.add(sousTotal);

                billet.setQuantite(billet.getQuantite() - item.getQuantite());
                billetRepository.save(billet);
            }

            String paymentUrl = payDunyaService.createInvoice(
                    billetsAchetes, montantTotal, participant.getEmail(),
                    "http://votre-domaine.com/callback-paiement"
            );

            billetAcheterRepository.saveAll(billetsAchetes);

            // Remplacer la partie Kafka
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); // Important pour Java 8+


            Map<String, Object> kafkaMessage = new HashMap<>();
            kafkaMessage.put("eventId", evenement.getId_evenement());
            kafkaMessage.put("montantTotal", montantTotal.doubleValue());
            kafkaMessage.put("quantite", totalQuantity);

            String message = mapper.writeValueAsString(kafkaMessage);

            LOGGER.info("Message Kafka validé : " + message); // Vérifier le format
            kafkaTemplate.send("ticket-purchases", message);


            return ResponseEntity.ok(new FactureResponse("success", "Facture générée avec succès", paymentUrl, referenceTransaction));

        } catch (Exception e) {
            LOGGER.severe("Erreur lors de l'achat du billet: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FactureResponse("error", e.getMessage(), null, null));
        }
    }


    public ResponseEntity<PaiementResponse> verifierPaiement(String referenceTransaction) {
        try {
            List<BilletAcheter> billets = billetAcheterRepository.findByReferenceTransaction(referenceTransaction);

            if (billets.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new PaiementResponse("error", "Transaction non trouvée", null));
            }

            boolean paiementValide = payDunyaService.verifierPaiement(referenceTransaction);

            if (paiementValide) {
                billets.forEach(billet -> {
                    billet.setStatutPaiement("PAYE");
                    billetAcheterRepository.save(billet);
                });

                byte[] qrCode = qrCodeService.generateQRCode(referenceTransaction, 250, 250);

                emailService.sendBilletEmail(
                        billets.get(0).getParticipant().getEmail(),
                        billets.get(0).getParticipant().getNom(),
                        billets.get(0).getEvenement().getNom(),
                        billets.stream().mapToInt(BilletAcheter::getQuantite).sum(),
                        billets.stream().map(BilletAcheter::getMontantTotal).reduce(BigDecimal.ZERO, BigDecimal::add),
                        referenceTransaction,
                        qrCode
                );

                return ResponseEntity.ok(new PaiementResponse("success", "Paiement confirmé", billets.get(0)));

            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new PaiementResponse("error", "Paiement non confirmé", null));
            }

        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la vérification du paiement: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PaiementResponse("error", "Erreur de vérification: " + e.getMessage(), null));
        }
    }

    public List<BilletParticipantDTO> getBilletsByParticipant(Long participantId) {
        List<BilletAcheter> billets = billetAcheterRepository.findByParticipantIdWithDetails(participantId);
        return billets.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private BilletParticipantDTO convertToDTO(BilletAcheter billetAcheter) {
        BilletParticipantDTO dto = new BilletParticipantDTO();
        Evenement event = billetAcheter.getEvenement();

        dto.setId(billetAcheter.getId());
        dto.setEventNom(event.getNom());
        dto.setEventLieu(event.getLieu());
        dto.setEventDate(event.getDate());
        dto.setTypeBillet(billetAcheter.getBillet().getTypeBillet());
        dto.setQuantite(billetAcheter.getQuantite());
        dto.setMontantTotal(billetAcheter.getMontantTotal());
        dto.setReferenceTransaction(billetAcheter.getReferenceTransaction());

        // Calcul dynamique du statut
        dto.setStatut(calculerStatut(billetAcheter));

        return dto;
    }

    private String calculerStatut(BilletAcheter billet) {
        if ("ANNULE".equals(billet.getStatutPaiement())) {
            return "ANNULÉ";
        }

        Date now = new Date();
        if (billet.getEvenement().getDate().before(now)) {
            return "PASSÉ";
        }

        return billet.getStatutPaiement();
    }

    @Transactional
    public ResponseEntity<?> annulerBillet(Long billetId, Long participantId) {
        try {
            BilletAcheter billet = billetAcheterRepository.findById(billetId)
                    .orElseThrow(() -> new RuntimeException("Billet non trouvé"));

            // Validation
            if (!billet.getParticipant().getIdUtilisateur().equals(participantId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès non autorisé");
            }

            if (billet.getEvenement().getDate().before(new Date())) {
                return ResponseEntity.badRequest().body("Impossible d'annuler un événement passé");
            }

            if (!"PAYE".equals(billet.getStatutPaiement())) {
                return ResponseEntity.badRequest().body("Seuls les billets payés peuvent être annulés");
            }

            // Mise à jour du statut
            billet.setStatutPaiement("ANNULE");
            billetAcheterRepository.save(billet);

            // Restaurer le stock
            Billet original = billet.getBillet();
            original.setQuantite(original.getQuantite() + billet.getQuantite());
            billetRepository.save(original);

            // Demande de remboursement
            boolean refundSuccess = payDunyaService.demanderRemboursement(
                    billet.getReferenceTransaction(),
                    billet.getMontantTotal()
            );

            if (!refundSuccess) {
                throw new RuntimeException("Échec du remboursement");
            }

            return ResponseEntity.ok("Billet annulé avec succès");

        } catch (Exception e) {
            LOGGER.severe("Erreur d'annulation: " + e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    public byte[] generateTicketPdf(BilletParticipantDTO dto) throws Exception {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(400, 600)); // Format de ticket plus petit et plus pratique
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Définition des couleurs
                Color primaryColor = new Color(0, 102, 204);    // Bleu primaire
                Color accentColor = new Color(255, 69, 0);      // Orange accent
                Color lightBg = new Color(245, 245, 250);       // Fond clair
                Color darkText = new Color(30, 30, 50);         // Texte foncé

                // Fond principal
                contentStream.setNonStrokingColor(lightBg);
                contentStream.addRect(0, 0, 400, 600);
                contentStream.fill();

                // Bannière supérieure
                contentStream.setNonStrokingColor(primaryColor);
                contentStream.addRect(0, 520, 400, 80);
                contentStream.fill();

                // Logo et titre
                PDImageXObject logo = PDImageXObject.createFromFile("C:/Users/Arona Ndiaye/OneDrive/Documents/Document/Memoire_M2/Projet_memoire_m2/images/img.png", document);
                contentStream.drawImage(logo, 20, 530, 50, 50);

                // Titre
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 22);
                contentStream.setNonStrokingColor(Color.WHITE);
                contentStream.beginText();
                contentStream.newLineAtOffset(90, 550);
                contentStream.showText("TICKET D'ENTRÉE");
                contentStream.endText();

                // Conteneur principal
                contentStream.setNonStrokingColor(Color.WHITE);
                contentStream.addRect(20, 100, 360, 400);
                contentStream.fill();

                // Bordure du conteneur
                contentStream.setStrokingColor(primaryColor);
                contentStream.setLineWidth(2);
                contentStream.addRect(20, 100, 360, 400);
                contentStream.stroke();

                // Section Événement
                drawEventSection(contentStream, dto, darkText, primaryColor);

                // Séparateur
                contentStream.setStrokingColor(new Color(230, 230, 240));
                contentStream.setLineWidth(1);
                contentStream.moveTo(40, 380);
                contentStream.lineTo(360, 380);
                contentStream.stroke();

                // Section Participant
                drawParticipantSection(contentStream, dto, darkText, primaryColor);

                // QR Code
                drawQrCodeSection(contentStream, document, dto);

                // Référence
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 9);
                contentStream.setNonStrokingColor(darkText);
                String ref = "REF: " + dto.getReferenceTransaction();
                float refWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(ref) / 1000 * 9;
                contentStream.beginText();
                contentStream.newLineAtOffset(200 - (refWidth / 2), 120);
                contentStream.showText(ref);
                contentStream.endText();

                // Bouton de validation avec plus d'espace
                drawValidationButton(contentStream, accentColor);

                // Footer
                contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 7);
                contentStream.setNonStrokingColor(darkText);
                String footerText = "Présentez ce ticket à l'entrée • Valable une seule fois • Non transférable";
                float textWidth = PDType1Font.HELVETICA_OBLIQUE.getStringWidth(footerText) / 1000 * 7;
                contentStream.beginText();
                contentStream.newLineAtOffset((400 - textWidth) / 2, 40);
                contentStream.showText(footerText);
                contentStream.endText();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

    private void drawEventSection(PDPageContentStream contentStream, BilletParticipantDTO dto, Color textColor, Color accentColor) throws IOException, ParseException {
        // En-tête de section
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.setNonStrokingColor(accentColor);
        contentStream.beginText();
        contentStream.newLineAtOffset(40, 460);
        contentStream.showText("ÉVÉNEMENT: " + dto.getEventNom());
        contentStream.endText();

        // Détails de l'événement
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.setNonStrokingColor(textColor);

        // Date et heure
        contentStream.beginText();
        contentStream.newLineAtOffset(40, 435);
        contentStream.showText("Date:");
        contentStream.endText();

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(80, 435);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH'h'mm", Locale.FRENCH);
        String dateStr = dateFormat.format(dto.getEventDate());
        String timeStr = "à " + (dto.getHeure() != null ? timeFormat.format(new SimpleDateFormat("HH:mm").parse(dto.getHeure())) : "");
        contentStream.showText(dateStr + " " + timeStr);
        contentStream.endText();

        // Lieu
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(40, 415);
        contentStream.showText("Lieu:");
        contentStream.endText();

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(80, 415);
        contentStream.showText(dto.getEventLieu());
        contentStream.endText();

        // Type de billet
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(40, 395);
        contentStream.showText("Type:");
        contentStream.endText();

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(80, 395);
        contentStream.showText(dto.getTypeBillet());
        contentStream.endText();
    }

    private void drawParticipantSection(PDPageContentStream contentStream, BilletParticipantDTO dto, Color textColor, Color accentColor) throws IOException {
        // En-tête de section
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.setNonStrokingColor(accentColor);
        contentStream.beginText();
        contentStream.newLineAtOffset(40, 350);
        contentStream.showText("PARTICIPANT");
        contentStream.endText();

        // Prenom
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.setNonStrokingColor(textColor);
        contentStream.beginText();
        contentStream.newLineAtOffset(40, 325);
        contentStream.showText("Prénom:");
        contentStream.endText();

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(80, 325);
        String fullName = dto.getParticipantPrenom();
        contentStream.showText(fullName);
        contentStream.endText();

        // Nom
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.setNonStrokingColor(textColor);
        contentStream.beginText();
        contentStream.newLineAtOffset(40, 305);
        contentStream.showText("Nom:");
        contentStream.endText();

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(80, 305);
        String Name = dto.getParticipantNom();
        contentStream.showText(Name);
        contentStream.endText();

        // Email
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(40, 285);
        contentStream.showText("Email:");
        contentStream.endText();

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(80, 285);
        contentStream.showText(dto.getParticipantEmail());
        contentStream.endText();

        // Quantité
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(40, 265);
        contentStream.showText("Quantité:");
        contentStream.endText();

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(95, 265);
        contentStream.showText(String.valueOf(dto.getQuantite()));
        contentStream.endText();
    }

    private void drawQrCodeSection(PDPageContentStream contentStream, PDDocument document, BilletParticipantDTO dto) throws IOException {
        // Zone QR entourée
        contentStream.setNonStrokingColor(Color.WHITE);
        contentStream.addRect(220, 250, 140, 140);
        contentStream.fill();

        contentStream.setStrokingColor(new Color(220, 220, 230));
        contentStream.setLineWidth(1);
        contentStream.addRect(220, 250, 140, 140);
        contentStream.stroke();

        // QR code
        try {
            URL url = new URL(dto.getQrCodeUrl());
            BufferedImage qrImage = ImageIO.read(url);
            PDImageXObject pdImage = LosslessFactory.createFromImage(document, qrImage);
            contentStream.drawImage(pdImage, 230, 260, 120, 120);
        } catch (Exception e) {
            // Fallback en cas d'erreur
            contentStream.setFont(PDType1Font.HELVETICA, 8);
            contentStream.setNonStrokingColor(Color.RED);
            contentStream.beginText();
            contentStream.newLineAtOffset(240, 310);
            contentStream.showText("QR Code indisponible");
            contentStream.endText();
        }
    }

    private void drawValidationButton(PDPageContentStream contentStream, Color buttonColor) throws IOException {
        // Espace entre la référence et le bouton
        float buttonY = 70;  // Position Y augmentée pour plus d'espace
        float buttonWidth = 140;
        float buttonHeight = 45;  // Hauteur augmentée
        float buttonX = 200 - (buttonWidth / 2);

        // Ombre du bouton pour effet 3D
        contentStream.setNonStrokingColor(new Color(100, 100, 100, 50));
        contentStream.addRect(buttonX + 3, buttonY - 3, buttonWidth, buttonHeight);
        contentStream.fill();

        // Fond du bouton
        contentStream.setNonStrokingColor(buttonColor);
        contentStream.addRect(buttonX, buttonY, buttonWidth, buttonHeight);
        contentStream.fill();

        // Bordure légère pour un style plus soigné
        contentStream.setStrokingColor(new Color(200, 50, 0));
        contentStream.setLineWidth(1.5f);
        contentStream.addRect(buttonX, buttonY, buttonWidth, buttonHeight);
        contentStream.stroke();

        // Texte du bouton
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
        contentStream.setNonStrokingColor(Color.WHITE);
        String valideText = "VALIDE";
        float textWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(valideText) / 1000 * 18;
        contentStream.beginText();
        contentStream.newLineAtOffset(200 - (textWidth / 2), buttonY + 15);  // Position Y centrée dans le bouton
        contentStream.showText(valideText);
        contentStream.endText();
    }

    // Méthode utilitaire pour centrer du texte
    private void drawCenteredText(PDPageContentStream contentStream, String text, PDType1Font font, float fontSize, float x, float y, Color color) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        contentStream.setFont(font, fontSize);
        contentStream.setNonStrokingColor(color);
        contentStream.beginText();
        contentStream.newLineAtOffset(x - (textWidth / 2), y);
        contentStream.showText(text);
        contentStream.endText();
    }

    public BilletParticipantDTO getBilletDetails(Long billetId, String token) {
        // Décoder le token JWT
        String email = jwtUtil.getUsernameFromToken(token.replace("Bearer ", ""));

        BilletAcheter billet = billetAcheterRepository.findById(billetId)
                .orElseThrow(() -> new ResourceNotFoundException("Billet non trouvé"));

        // Vérifier l'appartenance
        if (!billet.getParticipant().getEmail().equals(email)) {
            throw new AccessDeniedException("Accès non autorisé à ce billet");
        }

        return convertToFullDTO(billet);
    }

    private BilletParticipantDTO convertToFullDTO(BilletAcheter billet) {
        BilletParticipantDTO dto = new BilletParticipantDTO();
        Evenement event = billet.getEvenement();
        Utilisateur participant = billet.getParticipant();

        dto.setEventNom(event.getNom());
        dto.setEventLieu(event.getLieu());
        dto.setEventDate(event.getDate());
        dto.setHeure(event.getHeure());
        dto.setTypeBillet(billet.getBillet().getTypeBillet());
        dto.setQuantite(billet.getQuantite());
        dto.setMontantTotal(billet.getMontantTotal());
        dto.setStatut(calculerStatut(billet));
        dto.setReferenceTransaction(billet.getReferenceTransaction());
        dto.setParticipantNom(participant.getNom());
        dto.setParticipantPrenom(participant.getPrenom());
        dto.setParticipantEmail(participant.getEmail());
        dto.setQrCodeUrl(generateQrUrl(billet.getReferenceTransaction()));

        return dto;
    }

    private String generateQrUrl(String reference) {
        return "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data="
                + URLEncoder.encode(reference, StandardCharsets.UTF_8);
    }
}