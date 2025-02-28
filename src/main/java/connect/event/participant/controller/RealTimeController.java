package connect.event.participant.controller;

import connect.event.entity.Evenement;
import connect.event.participant.entity.SalesUpdate;
import connect.event.repository.EvenementRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/real-time")
public class RealTimeController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private EvenementRepository evenementRepository;

    @PostMapping("/sales")
    public void sendRealTimeUpdate(@RequestBody SalesUpdate update) {
        System.out.println("Reçu une nouvelle transaction : " + update);
        SalesUpdate enrichedUpdate = enrichWithEventData(update);
        System.out.println("Enrichi et envoyé via WebSocket : " + enrichedUpdate);
        messagingTemplate.convertAndSend("/topic/sales-updates", enrichedUpdate);
    }

    private SalesUpdate enrichWithEventData(SalesUpdate update) {
        Evenement event = evenementRepository.findById(update.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé"));

        return update.toBuilder()
                .eventName(event.getNom())
                .eventDate(event.getDate())
                .build();
    }
}