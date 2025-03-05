package connect.event.equipeMarketing.controller;

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
@CrossOrigin(origins = "*")
public class CampagneMarketingUpdateController {
    @Autowired
    private CampagneMarketingServiceUpdate campagneService;

    @PostMapping
    public ResponseEntity<CampagneMarketingUpdate> creerCampagne(@RequestBody CampagneMarketingUpdate campagne) {
        return ResponseEntity.status(HttpStatus.CREATED).body(campagneService.creerCampagne(campagne));
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
}
