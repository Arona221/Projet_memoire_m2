package connect.event.equipeMarketing.controller;

import connect.event.equipeMarketing.entity.CampagneMarketing;
import connect.event.equipeMarketing.service.CampagneMarketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/campagnes")
public class CampagneMarketingController {

    @Autowired
    private CampagneMarketingService campagneService;

    @PostMapping
    public CampagneMarketing creerCampagne(@RequestBody CampagneMarketing campagne) {
        return campagneService.creerCampagne(campagne);
    }

    @GetMapping("/{id}")
    public CampagneMarketing getCampagne(@PathVariable Long id) {
        return campagneService.getCampagneById(id);
    }

    @PostMapping("/{id}/envoyer")
    public void envoyerCampagne(@PathVariable Long id) {
        campagneService.envoyerCampagne(id);
    }
}
