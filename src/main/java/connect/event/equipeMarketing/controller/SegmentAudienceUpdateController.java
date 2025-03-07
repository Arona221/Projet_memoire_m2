package connect.event.equipeMarketing.controller;

import connect.event.equipeMarketing.entity.Contact;
import connect.event.equipeMarketing.entity.SegmentAudienceUpdate;
import connect.event.equipeMarketing.service.SegmentAudienceUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/segments/update")
@CrossOrigin(origins = "http://localhost:4200")
public class SegmentAudienceUpdateController {

    @Autowired
    private SegmentAudienceUpdateService segmentService;

    /**
     * Crée un nouveau segment.
     *
     * @param segment Le segment à créer.
     * @return Le segment créé.
     */
    @PostMapping
    public ResponseEntity<SegmentAudienceUpdate> creerSegment(@RequestBody SegmentAudienceUpdate segment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(segmentService.creerSegment(segment));
    }

    /**
     * Récupère tous les segments.
     *
     * @return Une liste de tous les segments.
     */
    @GetMapping
    public ResponseEntity<List<SegmentAudienceUpdate>> getAllSegments() {
        return ResponseEntity.ok(segmentService.getAllSegments());
    }

    /**
     * Récupère un segment par son ID.
     *
     * @param id L'ID du segment.
     * @return Le segment correspondant.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SegmentAudienceUpdate> getSegmentById(@PathVariable Long id) {
        return ResponseEntity.ok(segmentService.getSegmentById(id));
    }

    /**
     * Ajoute un contact à un segment.
     *
     * @param segmentId L'ID du segment.
     * @param contact   Le contact à ajouter.
     * @return Le segment mis à jour.
     */
    @PostMapping("/{segmentId}/contacts")
    public ResponseEntity<SegmentAudienceUpdate> ajouterContactAuSegment(
            @PathVariable Long segmentId,
            @RequestBody Contact contact) {
        return ResponseEntity.ok(segmentService.ajouterContactAuSegment(segmentId, contact));
    }

    /**
     * Supprime un contact d'un segment.
     *
     * @param segmentId L'ID du segment.
     * @param contactId L'ID du contact à supprimer.
     * @return Le segment mis à jour.
     */
    @DeleteMapping("/{segmentId}/contacts/{contactId}")
    public ResponseEntity<SegmentAudienceUpdate> supprimerContactDuSegment(
            @PathVariable Long segmentId,
            @PathVariable Long contactId) {
        return ResponseEntity.ok(segmentService.supprimerContactDuSegment(segmentId, contactId));
    }
}

