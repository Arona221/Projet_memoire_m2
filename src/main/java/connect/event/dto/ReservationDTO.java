package connect.event.dto;
import connect.event.entity.Reservation;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private Long idOrganisateur;
    private Long idRessource;
    private LocalDate date;
    private LocalTime heure;

    public static ReservationDTO fromEntity(Reservation reservation) {
        return new ReservationDTO(
                reservation.getId(),
                reservation.getOrganisateur().getIdUtilisateur(),
                reservation.getRessource().getId(),
                reservation.getDate(),
                reservation.getHeure()
        );
    }
}
