package connect.event.participant.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SalesUpdate {
    private Long eventId;
    private String eventName;
    private Date eventDate;
    private int quantity;
    private BigDecimal amount;
    private Date timestamp;
}