package connect.event.admin.entity;

import connect.event.admin.enums.TypeRessource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Une seule table pour tous les types
@DiscriminatorColumn(name = "type_ressource", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class Ressource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private double prix;
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_ressource", insertable = false, updatable = false) // Empêcher la duplication du champ
    private TypeRessource type;
}
