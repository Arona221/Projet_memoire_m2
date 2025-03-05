package connect.event.equipeMarketing.service;

import connect.event.equipeMarketing.entity.Contact;
import connect.event.equipeMarketing.entity.SegmentAudienceUpdate;
import connect.event.equipeMarketing.repository.ContactRepository;
import connect.event.equipeMarketing.repository.SegmentAudienceUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SegmentAudienceUpdateService {

    @Autowired
    private SegmentAudienceUpdateRepository segmentRepository;

    @Autowired
    private ContactRepository contactRepository;

    /**
     * Crée un nouveau segment avec des contacts.
     *
     * @param segment Le segment à créer.
     * @return Le segment créé.
     */
    public SegmentAudienceUpdate creerSegment(SegmentAudienceUpdate segment) {
        // Sauvegarder le segment
        SegmentAudienceUpdate savedSegment = segmentRepository.save(segment);

        // Associer les contacts au segment
        for (Contact contact : savedSegment.getContacts()) {
            contact.setSegment(savedSegment);
            contactRepository.save(contact);
        }

        return savedSegment;
    }

    /**
     * Récupère tous les segments.
     *
     * @return Une liste de tous les segments.
     */
    public List<SegmentAudienceUpdate> getAllSegments() {
        return segmentRepository.findAll();
    }

    /**
     * Récupère un segment par son ID.
     *
     * @param id L'ID du segment.
     * @return Le segment correspondant.
     * @throws RuntimeException Si le segment n'est pas trouvé.
     */
    public SegmentAudienceUpdate getSegmentById(Long id) {
        return segmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Segment non trouvé avec l'ID: " + id));
    }

    /**
     * Ajoute un contact à un segment.
     *
     * @param segmentId L'ID du segment.
     * @param contact   Le contact à ajouter.
     * @return Le segment mis à jour.
     */
    public SegmentAudienceUpdate ajouterContactAuSegment(Long segmentId, Contact contact) {
        SegmentAudienceUpdate segment = getSegmentById(segmentId);
        contact.setSegment(segment);
        segment.getContacts().add(contact);
        return segmentRepository.save(segment);
    }

    /**
     * Supprime un contact d'un segment.
     *
     * @param segmentId L'ID du segment.
     * @param contactId L'ID du contact à supprimer.
     * @return Le segment mis à jour.
     */
    public SegmentAudienceUpdate supprimerContactDuSegment(Long segmentId, Long contactId) {
        SegmentAudienceUpdate segment = getSegmentById(segmentId);
        segment.getContacts().removeIf(contact -> contact.getId().equals(contactId));
        return segmentRepository.save(segment);
    }
}