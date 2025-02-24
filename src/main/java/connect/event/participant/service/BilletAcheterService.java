    package connect.event.participant.service;

    import connect.event.entity.*;
    import connect.event.participant.DTO.*;
    import connect.event.participant.entity.BilletAcheter;
    import connect.event.participant.repository.BilletAcheterRepository;
    import connect.event.repository.BilletRepository;
    import connect.event.repository.EvenementRepository;
    import connect.event.repository.UtilisateurRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.math.BigDecimal;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.UUID;
    import java.util.logging.Logger;

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

                    BilletAcheter billetAcheter = new BilletAcheter();
                    billetAcheter.setQuantite(item.getQuantite());
                    billetAcheter.setMontantTotal(sousTotal);
                    billetAcheter.setStatutPaiement("EN_ATTENTE");
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
    }
