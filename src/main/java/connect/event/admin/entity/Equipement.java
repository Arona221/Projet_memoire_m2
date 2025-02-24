package connect.event.admin.entity;

import connect.event.admin.enums.TypeRessource;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("EQUIPEMENT")
@Getter
@Setter
public class Equipement extends Ressource {
    private String typeEquipement;
    private int quantite;
    private String specifications;
}