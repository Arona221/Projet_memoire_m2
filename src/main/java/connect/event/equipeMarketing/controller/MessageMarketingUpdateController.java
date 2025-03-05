package connect.event.equipeMarketing.controller;

import connect.event.entity.Evenement;
import connect.event.equipeMarketing.entity.Contact;
import connect.event.equipeMarketing.entity.MessageMarketingUpdate;
import connect.event.equipeMarketing.repository.ContactRepository;
import connect.event.equipeMarketing.service.MessageMarketingUpdateService;
import connect.event.repository.EvenementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@RestController
@RequestMapping("/messages/update") // Changer le chemin ici
@CrossOrigin(origins = "*")
public class MessageMarketingUpdateController {

    @Autowired
    private MessageMarketingUpdateService messageService;
    @Autowired
    private EvenementRepository evenementRepository;

    @PostMapping
    public ResponseEntity<MessageMarketingUpdate> creerMessage(@RequestBody MessageMarketingUpdate message) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.creerMessage(message));
    }

    @GetMapping
    public ResponseEntity<List<MessageMarketingUpdate>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageMarketingUpdate> getMessageById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getMessageById(id));
    }

    @Autowired
    private ContactRepository contactRepository;

    @PostMapping("/generer/{idEvenement}")
    public ResponseEntity<MessageMarketingUpdate> genererMessage(@PathVariable Long idEvenement) {
        Evenement evenement = evenementRepository.findById(idEvenement)
                .orElseThrow(() -> new IllegalArgumentException("Événement non trouvé"));

        log.info("📢 Génération du message pour l'événement : {}", evenement.getNom());

        // Récupérer les contacts (par exemple, tous les contacts ou ceux d'un segment spécifique)
        List<Contact> contacts = contactRepository.findAll(); // Ou une autre logique de récupération

        // Créer un nouveau message
        MessageMarketingUpdate message = new MessageMarketingUpdate();

        // Générer le message personnalisé
        MessageMarketingUpdate messagePersonnalise = messageService.creerMessagePersonnalise(contacts, message, evenement);

        if (messagePersonnalise.getIdMessage() == null) {
            log.error("❌ Échec de l'enregistrement en base !");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

        log.info("✅ Message enregistré avec succès, ID: {}", messagePersonnalise.getIdMessage());
        return ResponseEntity.status(HttpStatus.CREATED).body(messagePersonnalise);
    }
}