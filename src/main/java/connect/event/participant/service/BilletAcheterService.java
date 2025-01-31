package connect.event.participant.service;

import com.google.zxing.WriterException;
import connect.event.participant.DTO.BilletSelectionDTO;
import connect.event.participant.DTO.FactureResponse;
import connect.event.participant.DTO.PaiementResponse;
import connect.event.participant.entity.BilletAcheter;
import connect.event.entity.Billet;
import connect.event.entity.Evenement;
import connect.event.entity.Utilisateur;
import connect.event.participant.repository.BilletAcheterRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BilletAcheterService {

    @Autowired
    private PayDunyaService payDunyaService;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private connect.event.repository.UtilisateurRepository utilisateurRepository;

    @Autowired
    private connect.event.repository.BilletRepository billetRepository;

    @Autowired
    private BilletAcheterRepository billetAcheterRepository;

    public ResponseEntity<FactureResponse> acheterBillet(BilletSelectionDTO billetDTO) {
        try {
            // Validation et récupération des informations du participant et du billet
            Optional<Utilisateur> optionalParticipant = utilisateurRepository.findById(billetDTO.getParticipantId());
            Optional<Billet> optionalBillet = billetRepository.findById(billetDTO.getBilletId());

            if (optionalParticipant.isEmpty() || optionalBillet.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new FactureResponse("error", "Participant ou Billet non trouvé", null));
            }

            Utilisateur participant = optionalParticipant.get();
            Billet billet = optionalBillet.get();

            // Calcul du montant total
            BigDecimal montantTotal = billet.getPrix().multiply(BigDecimal.valueOf(billetDTO.getQuantite()));

            // Créer et sauvegarder l'achat du billet
            BilletAcheter billetAcheter = new BilletAcheter();
            billetAcheter.setQuantite(billetDTO.getQuantite());
            billetAcheter.setMontantTotal(montantTotal);
            billetAcheter.setStatutPaiement("EN_ATTENTE");
            billetAcheter.setParticipant(participant);
            billetAcheter.setBillet(billet);
            billetAcheter.setEvenement(new Evenement(billetDTO.getEvenementId()));

            billetAcheter = billetAcheterRepository.save(billetAcheter);

            // Création de la facture PayDunya
            String paiementUrl = payDunyaService.createInvoice(billetDTO.getBilletId(), billetDTO.getQuantite(), "http://callback.com");

            // Retourner l'URL de la facture PayDunya pour le paiement
            return ResponseEntity.ok(new FactureResponse("success", "Facture PayDunya créée avec succès.", paiementUrl));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new FactureResponse("error", "Erreur lors de l'achat du billet : " + e.getMessage(), null));
        }
    }

    public ResponseEntity<PaiementResponse> verifierPaiement(String referenceTransaction) {
        try {
            Optional<BilletAcheter> billetAcheterOpt = billetAcheterRepository.findByReferenceTransaction(referenceTransaction);

            if (billetAcheterOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PaiementResponse("error", "Billet non trouvé pour cette transaction.", null));
            }

            BilletAcheter billetAcheter = billetAcheterOpt.get();
            if (billetAcheter.getStatutPaiement().equals("PAYE")) {
                return ResponseEntity.ok(new PaiementResponse("success", "Paiement déjà validé.", billetAcheter));
            }

            boolean paiementValide = payDunyaService.verifierPaiement(referenceTransaction);

            if (paiementValide) {
                billetAcheter.setStatutPaiement("PAYE");
                billetAcheterRepository.save(billetAcheter);

                // Générer le QR Code
                byte[] qrCode = qrCodeService.generateQRCode(referenceTransaction, 200, 200);

                // Envoyer l'email de confirmation
                emailService.sendBilletEmail(
                        billetAcheter.getParticipant().getEmail(),
                        billetAcheter.getParticipant().getNom(),
                        billetAcheter.getEvenement().getNom(),
                        billetAcheter.getQuantite(),
                        billetAcheter.getMontantTotal(),
                        referenceTransaction,
                        qrCode
                );

                return ResponseEntity.ok(new PaiementResponse("success", "Paiement validé avec succès.", billetAcheter));
            } else {
                billetAcheter.setStatutPaiement("ANNULE");
                billetAcheterRepository.save(billetAcheter);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PaiementResponse("error", "Le paiement a échoué.", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PaiementResponse("error", "Erreur lors de la vérification du paiement : " + e.getMessage(), null));
        }
    }
}