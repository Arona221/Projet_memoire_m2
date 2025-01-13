package connect.event.entity;

import connect.event.enums.Departement;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Lieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLieu;

    private String nom;

    private String adresse;

    private int capacite;

    private boolean disponible = true;;

    @Enumerated(EnumType.STRING)
    private Departement departement;
}