package connect.event.admin.dto;

import connect.event.admin.entity.Equipement;
import connect.event.admin.enums.TypeRessource;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipementDTO extends RessourceDTO {
    private String typeEquipement;
    private int quantite;
    private String specifications;

    public static EquipementDTO fromEntity(Equipement equipement) {
        EquipementDTO dto = new EquipementDTO();
        dto.setId(equipement.getId());
        dto.setNom(equipement.getNom());
        dto.setPrix(equipement.getPrix());
        dto.setImage(equipement.getImage());
        dto.setType(TypeRessource.EQUIPEMENT);
        dto.setTypeEquipement(equipement.getTypeEquipement());
        dto.setQuantite(equipement.getQuantite());
        dto.setSpecifications(equipement.getSpecifications());
        return dto;
    }
}