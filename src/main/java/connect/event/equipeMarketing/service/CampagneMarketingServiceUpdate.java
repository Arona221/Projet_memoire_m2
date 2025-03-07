package connect.event.equipeMarketing.service;

import connect.event.entity.Evenement;
import connect.event.equipeMarketing.emuns.Canal;
import connect.event.equipeMarketing.emuns.StatutCampagne;
import connect.event.equipeMarketing.entity.CampagneMarketingUpdate;
import connect.event.equipeMarketing.entity.Contact;
import connect.event.equipeMarketing.entity.MessageMarketingUpdate;
import connect.event.equipeMarketing.entity.SegmentAudienceUpdate;
import connect.event.equipeMarketing.repository.CampagneMarketingUpadateRepository;
import connect.event.equipeMarketing.repository.ContactRepository;
import connect.event.equipeMarketing.repository.MessageMarketingUpdateRepository;
import connect.event.equipeMarketing.repository.SegmentAudienceUpdateRepository;
import connect.event.exception.CampagneNotFoundException;
import connect.event.repository.EvenementRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CampagneMarketingServiceUpdate {
    private static final Logger logger = LoggerFactory.getLogger(CampagneMarketingServiceUpdate.class);
    private static final Logger log = LoggerFactory.getLogger( CampagneMarketingServiceUpdate.class);

    @Autowired
    private CampagneMarketingUpadateRepository campagneRepository;

    @Autowired
    private FacebookAdsService facebookService;

    @Autowired
    private GoogleAdsService googleService;

    @Autowired
    private MailchimpService mailchimpService;
    @Autowired
    private EvenementRepository evenementRepository; // Ajoutez ce repository

    @Autowired
    private MessageMarketingUpdateRepository messageRepository; // Ajoutez ce repository

    @Autowired
    private SegmentAudienceUpdateRepository segmentRepository; // Ajoutez ce repository
    @Autowired
    private MessageMarketingUpdateService messageService;


    /**
     * Crée une nouvelle campagne marketing.
     *
     * @param campagne La campagne à créer.
     * @return La campagne créée.
     * @throws IllegalArgumentException Si la campagne n'a pas de message, de segment ou d'événement.
     */
    @Transactional
    public CampagneMarketingUpdate creerCampagne(CampagneMarketingUpdate campagne) {
        log.info("Création campagne - Données reçues: {}", campagne);
        log.info("Message ID: {}", (campagne.getMessage() != null) ? campagne.getMessage().getIdMessage() : "null");
        log.info("Segment ID: {}", (campagne.getSegment() != null) ? campagne.getSegment().getIdSegment() : "null");
        // Validation des champs obligatoires
        if (campagne.getMessage() == null || campagne.getSegment() == null || campagne.getEvenement() == null) {
            throw new IllegalArgumentException("La campagne doit avoir un message, un segment et un événement");
        }
        if (campagne.getNom() == null || campagne.getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la campagne est obligatoire");
        }
        if (campagne.getBudget() <= 0) {
            throw new IllegalArgumentException("Le budget doit être supérieur à 0");
        }
        if (campagne.getDateDebut() == null || campagne.getDateFin() == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires");
        }
        if (campagne.getDateDebut().isAfter(campagne.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }
        if (campagne.getSegment() == null || campagne.getSegment().getIdSegment() == null) {
            throw new IllegalArgumentException("L'ID du segment est requis");
        }

        // Charger l'événement, le message et le segment
        Evenement evenement = evenementRepository.findById(campagne.getEvenement().getId_evenement())
                .orElseThrow(() -> new IllegalArgumentException("Événement non trouvé"));
        MessageMarketingUpdate message = messageRepository.findById(campagne.getMessage().getIdMessage())
                .orElseThrow(() -> new IllegalArgumentException("Message non trouvé"));
        SegmentAudienceUpdate segment = segmentRepository.findById(campagne.getSegment().getIdSegment())
                .orElseThrow(() -> new IllegalArgumentException("Segment non trouvé"));

        campagne.setEvenement(evenement);
        campagne.setMessage(message);
        campagne.setSegment(segment);

        // Initialisation du statut si non défini
        if (campagne.getStatut() == null) {
            campagne.setStatut(StatutCampagne.Planifier);
        }

        // Sauvegarde de la campagne
        return campagneRepository.save(campagne);
    }
    @Autowired
    private ContactRepository contactRepository;

    @Transactional
    public CampagneMarketingUpdate creerCampagneAvecMessage(Long idEvenement) {
        // Récupérer l'événement
        Evenement evenement = evenementRepository.findById(idEvenement)
                .orElseThrow(() -> new IllegalArgumentException("Événement non trouvé"));

        // Récupérer les contacts (par exemple, tous les contacts ou ceux d'un segment spécifique)
        List<Contact> contacts = contactRepository.findAll(); // Ou une autre logique de récupération

        // Créer un nouveau message
        MessageMarketingUpdate message = new MessageMarketingUpdate();

        // Générer le message personnalisé
        MessageMarketingUpdate messagePersonnalise = messageService.creerMessagePersonnalise(contacts, message, evenement);

        // Créer la campagne
        CampagneMarketingUpdate campagne = new CampagneMarketingUpdate();
        campagne.setEvenement(evenement);
        campagne.setMessage(messagePersonnalise);
        campagne.setStatut(StatutCampagne.Planifier);

        // Enregistrer la campagne en base de données
        return campagneRepository.save(campagne);
    }

    /**
     * Publie une campagne marketing en fonction du canal choisi.
     *
     * @param idCampagne L'ID de la campagne à publier.
     * @throws CampagneNotFoundException Si la campagne n'est pas trouvée.
     * @throws IllegalStateException      Si la campagne est déjà terminée ou n'a pas de message.
     * @throws RuntimeException          Si une erreur survient lors de la publication.
     */
    @Transactional
    public void publierCampagne(Long idCampagne) {
        logger.info("Tentative de publication de la campagne avec l'ID : {}", idCampagne);

        CampagneMarketingUpdate campagne = campagneRepository.findById(idCampagne)
                .orElseThrow(() -> new CampagneNotFoundException("Campagne non trouvée"));

        logger.info("Campagne trouvée : {}", campagne);

        MessageMarketingUpdate message = campagne.getMessage();
        Evenement evenement = campagne.getEvenement();
        List<Contact> contacts = campagne.getSegment().getContacts();

        if (message == null) {
            logger.error("La campagne n'a pas de message associé");
            throw new IllegalStateException("Impossible de publier une campagne sans message");
        }

        if (campagne.getStatut() == StatutCampagne.Terminee) {
            logger.error("La campagne est déjà terminée");
            throw new IllegalStateException("Impossible de publier une campagne déjà terminée");
        }

        try {
            logger.info("Canal de publication : {}", message.getCanal());

            switch (message.getCanal()) {
                case MAILCHIMP:
                    logger.info("Envoi d'e-mails via Mailchimp");
                    mailchimpService.envoyerCampagneMailchimp(contacts, message, evenement);
                    break;
                default:
                    logger.error("Canal non pris en charge : {}", message.getCanal());
                    throw new IllegalStateException("Canal non pris en charge : " + message.getCanal());
            }

            campagne.setStatut(StatutCampagne.En_cours);
            campagneRepository.save(campagne);
            logger.info("Campagne publiée avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la publication de la campagne", e);
            throw new RuntimeException("Erreur lors de la publication de la campagne: " + e.getMessage(), e);
        }
    }
    /**
     * Récupère toutes les campagnes marketing.
     *
     * @return Une liste de toutes les campagnes.
     */
    public List<CampagneMarketingUpdate> getAllCampagnes() {
        return campagneRepository.findAll();
    }

    /**
     * Récupère une campagne par son ID.
     *
     * @param id L'ID de la campagne.
     * @return La campagne correspondante.
     * @throws CampagneNotFoundException Si la campagne n'est pas trouvée.
     */
    public CampagneMarketingUpdate getCampagneById(Long id) {
        return campagneRepository.findById(id)
                .orElseThrow(() -> new CampagneNotFoundException("Campagne non trouvée avec l'ID: " + id));
    }

    /**
     * Termine une campagne marketing.
     *
     * @param id L'ID de la campagne à terminer.
     * @throws CampagneNotFoundException Si la campagne n'est pas trouvée.
     */
    @Transactional
    public void terminerCampagne(Long id) {
        CampagneMarketingUpdate campagne = getCampagneById(id);
        campagne.setStatut(StatutCampagne.Terminee);
        campagneRepository.save(campagne);
    }
}