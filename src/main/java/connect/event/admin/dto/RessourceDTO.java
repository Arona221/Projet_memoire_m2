package connect.event.admin.dto;

import connect.event.admin.entity.Ressource;
import connect.event.admin.enums.TypeRessource;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RessourceDTO {
    private Long id;
    private String nom;
    private double prix;
    private String image;
    private TypeRessource type;

    public static RessourceDTO fromEntity(Ressource ressource) {
        RessourceDTO dto = new RessourceDTO();
        dto.setId(ressource.getId());
        dto.setNom(ressource.getNom());
        dto.setPrix(ressource.getPrix());
        dto.setImage(ressource.getImage());
        dto.setType(ressource.getType());
        return dto;
    }
}
