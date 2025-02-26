package connect.event.participant.controller;

import connect.event.participant.entity.SalesMetrics;
import connect.event.repository.SalesMetricsRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = "http://localhost:4200")
public class AnalyticsController {

    @Autowired
    private SalesMetricsRepository salesMetricsRepository;

    @GetMapping("/event/{eventId}")

    public ResponseEntity<SalesMetrics> getEventAnalytics(@PathVariable Long eventId) {
        SalesMetrics metrics = salesMetricsRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune donnée trouvée pour l'événement : " + eventId));

        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/organisateur/{organisateurId}")
    public ResponseEntity<List<SalesMetrics>> getOrganisateurAnalytics(@PathVariable Long organisateurId) {
        List<SalesMetrics> metrics = salesMetricsRepository.findByOrganisateurId(organisateurId);
        return ResponseEntity.ok(metrics);
    }
}
