package connect.event.controller;

import connect.event.dto.EvenementDTO;
import connect.event.entity.Evenement;
import connect.event.enums.Status;
import connect.event.service.EvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evenements")
public class EvenementController {
    @Autowired
    private EvenementService evenementService;

    @GetMapping
    public List<EvenementDTO> getAllEvenements() {
        return evenementService.getAllEvenements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvenementDTO> getEvenementById(@PathVariable Long id) {
        EvenementDTO evenement = evenementService.getEvenementById(id); // Appel au service.
        return evenement != null ? ResponseEntity.ok(evenement) // Si l'événement existe, renvoie 200 OK.
                : ResponseEntity.notFound().build(); // Sinon, renvoie 404 Not Found.
    }

    @PostMapping
    public ResponseEntity<Evenement> createEvenement(@RequestBody EvenementDTO evenementDTO) {
        // Vérifiez si le status est null et initialisez-le avec EN_ATTENTE
        if (evenementDTO.getStatus() == null) {
            evenementDTO.setStatus(Status.EN_ATTENTE);
        }

        // Créez l'événement avec le service
        Evenement newEvenement = evenementService.createEvenement(evenementDTO);

        // Retournez la réponse avec l'événement créé
        return ResponseEntity.ok(newEvenement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvenement(@PathVariable Long id) {
        evenementService.deleteEvenement(id);
        return ResponseEntity.noContent().build();
    }
}