package connect.event.admin.entity;

import connect.event.admin.enums.TypeRessource;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RessourceFilter {
    private String searchTerm;
    private TypeRessource type;
    private Double prixMin;
    private Double prixMax;
    private String sortField = "nom";
    private String sortDirection = "asc";
}