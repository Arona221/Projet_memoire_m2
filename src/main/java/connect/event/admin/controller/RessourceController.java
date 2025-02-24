package connect.event.admin.controller;

import connect.event.admin.dto.*;
import connect.event.admin.service.FileStorageService;
import connect.event.admin.entity.RessourceFilter;
import connect.event.admin.service.RessourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/ressources")
@Tag(name = "Gestion des Ressources", description = "API pour la gestion des ressources événementielles")
public class RessourceController {

    private final RessourceService ressourceService;
    private final FileStorageService fileStorageService;

    public RessourceController(RessourceService ressourceService,
                               FileStorageService fileStorageService) {
        this.ressourceService = ressourceService;
        this.fileStorageService = fileStorageService;
    }

    @Operation(summary = "Recherche de ressources avec pagination et filtres")
    @GetMapping
    public ResponseEntity<Page<RessourceDTO>> searchRessources(
            @Parameter(description = "Filtres de recherche") @ModelAttribute RessourceFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ressourceService.searchRessources(filter, page, size));
    }

    @Operation(summary = "Télécharger une image")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @Parameter(description = "Fichier image à uploader") @RequestParam("file") MultipartFile file) throws IOException {
        String fileName = fileStorageService.storeFile(file);
        return ResponseEntity.ok("/uploads/" + fileName);
    }

    @Operation(summary = "Créer un nouveau lieu")
    @PostMapping("/lieux")
    public ResponseEntity<LieuDTO> createLieu(
            @Parameter(description = "DTO pour la création d'un lieu") @RequestBody LieuDTO lieuDTO) {
        return new ResponseEntity<>(ressourceService.createLieu(lieuDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Créer un nouvel équipement")
    @PostMapping("/equipements")
    public ResponseEntity<EquipementDTO> createEquipement(
            @Parameter(description = "DTO pour la création d'un équipement") @RequestBody EquipementDTO dto) {
        return new ResponseEntity<>(ressourceService.createEquipement(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Supprimer une ressource")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRessource(
            @Parameter(description = "ID de la ressource à supprimer") @PathVariable Long id) {
        ressourceService.deleteRessource(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Créer un nouveau transport")
    @PostMapping("/transports")
    public ResponseEntity<TransportDTO> createTransport(
            @Parameter(description = "DTO pour la création d'un transport") @RequestBody TransportDTO transportDTO) {
        return new ResponseEntity<>(ressourceService.createTransport(transportDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Mettre à jour une ressource")
    @PutMapping("/{id}")
    public ResponseEntity<RessourceDTO> updateRessource(
            @Parameter(description = "ID de la ressource") @PathVariable Long id,
            @RequestBody RessourceDTO ressourceDTO) {
        return ResponseEntity.ok(ressourceService.updateRessource(id, ressourceDTO));
    }
    @Operation(summary = "Récupérer une ressource par ID")
    @GetMapping("/{id}")
    public ResponseEntity<RessourceDTO> getRessourceById(
            @Parameter(description = "ID de la ressource") @PathVariable Long id) {
        return ResponseEntity.ok(ressourceService.getRessourceById(id));
    }

    @Operation(summary = "Récupérer toutes les ressources avec pagination")
    @GetMapping("/all")
    public ResponseEntity<Page<RessourceDTO>> getAllRessourcesPaginated(
            @Parameter(description = "Numéro de page (0-indexé)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Nombre d'éléments par page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ressourceService.getAllRessourcesPaginated(page, size));
    }


}