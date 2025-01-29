package connect.event.controller;
import connect.event.dto.ReservationDTO;
import connect.event.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping("/{idOrganisateur}/{idRessource}")
    public ResponseEntity<ReservationDTO> reserverRessource(
            @PathVariable Long idOrganisateur,
            @PathVariable Long idRessource,
            @RequestBody ReservationDTO reservationDTO) {
        return ResponseEntity.ok(reservationService.reserverRessource(idOrganisateur, idRessource, reservationDTO));
    }
}

