package connect.event.participant.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BilletItemDTO {
    @NotNull(message = "L'ID du billet est obligatoire")
    private Long billetId;

    @Min(value = 1, message = "La quantité doit être d'au moins 1")
    private int quantite;
}