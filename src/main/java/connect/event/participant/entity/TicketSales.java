package connect.event.participant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "ticket_sales")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketSales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sales_metrics_id", nullable = false)
    @JsonIgnore
    private SalesMetrics salesMetrics;

    @Column(name = "ticket_type", nullable = false)
    private String ticketType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "montant_total", precision = 10, scale = 2)
    private BigDecimal montantTotal;

    public TicketSales(String ticketType, Integer quantity, BigDecimal montantTotal) {
        this.ticketType = ticketType;
        this.quantity = quantity;
        this.montantTotal = montantTotal;
    }
}