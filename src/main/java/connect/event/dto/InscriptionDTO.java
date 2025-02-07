package connect.event.dto;

import connect.event.enums.TypeUtilisateur;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class InscriptionDTO {
    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    private String motDePasse;
    @NotBlank(message = "Le numero de telephone est obligatoire")
    @Pattern(regexp = "^\\+?[0-9]+$", message = "Numéro de téléphone invalide")
    private String phoneNumber;
    @NotNull(message = "Le type d'utilisateur est obligatoire")
    private TypeUtilisateur typeUtilisateur;
}
