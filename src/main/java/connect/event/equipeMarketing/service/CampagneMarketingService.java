package connect.event.equipeMarketing.service;

import connect.event.admin.service.EmailService;
import connect.event.equipeMarketing.entity.CampagneMarketing;
import connect.event.equipeMarketing.repository.CampagneMarketingRepository;
import connect.event.equipeMarketing.repository.MessageMarketingRepository;
import connect.event.equipeMarketing.repository.SegmentAudienceRepository;
import connect.event.repository.EvenementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CampagneMarketingService {
    private static final Logger log = LoggerFactory.getLogger( CampagneMarketingService.class);

    @Autowired
    private CampagneMarketingRepository campagneRepository;

    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private MessageMarketingRepository messageRepository;

    @Autowired
    private SegmentAudienceRepository segmentRepository;

    @Autowired
    private EmailServiceMailchimp emailServiceMailchimp;

    public CampagneMarketing creerCampagne(CampagneMarketing campagne) {
        log.info("Création campagne - Données reçues: {}", campagne);
        log.info("Message ID: {}", (campagne.getMessage() != null) ? campagne.getMessage().getIdMessage() : "null");
        log.info("Segment ID: {}", (campagne.getSegment() != null) ? campagne.getSegment().getIdSegment() : "null");
        if (campagne.getEvenement() == null || campagne.getEvenement().getId_evenement() == null) {
            throw new IllegalArgumentException("L'événement ne peut pas être null");
        }

        if (campagne.getMessage() == null || campagne.getMessage().getIdMessage() == null) {
            throw new IllegalArgumentException("Le message marketing ne peut pas être null");
        }

        if (campagne.getSegment() == null || campagne.getSegment().getIdSegment() == null) {
            throw new IllegalArgumentException("Le segment d'audience ne peut pas être null");
        }

        // Récupérer les entités associées
        campagne.setEvenement(evenementRepository.findById(campagne.getEvenement().getId_evenement())
                .orElseThrow(() -> new IllegalArgumentException("Événement introuvable")));

        campagne.setMessage(messageRepository.findById(campagne.getMessage().getIdMessage())
                .orElseThrow(() -> new IllegalArgumentException("Message marketing introuvable")));

        campagne.setSegment(segmentRepository.findById(campagne.getSegment().getIdSegment())
                .orElseThrow(() -> new IllegalArgumentException("Segment d'audience introuvable")));

        return campagneRepository.save(campagne);
    }


    public CampagneMarketing getCampagneById(Long id) {
        return campagneRepository.findById(id).orElse(null);
    }

    public void envoyerCampagne(Long id) {
        CampagneMarketing campagne = campagneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Campagne introuvable"));

        // Récupérer les détails de la campagne
        String message = campagne.getMessage().getContenu();
        List<String> criteres = campagne.getSegment().getCriteres();

        // Construire le message final
        String messageFinal = message + "\n\nDétails de l'événement :\n"
                + "Nom : " + campagne.getEvenement().getNom() + "\n"
                + "Lieu : " + campagne.getEvenement().getLieu() + "\n"
                + "Date : " + campagne.getEvenement().getDate() + "\n";

        // Envoyer l'email via Mailchimp ou un autre service
        emailServiceMailchimp.envoyerEmail(criteres, messageFinal);

        System.out.println("Campagne envoyée avec succès !");
    }
}
