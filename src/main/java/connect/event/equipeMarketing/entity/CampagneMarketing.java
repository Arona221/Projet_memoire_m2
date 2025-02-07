package connect.event.equipeMarketing.entity;

import connect.event.entity.Evenement;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor @AllArgsConstructor
@Entity
public class CampagneMarketing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCampagne;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private LocalDateTime dateProgrammation;

    @ManyToOne
    @JoinColumn(name = "id_evenement", nullable = false)
    private Evenement evenement;


    @ManyToOne
    @JoinColumn(name = "idMessage", nullable = false)
    private MessageMarketing message;

    @ManyToOne
    @JoinColumn(name = "idSegment", nullable = false)
    private SegmentAudience segment;


}
