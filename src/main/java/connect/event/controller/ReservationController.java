package connect.event.controller;

import connect.event.dto.ReservationDTO;
import connect.event.entity.Reservation;
import connect.event.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping("/reserver")
    public ResponseEntity<Reservation> reserverLieu(@Valid @RequestBody ReservationDTO reservationDTO) {
        Reservation reservation = reservationService.reserverLieu(reservationDTO);
        return ResponseEntity.ok(reservation);
    }
}
