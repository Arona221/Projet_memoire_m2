package connect.event.participant.entity;

import connect.event.entity.Evenement;
import connect.event.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "favoris")
public class Favoris {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", referencedColumnName = "idUtilisateur")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "evenement_id", referencedColumnName = "id_evenement")
    private Evenement evenement;

    private LocalDateTime dateAjout = LocalDateTime.now();
}
