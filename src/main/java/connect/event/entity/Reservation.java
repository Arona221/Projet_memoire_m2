package connect.event.entity;
import connect.event.admin.entity.Ressource;
import connect.event.enums.StatutReservation;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"idRessource", "date", "heure"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idOrganisateur", nullable = false)
    private Utilisateur organisateur;

    @ManyToOne
    @JoinColumn(name = "idRessource", nullable = false)
    private Ressource ressource;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;
}

