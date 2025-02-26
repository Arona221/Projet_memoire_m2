package connect.event.participant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_metrics")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "organisateur_id", nullable = false)
    private Long organisateurId;

    @Column(name = "total_revenue", precision = 10, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(name = "total_tickets_sold")
    private Integer totalTicketsSold = 0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Version
    private Long version;

    public SalesMetrics(Long eventId, Long organisateurId, BigDecimal totalRevenue,
                        Integer totalTicketsSold, LocalDateTime lastUpdated) {
        this.eventId = eventId;
        this.organisateurId = organisateurId;
        this.totalRevenue = totalRevenue;
        this.totalTicketsSold = totalTicketsSold;
        this.lastUpdated = lastUpdated;
    }
}