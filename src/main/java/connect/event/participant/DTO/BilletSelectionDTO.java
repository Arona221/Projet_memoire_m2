package connect.event.participant.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class BilletSelectionDTO {
    @NotNull(message = "L'ID de l'événement est obligatoire")
    private Long evenementId;

    @NotNull(message = "L'ID du participant est obligatoire")
    private Long participantId;

    @NotEmpty(message = "Au moins un billet doit être sélectionné")
    private List<BilletItemDTO> billets;

    @NotBlank(message = "Le mode de paiement est obligatoire")
    private String modePaiement;
}