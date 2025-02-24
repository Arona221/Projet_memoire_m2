package connect.event.dto;
import connect.event.entity.Reservation;
import connect.event.enums.StatutReservation;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private Long idOrganisateur;
    private Long idRessource;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Double totalPrice;
    @Enumerated(EnumType.STRING)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    public static ReservationDTO fromEntity(Reservation reservation) {
        return new ReservationDTO(
                reservation.getId(),
                reservation.getOrganisateur().getIdUtilisateur(),
                reservation.getRessource().getId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getTotalPrice(),
                reservation.getStatut()
        );
    }

}