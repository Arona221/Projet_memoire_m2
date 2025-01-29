package connect.event.admin.entity;

import connect.event.admin.enums.TypeRessource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("TRANSPORT") // Valeur stockée dans la colonne `type_ressource`
@Getter
@Setter
public class Transport extends Ressource {
    private String typeTransport;
    private int nombrePlaces;
}
