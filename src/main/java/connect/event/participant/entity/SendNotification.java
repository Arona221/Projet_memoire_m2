package connect.event.participant.entity;

import connect.event.entity.Evenement;
import connect.event.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "send_notification")
public class SendNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "evenement_id", nullable = false)
    private Evenement evenement;

    @Column(nullable = false)
    private String typeNotification; // "BILLET" ou "FAVORI"

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    private LocalDateTime dateEnvoi;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;
}