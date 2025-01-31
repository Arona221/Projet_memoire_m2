package connect.event.participant.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BilletSelectionDTO {

    @NotNull(message = "L'ID de l'événement est obligatoire")
    private Long evenementId;

    @NotNull(message = "L'ID du participant est obligatoire")
    private Long participantId;

    @NotNull(message = "L'ID du billet est obligatoire")
    private Long billetId;

    @Min(value = 1, message = "La quantité doit être d'au moins 1")
    private int quantite;

    @NotBlank(message = "Le mode de paiement est obligatoire")
    private String modePaiement; // PAYDUNYA ou WAVE
}