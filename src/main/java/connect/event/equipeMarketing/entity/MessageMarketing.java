package connect.event.equipeMarketing.entity;

import connect.event.entity.Evenement;
import connect.event.equipeMarketing.emuns.Canal;
import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MessageMarketing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMessage;

    @Column(nullable = false)
    private String contenu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Canal canal;

    @ManyToOne
    @JoinColumn(name = "idEvenement", nullable = false)
    private Evenement evenement;
}

