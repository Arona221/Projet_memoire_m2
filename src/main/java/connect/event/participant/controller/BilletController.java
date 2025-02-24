package connect.event.participant.controller;

import connect.event.participant.DTO.BilletSelectionDTO;
import connect.event.participant.DTO.FactureResponse;
import connect.event.participant.DTO.PaiementResponse;
import connect.event.participant.service.BilletAcheterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/billets")
public class BilletController {

    @Autowired
    private BilletAcheterService billetAcheterService;

    @PostMapping("/acheter")
    public ResponseEntity<FactureResponse> acheterBillet(@Valid @RequestBody BilletSelectionDTO billetDTO) {
        return billetAcheterService.acheterBillet(billetDTO);
    }

    @GetMapping("/verifier/{reference}")
    public ResponseEntity<PaiementResponse> verifierPaiement(@PathVariable String reference) {
        return billetAcheterService.verifierPaiement(reference);
    }
}