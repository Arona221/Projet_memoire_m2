package connect.event.admin.controller;

import connect.event.admin.dto.LieuDTO;
import connect.event.admin.dto.RessourceDTO;
import connect.event.admin.dto.TransportDTO;
import connect.event.admin.service.RessourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ressources")
public class RessourceController {

    @Autowired
    private RessourceService ressourceService;

    // 🔹 Récupérer toutes les ressources
    @GetMapping
    public ResponseEntity<List<RessourceDTO>> getAllRessources() {
        return ResponseEntity.ok(ressourceService.getAllRessources());
    }

    // 🔹 Récupérer une ressource par ID
    @GetMapping("/{id}")
    public ResponseEntity<RessourceDTO> getRessourceById(@PathVariable Long id) {
        Optional<RessourceDTO> ressource = ressourceService.getRessourceById(id);
        return ressource.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Créer un lieu
    @PostMapping("/lieu")
    public ResponseEntity<LieuDTO> createLieu(@RequestBody LieuDTO lieuDTO) {
        return ResponseEntity.ok(ressourceService.createLieu(lieuDTO));
    }

    // 🔹 Créer un transport
    @PostMapping("/transport")
    public ResponseEntity<TransportDTO> createTransport(@RequestBody TransportDTO transportDTO) {
        return ResponseEntity.ok(ressourceService.createTransport(transportDTO));
    }

    // 🔹 Supprimer une ressource
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRessource(@PathVariable Long id) {
        return ressourceService.deleteRessource(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
