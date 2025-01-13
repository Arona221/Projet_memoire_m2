package connect.event.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservationDTO {

    @NotNull(message = "L'identifiant du lieu est obligatoire")
    private Long idLieu;

    @NotNull(message = "L'identifiant de l'utilisateur est obligatoire")
    private Long idUtilisateur;

    @NotNull(message = "La date de réservation est obligatoire")
    private LocalDate dateReservation;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime heureFin;
}
