package connect.event.participant.controller;

import connect.event.participant.service.FavorisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favoris")
public class FavorisController {

    @Autowired
    private FavorisService favorisService;

    /**
     * Ajouter un événement aux favoris.
     */
    @PostMapping("/add")
    public ResponseEntity<Void> addToFavorites(@RequestParam Long idUtilisateur, @RequestParam Long idEvenement) {
        try {
            favorisService.addToFavorites(idUtilisateur, idEvenement);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retirer un événement des favoris.
     */
    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFromFavorites(@RequestParam Long idUtilisateur, @RequestParam Long idEvenement) {
        try {
            favorisService.removeFromFavorites(idUtilisateur, idEvenement);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
