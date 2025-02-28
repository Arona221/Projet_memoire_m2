package connect.event.participant.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import connect.event.entity.Evenement;
import connect.event.participant.entity.SalesMetrics;
import connect.event.participant.entity.TicketSales;
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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Service
public class SalesAnalyticsConsumer {

    private static final Logger LOGGER = Logger.getLogger(SalesAnalyticsConsumer.class.getName());
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
            LOGGER.info("Received message: " + message);
            JsonNode jsonNode = new ObjectMapper().readTree(message);

            // Extraction des valeurs
            Long eventId = jsonNode.get("eventId").asLong();
            Double montantTotal = jsonNode.get("montantTotal").asDouble();
            Integer quantite = jsonNode.get("quantite").asInt();
            JsonNode ticketsByTypeNode = jsonNode.get("ticketsByType");
            JsonNode montantsByTypeNode = jsonNode.get("montantsByType");

            // Convertir les tickets par type en Map
            Map<String, Integer> ticketsByType = new ObjectMapper().convertValue(
                    ticketsByTypeNode,
                    new TypeReference<Map<String, Integer>>() {}
            );

            // Convertir les montants par type en Map
            Map<String, BigDecimal> montantsByType = new ObjectMapper().convertValue(
                    montantsByTypeNode,
                    new TypeReference<Map<String, BigDecimal>>() {}
            );

            // Récupérer ou créer les métriques de vente
            SalesMetrics metrics = getSalesMetricsForEvent(eventId);

            // Mise à jour globale (totaux)
            metrics.setTotalRevenue(metrics.getTotalRevenue().add(BigDecimal.valueOf(montantTotal)));
            metrics.setTotalTicketsSold(metrics.getTotalTicketsSold() + quantite);
            metrics.setLastUpdated(LocalDateTime.now());

            // Mise à jour par type de billet
            updateTicketsByType(metrics, ticketsByType, montantsByType);

            // Sauvegarder les métriques
            salesMetricsRepository.save(metrics);

            LOGGER.info(String.format("✅ Updated metrics - Event: %d | Revenue: %.2f | Tickets: %d | Types: %d",
                    eventId, metrics.getTotalRevenue(), metrics.getTotalTicketsSold(), metrics.getTicketsSoldByType().size()));

        } catch (Exception e) {
            LOGGER.severe("💥 CRITICAL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void updateTicketsByType(SalesMetrics metrics, Map<String, Integer> ticketsByType, Map<String, BigDecimal> montantsByType) {
        for (Map.Entry<String, Integer> entry : ticketsByType.entrySet()) {
            String ticketType = entry.getKey();
            Integer quantity = entry.getValue();
            BigDecimal amount = montantsByType.get(ticketType);

            // Vérifier si ce type de ticket existe déjà
            boolean found = false;
            for (TicketSales existingTicket : metrics.getTicketsSoldByType()) {
                if (existingTicket.getTicketType().equals(ticketType)) {
                    // Mettre à jour le ticket existant
                    existingTicket.setQuantity(existingTicket.getQuantity() + quantity);
                    existingTicket.setMontantTotal(existingTicket.getMontantTotal().add(amount));
                    found = true;
                    break;
                }
            }

            // Si le type de ticket n'existe pas, en créer un nouveau
            if (!found) {
                TicketSales newTicketSale = new TicketSales(ticketType, quantity, amount);
                metrics.addTicketSales(newTicketSale);
            }
        }
    }

    @Transactional
    protected SalesMetrics getSalesMetricsForEvent(Long eventId) {
        return salesMetricsRepository.findByEventId(eventId)
                .orElseGet(() -> {
                    LOGGER.info("🆕 Création de nouvelles métriques pour l'événement " + eventId);
                    Evenement event = evenementRepository.findById(eventId)
                            .orElseThrow(() -> new RuntimeException("Événement " + eventId + " non trouvé"));

                    SalesMetrics newMetrics = new SalesMetrics(
                            eventId,
                            event.getOrganisateur().getIdUtilisateur(),
                            BigDecimal.ZERO,
                            0,
                            LocalDateTime.now(),
                            event.getNom()
                    );

                    return salesMetricsRepository.save(newMetrics);
                });
    }
}