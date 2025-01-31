package connect.event.participant.DTO;

import connect.event.entity.Billet;
import connect.event.participant.entity.BilletAcheter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaiementResponse {
    private String status;
    private String message;
    private String statutPaiement;
    private Billet billet;

    public PaiementResponse(String status, String message, BilletAcheter billetAcheter) {
        this.status = status;
        this.message = message;
        // Vérification de la nullité de billet
        if (billetAcheter != null && billetAcheter.getBillet() != null) {
            this.billet = billetAcheter.getBillet();
        } else {
            this.billet = null; // Vous pouvez aussi lancer une exception si nécessaire
        }
    }
}
