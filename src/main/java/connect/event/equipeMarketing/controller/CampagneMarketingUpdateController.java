package connect.event.equipeMarketing.controller;

import connect.event.equipeMarketing.DTO.PlanificationRequest;
import connect.event.equipeMarketing.emuns.StatutCampagne;
import connect.event.equipeMarketing.entity.CampagneMarketingUpdate;
import connect.event.equipeMarketing.service.CampagneMarketingServiceUpdate;
import connect.event.exception.CampagneNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping(value = "/campagnes", produces = "application/json")
@CrossOrigin(origins = "http://localhost:4200")
public class CampagneMarketingUpdateController {
    @Autowired
    private CampagneMarketingServiceUpdate campagneService;

    @PostMapping
    public ResponseEntity<?> creerCampagne(@RequestBody CampagneMarketingUpdate campagne) {
        if (campagne.getMessage() == null || campagne.getSegment() == null) {
            return ResponseEntity.badRequest().body("Message et segment doivent être fournis.");
        }
        return ResponseEntity.ok(campagneService.creerCampagne(campagne));
    }


    @GetMapping
    public ResponseEntity<List<CampagneMarketingUpdate>> getAllCampagnes() {
        return ResponseEntity.ok(campagneService.getAllCampagnes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampagneMarketingUpdate> getCampagneById(@PathVariable Long id) {
        return ResponseEntity.ok(campagneService.getCampagneById(id));
    }
    @PostMapping("/{id}/publier")
    public ResponseEntity<String> publierCampagne(@PathVariable Long id) {
        try {
            campagneService.publierCampagne(id);
            return ResponseEntity.ok("Campagne publiée avec succès");
        } catch (CampagneNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Erreur lors de la publication de la campagne", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la publication de la campagne: " + e.getMessage());
        }
    }
    @PostMapping("/{id}/terminer")
    public ResponseEntity<String> terminerCampagne(@PathVariable Long id) {
        try {
            campagneService.terminerCampagne(id);
            return ResponseEntity.ok("Campagne terminée avec succès");
        } catch (CampagneNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la terminaison de la campagne: " + e.getMessage());
        }
    }

    /**
     * Planifier une campagne avec une date et heure de publication.
     */
    @PostMapping("/{id}/planifier")
    public ResponseEntity<String> planifierCampagne(
            @PathVariable Long id,
            @RequestBody PlanificationRequest request
    ) {
        campagneService.planifierPublication(id, request.getDate(), request.getHeure());
        return ResponseEntity.ok("✅ Campagne planifiée avec succès pour le " + request.getDate() + " à " + request.getHeure());
    }

    /**
     * Récupérer toutes les campagnes planifiées pour une publication future.
     */
    @GetMapping("/planifiees")
    public ResponseEntity<List<CampagneMarketingUpdate>> getCampagnesPlanifiees() {
        return ResponseEntity.ok(campagneService.getCampagnesPlanifiees());
    }


    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<CampagneMarketingUpdate>> getCampagnesByStatut(
            @PathVariable String statut) {

        StatutCampagne statutCampagne;
        try {
            statutCampagne = StatutCampagne.valueOf(statut);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(campagneService.getCampagnesByStatut(statutCampagne));
    }
}
