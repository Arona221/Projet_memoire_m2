package connect.event.participant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import connect.event.entity.Evenement;
import connect.event.participant.entity.SalesMetrics;
import connect.event.repository.EvenementRepository;
import connect.event.repository.SalesMetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SalesAnalyticsConsumer {


    private final Map<Long, SalesMetrics> metricsCache = new ConcurrentHashMap<>();
    @Autowired
    EvenementRepository evenementRepository;
    @Autowired
    SalesMetricsRepository salesMetricsRepository;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "ticket-purchases", groupId = "sales-analytics-group")
    public void processTicketPurchase(String message) {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(message);

            // Extraction correcte des valeurs
            Long eventId = jsonNode.get("eventId").asLong();
            Double montantTotal = jsonNode.get("montantTotal").asDouble();
            Integer quantite = jsonNode.get("quantite").asInt(); // Reçoit maintenant la somme totale

            updateSalesMetrics(eventId, montantTotal, quantite);
        } catch (Exception e) {
            System.err.println("💥 ERREUR CRITIQUE: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    protected void updateSalesMetrics(Long eventId, Double montantTotal, Integer quantite) {
        try {
            Evenement event = evenementRepository.findById(eventId)
                    .orElseThrow(() -> new RuntimeException("Événement " + eventId + " non trouvé"));

            SalesMetrics metrics = salesMetricsRepository.findByEventId(eventId)
                    .orElseGet(() -> {
                        System.out.println("🆕 Création nouvelle métrique pour event " + eventId);
                        return new SalesMetrics(
                                eventId,
                                event.getOrganisateur().getIdUtilisateur(),
                                BigDecimal.ZERO,
                                0,
                                LocalDateTime.now()
                        );
                    });

            System.out.printf("📊 Avant mise à jour - Event: %d | CA: %.2f | Tickets: %d%n",
                    eventId, metrics.getTotalRevenue(), metrics.getTotalTicketsSold());

            metrics.setTotalRevenue(metrics.getTotalRevenue().add(BigDecimal.valueOf(montantTotal)));
            metrics.setTotalTicketsSold(metrics.getTotalTicketsSold() + quantite);
            metrics.setLastUpdated(LocalDateTime.now());

            salesMetricsRepository.saveAndFlush(metrics);

            System.out.printf("✅ Après mise à jour - Event: %d | CA: %.2f | Tickets: %d%n",
                    eventId, metrics.getTotalRevenue(), metrics.getTotalTicketsSold());

        } catch (Exception e) {
            System.err.println("💥 ERREUR LORS DE LA MISE À JOUR: " + e.getMessage());
            throw e; // Pour rollback transaction
        }
    }
}
