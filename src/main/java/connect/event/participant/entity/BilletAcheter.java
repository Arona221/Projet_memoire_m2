package connect.event.participant.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import connect.event.entity.Billet;
import connect.event.entity.Evenement;
import connect.event.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "billet_acheter")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BilletAcheter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_evenement", nullable = false)
    @JsonBackReference
    private Evenement evenement;

    @ManyToOne
    @JoinColumn(name = "id_participant", nullable = false)
    private Utilisateur participant;

    @ManyToOne
    @JoinColumn(name = "id_billet", nullable = false)
    private Billet billet;

    @Column(nullable = false)
    private int quantite;

    @Column(nullable = false)
    private BigDecimal montantTotal;

    @Column(nullable = false)
    private String statutPaiement; // EN_ATTENTE, PAYE, ANNULE

    @Column
    private String referenceTransaction; // Référence de la transaction PayDunya
}
