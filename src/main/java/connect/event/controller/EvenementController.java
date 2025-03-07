package connect.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import connect.event.admin.service.EmailService;
import connect.event.dto.EvenementDTO;
import connect.event.entity.Evenement;
import connect.event.enums.Categorie;
import connect.event.enums.Status;
import connect.event.service.EvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;



@RestController
@RequestMapping("/evenements")
@CrossOrigin(origins = "http://localhost:4200")
public class EvenementController {
    @Autowired
    private EvenementService evenementService;
    @Autowired  // Injecte l'ObjectMapper configuré par Spring
    private ObjectMapper objectMapper;

    @Qualifier("adminEmailService")  // Spécifie le bean à injecter
    private EmailService emailService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvenement(@PathVariable Long id) {
        evenementService.deleteEvenement(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public List<EvenementDTO> getAllEvenements()
    {
        return evenementService.getAllEvenements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvenementDTO> getEvenementById(@PathVariable Long id) {
        EvenementDTO evenement = evenementService.getEvenementById(id); // Appel au service.
        return evenement != null ? ResponseEntity.ok(evenement) // Si l'événement existe, renvoie 200 OK.
                : ResponseEntity.notFound().build(); // Sinon, renvoie 404 Not Found.
    }

    @PostMapping
    public ResponseEntity<Evenement> createEvenement(
            @RequestParam("idUtilisateur") Long idUtilisateur,
            @RequestPart("evenement") String evenementJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {

        // Utilise l'ObjectMapper injecté au lieu de new ObjectMapper()
        EvenementDTO evenementDTO = objectMapper.readValue(evenementJson, EvenementDTO.class);

        Evenement newEvenement = evenementService.createEvenement(evenementDTO, idUtilisateur, imageFile);
        return ResponseEntity.ok(newEvenement);
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
    @GetMapping("/organisateur/{idOrganisateur}")
    public ResponseEntity<Page<EvenementDTO>> getEvenementsByOrganisateur(
            @PathVariable Long idOrganisateur,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Status status) {

        Page<EvenementDTO> evenements = evenementService.getEvenementsByOrganisateur(idOrganisateur, page, size, search, status);
        return ResponseEntity.ok(evenements);
    }
    @GetMapping("/approved")
    public ResponseEntity<Page<EvenementDTO>> getApprovedEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam(required = false) String lieu) {

        Page<EvenementDTO> events = evenementService.getApprovedEvents(page, size, search, categorie, date, lieu);
        return ResponseEntity.ok(events);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @GetMapping("/events")
    public ResponseEntity<Page<EvenementDTO>> getApprovedEventMarting(
            @RequestParam(required = false) String categorie,
            @RequestParam(required = false) String lieu,
            @RequestParam(required = false) String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
        Page<EvenementDTO> approvedEvents = evenementService.getApprovedEventMarting(categorie, lieu, date, pageable);

        return ResponseEntity.ok(approvedEvents);
    }




}