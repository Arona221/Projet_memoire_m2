package connect.event.dto;

import lombok.Data;

@Data
public class TokenDTO {
    private String token;
    private String type = "Bearer";
    private String typeUtilisateur;
    private String nom; // Ajout du nom de l'utilisateur
    private String email; // Ajout de l'email de l'utilisateur
    private long idutilisateur;

    public TokenDTO(String token, String typeUtilisateur, String nom, String email , long idutilisateur) {
        this.token = token;
        this.typeUtilisateur = typeUtilisateur;
        this.nom = nom;
        this.email = email;
        this.idutilisateur = idutilisateur;
    }


}
