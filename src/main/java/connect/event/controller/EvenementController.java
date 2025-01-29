package connect.event.controller;

import connect.event.dto.EvenementDTO;
import connect.event.entity.Evenement;
import connect.event.enums.Categorie;
import connect.event.enums.Status;
import connect.event.service.EvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    public ResponseEntity<Evenement> createEvenement(@RequestBody EvenementDTO evenementDTO, @RequestParam Long idUtilisateur) {
        // Créez l'événement avec l'utilisateur en tant qu'organisateur
        Evenement newEvenement = evenementService.createEvenement(evenementDTO, idUtilisateur);

        // Retourner la réponse avec l'événement créé
        return ResponseEntity.ok(newEvenement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvenement(@PathVariable Long id) {
        evenementService.deleteEvenement(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam Status status) {
        boolean updated = evenementService.updateStatus(id, status);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
    @GetMapping("/search")
    public ResponseEntity<List<EvenementDTO>> searchEvenements(
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam(required = false) String lieu) {

        // Convertir la catégorie en enum si elle est fournie
        Categorie cat = null;
        if (categorie != null && !categorie.isEmpty()) {
            try {
                cat = Categorie.valueOf(categorie.toUpperCase()); // Conversion
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null); // Mauvaise catégorie
            }
        }

        // Recherche des événements avec les filtres
        List<EvenementDTO> result = evenementService.searchEvenements(cat, date, lieu);
        return ResponseEntity.ok(result);
    }

}
