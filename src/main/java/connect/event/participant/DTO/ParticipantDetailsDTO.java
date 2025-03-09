package connect.event.participant.DTO;

import lombok.Data;

import java.util.Map;

@Data
public class ParticipantDetailsDTO {
    private String prenom;
    private String nom;
    private String email;
    private Map<String, Integer> tickets; // Clé: type de billet, Valeur: quantité
}
