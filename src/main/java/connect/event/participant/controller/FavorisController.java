package connect.event.participant.controller;

import connect.event.entity.Evenement;
import connect.event.participant.service.FavorisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favoris")
public class FavorisController {

    private final FavorisService favorisService;

    @PostMapping
    public ResponseEntity<Void> addFavorite(
            @RequestParam("idUtilisateur") Long utilisateurId,
            @RequestParam("idEvenement") Long evenementId) {

        favorisService.addToFavorites(utilisateurId, evenementId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(
            @RequestParam("idUtilisateur") Long utilisateurId,
            @RequestParam("idEvenement") Long evenementId) {

        favorisService.removeFromFavorites(utilisateurId, evenementId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> checkFavoriteStatus(
            @RequestParam("idUtilisateur") Long utilisateurId,
            @RequestParam("idEvenement") Long evenementId) {

        return ResponseEntity.ok(favorisService.isFavorite(utilisateurId, evenementId));
    }

    @GetMapping
    public ResponseEntity<Page<Evenement>> getFavorites(
            @RequestParam(name = "idUtilisateur", defaultValue = "0") Long utilisateurId, // Ajoutez name=
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        return ResponseEntity.ok(favorisService.getFavorisUtilisateur(
                utilisateurId,
                PageRequest.of(page, size, Sort.by("dateAjout").descending()))
        );
    }
}
