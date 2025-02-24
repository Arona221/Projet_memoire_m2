package connect.event.controller;
import connect.event.dto.ReservationDTO;
import connect.event.enums.StatutReservation;
import connect.event.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ressources")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping("/reserver")
    public ResponseEntity<ReservationDTO> reserverRessource(
            @RequestParam Long idOrganisateur,
            @RequestParam Long idRessource,
            @RequestBody ReservationDTO reservationDTO) {
        return ResponseEntity.ok(reservationService.reserverRessource(idOrganisateur, idRessource, reservationDTO));
    }
    @PutMapping("/{id}/statut")
    public ResponseEntity<ReservationDTO> updateStatutReservation(
            @PathVariable Long id,
            @RequestParam StatutReservation statut,
            @RequestParam Long adminId) {
        return ResponseEntity.ok(
                reservationService.updateStatutReservation(id, statut, adminId)
        );
    }

    @GetMapping("/utilisateur/{id}")
    public ResponseEntity<List<ReservationDTO>> getReservationsParUtilisateur(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                reservationService.getReservationsByOrganisateur(id)
        );
    }
    @DeleteMapping("/{id}/annuler")
    public ResponseEntity<Void> annulerReservation(
            @PathVariable Long id,
            @RequestParam Long organisateurId) {
        reservationService.annulerReservation(id, organisateurId);
        return ResponseEntity.noContent().build();
    }


}

