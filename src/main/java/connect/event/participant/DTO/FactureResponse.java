package connect.event.participant.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class FactureResponse {
    private String status;
    private String message;
    private String invoiceUrl;
    private String referenceTransaction;
}
