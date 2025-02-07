package connect.event.equipeMarketing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor @AllArgsConstructor
@Entity
public class Conversion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConversion;

    @Column(nullable = false)
    private LocalDateTime dateConversion;

    @Column(nullable = false)
    private String type; // Ex: "clic", "inscription", "achat"

    @ManyToOne
    @JoinColumn(name = "idCampagne", nullable = false)
    private CampagneMarketing campagne;


}
