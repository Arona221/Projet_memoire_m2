package connect.event.admin.entity;

import connect.event.admin.enums.Departement;
import connect.event.admin.enums.TypeRessource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("LIEU") // Valeur stockée dans la colonne `type_ressource`
@Getter
@Setter
public class Lieu extends Ressource {
    private int capacite;

    @Enumerated(EnumType.STRING)
    private Departement departement;

    private String adresse;
}
