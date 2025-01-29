package connect.event.admin.dto;

import connect.event.admin.entity.Transport;
import connect.event.admin.enums.TypeRessource;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransportDTO extends RessourceDTO {
    private String typeTransport;
    private int nombrePlaces;

    public static TransportDTO fromEntity(Transport transport) {
        TransportDTO dto = new TransportDTO();
        dto.setId(transport.getId());
        dto.setNom(transport.getNom());
        dto.setPrix(transport.getPrix());
        dto.setImage(transport.getImage());
        dto.setType(TypeRessource.TRANSPORT);
        dto.setTypeTransport(transport.getTypeTransport());
        dto.setNombrePlaces(transport.getNombrePlaces());
        return dto;
    }
}
