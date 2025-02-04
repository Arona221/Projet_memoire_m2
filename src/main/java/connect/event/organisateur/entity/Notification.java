package connect.event.organisateur.entity;

import connect.event.entity.Utilisateur;
import connect.event.organisateur.enums.TypeNotification;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotification;
    private LocalDate date;
    @Column(columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String contenu;



    @Enumerated(EnumType.STRING)
    private TypeNotification typeNotification;

    @ManyToOne
    @JoinColumn(name = "idParticipant", nullable = false)
    private Utilisateur participant;


}