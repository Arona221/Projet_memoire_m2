package connect.event.participant.controller;

import connect.event.participant.DTO.*;
import connect.event.participant.service.BilletAcheterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/billets")
@CrossOrigin(origins = "http://localhost:4200")
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

    @GetMapping("/participant/{participantId}")
    public ResponseEntity<List<BilletParticipantDTO>> getBilletsParticipant(
            @PathVariable Long participantId) {
        List<BilletParticipantDTO> billets = billetAcheterService.getBilletsByParticipant(participantId);
        return ResponseEntity.ok(billets);
    }

    @PostMapping("/annuler/{billetId}")
    public ResponseEntity<?> annulerBillet(
            @PathVariable Long billetId,
            @RequestParam Long participantId) {
        return billetAcheterService.annulerBillet(billetId, participantId);
    }
    @GetMapping("/{billetId}/generate-ticket")
    public ResponseEntity<byte[]> generateTicketById(
            @PathVariable Long billetId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        try {
            BilletParticipantDTO dto = billetAcheterService.getBilletDetails(billetId, token);
            byte[] pdfBytes = billetAcheterService.generateTicketPdf(dto);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=ticket-"+dto.getReferenceTransaction()+".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Erreur génération PDF: " + e.getMessage()).getBytes());
        }
    }

    // BilletController.java
    @PreAuthorize("hasRole('ORGANISATEUR')")
    @GetMapping("/organizer/{organizerId}/events")
    public ResponseEntity<List<EventParticipantsDTO>> getOrganizerEventsWithParticipants(
            @PathVariable Long organizerId) { // Retirer le paramètre token
        return billetAcheterService.getOrganizerEventsWithParticipants(organizerId);
    }

    @PreAuthorize("hasRole('ORGANISATEUR')")
    @GetMapping("/event/{eventId}/participants")
    public ResponseEntity<List<ParticipantDetailsDTO>> getEventParticipants(
            @PathVariable Long eventId) { // Retirer le paramètre token
        return billetAcheterService.getEventParticipants(eventId);
    }
}