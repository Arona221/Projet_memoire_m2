package connect.event.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Billet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Clé primaire unique pour chaque billet

    private String typeBillet; // Exemple: VIP, Standard, Étudiant
    private BigDecimal prix;   // Prix du billet
    private Integer quantite;  // Quantité disponible

    @ManyToOne
    @JoinColumn(name = "evenement_id", nullable = false) // Association à l'événement parent
    private Evenement evenement;
}
