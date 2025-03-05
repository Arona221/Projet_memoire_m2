package connect.event.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(exclude = "evenement") // Exclure la référence à Evenement
public class Billet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String typeBillet;
    private BigDecimal prix;
    private Integer quantite;

    @ManyToOne
    @JoinColumn(name = "evenement_id", nullable = false)
    @JsonBackReference
    private Evenement evenement;
}