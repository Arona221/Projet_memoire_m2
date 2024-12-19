package connect.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class BilletDTO {
    private String typeBillet;
    private BigDecimal prix;
    private Integer quantite;
}
