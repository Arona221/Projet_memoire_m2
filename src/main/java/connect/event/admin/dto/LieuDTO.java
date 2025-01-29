package connect.event.admin.dto;

import connect.event.admin.entity.Lieu;
import connect.event.admin.enums.Departement;
import connect.event.admin.enums.TypeRessource;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LieuDTO extends RessourceDTO {
    private int capacite;
    private Departement departement;
    private String adresse;

    public static LieuDTO fromEntity(Lieu lieu) {
        LieuDTO dto = new LieuDTO();
        dto.setId(lieu.getId());
        dto.setNom(lieu.getNom());
        dto.setPrix(lieu.getPrix());
        dto.setImage(lieu.getImage());
        dto.setType(TypeRessource.LIEU);
        dto.setCapacite(lieu.getCapacite());
        dto.setDepartement(lieu.getDepartement());
        dto.setAdresse(lieu.getAdresse());
        return dto;
    }
}
