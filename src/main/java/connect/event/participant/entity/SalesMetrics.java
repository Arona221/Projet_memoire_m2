package connect.event.participant.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdated;

    @Version
    private Long version;

    @Column(name = "event_name")
    private String eventName;

    @OneToMany(mappedBy = "salesMetrics", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<TicketSales> ticketsSoldByType = new ArrayList<>();

    public void addTicketSales(TicketSales ticketSales) {
        ticketSales.setSalesMetrics(this); // Définir la relation bidirectionnelle
        this.ticketsSoldByType.add(ticketSales);
    }
    public SalesMetrics(Long eventId, Long organisateurId, BigDecimal totalRevenue,
                        Integer totalTicketsSold, LocalDateTime lastUpdated, String eventName) {
        this.eventId = eventId;
        this.organisateurId = organisateurId;
        this.totalRevenue = totalRevenue;
        this.totalTicketsSold = totalTicketsSold;
        this.lastUpdated = lastUpdated;
        this.eventName = eventName;
    }
}