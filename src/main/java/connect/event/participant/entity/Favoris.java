package connect.event.participant.entity;

import connect.event.entity.Evenement;
import connect.event.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "favoris")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favoris {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFavoris;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "id_evenement", nullable = false)
    private Evenement evenement;
}
