package connect.event.participant.controller;

import connect.event.entity.Evenement;
import connect.event.participant.entity.SalesMetrics;
import connect.event.participant.entity.TicketSales;
import connect.event.repository.EvenementRepository;
import connect.event.repository.SalesMetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = "http://localhost:4200")
public class AnalyticsController {

    @Autowired
    private SalesMetricsRepository salesMetricsRepository;

    @Autowired
    private EvenementRepository evenementRepository;

    @GetMapping("/organisateur/{organisateurId}")
    public ResponseEntity<List<SalesMetrics>> getOrganisateurAnalytics(@PathVariable Long organisateurId) {
        List<SalesMetrics> metrics = salesMetricsRepository.findByOrganisateurId(organisateurId);

        // Ajouter le nom de l'événement à chaque métrique
        metrics.forEach(metric -> {
            Evenement event = evenementRepository.findById(metric.getEventId())
                    .orElseThrow(() -> new RuntimeException("Événement non trouvé"));
            metric.setEventName(event.getNom());
        });

        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/event/{eventId}/details")
    public ResponseEntity<Map<String, Object>> getEventAnalyticsDetails(@PathVariable Long eventId) {
        SalesMetrics metrics = salesMetricsRepository.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("Métriques non trouvées pour l'événement: " + eventId));

        // Récupérer l'événement pour plus de détails
        Evenement event = evenementRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        // Construire une réponse détaillée
        Map<String, Object> response = new HashMap<>();
        response.put("eventId", eventId);
        response.put("eventName", event.getNom());
        response.put("totalRevenue", metrics.getTotalRevenue());
        response.put("totalTicketsSold", metrics.getTotalTicketsSold());
        response.put("lastUpdated", metrics.getLastUpdated());

        // Ajouter les détails par type de billet
        List<Map<String, Object>> ticketDetails = metrics.getTicketsSoldByType().stream()
                .map(ticket -> {
                    Map<String, Object> detail = new HashMap<>();
                    detail.put("type", ticket.getTicketType());
                    detail.put("quantity", ticket.getQuantity());
                    detail.put("revenue", ticket.getMontantTotal());
                    return detail;
                })
                .collect(Collectors.toList());

        response.put("ticketsByType", ticketDetails);

        return ResponseEntity.ok(response);
    }
}