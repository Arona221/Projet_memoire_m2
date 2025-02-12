package connect.event.dto;

import connect.event.enums.Categorie;
import connect.event.enums.Status;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvenementDTO {

    @NotNull(message = "Le nom de l'événement ne peut pas être nul.")
    @Size(min = 1, max = 100, message = "Le nom de l'événement doit comporter entre 1 et 100 caractères.")
    private String nom;

    private Date date;
    private String description;
    private String lieu;
    private Categorie categorie;
    private Status status;
    private int nombrePlaces;


    private String imageUrl; // Stocke uniquement le nom du fichier
    private List<BilletDTO> billets;
    private Long id_evenement;
    private String heure;
}
