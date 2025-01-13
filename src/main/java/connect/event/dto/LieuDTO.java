package connect.event.dto;

import connect.event.enums.Departement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LieuDTO {

    @NotNull(message = "La région est obligatoire")
    private Departement departement;
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    @Min(value = 1, message = "La capacité doit être supérieure à 0")
    private int capacite;

    private boolean disponible=true;


}
