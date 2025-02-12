package connect.event.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Long id;

    private String typeBillet;
    private BigDecimal prix;
    private Integer quantite;

    @ManyToOne
    @JoinColumn(name = "evenement_id")
    @JsonIgnore // Empêche la sérialisation infinie
    private Evenement evenement;


}