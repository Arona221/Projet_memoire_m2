package connect.event.organisateur.controller;

import connect.event.organisateur.DTO.NotificationDTO;
import connect.event.organisateur.service.NotificationService;
import connect.event.organisateur.service.NotificationServiceSms;
import connect.event.organisateur.service.NotificationServiceWhatsApp;
import connect.event.participant.entity.SendNotification;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationServiceSms notificationServiceSms;
    @Autowired
    private NotificationServiceWhatsApp notificationServiceWhatsApp;

    // NotificationController.java

    @PostMapping("/email")
    public void envoyerNotification(
            @RequestParam Long organisateurId,
            @RequestParam Long evenementId,
            @RequestBody NotificationDTO notificationDTO) throws MessagingException {

        System.out.println("=== REÇU DU FRONTEND ===");
        System.out.println("Organisateur ID: " + organisateurId);
        System.out.println("Événement ID: " + evenementId);
        System.out.println("Contenu: " + notificationDTO.getContenu());
        System.out.println("Type: " + notificationDTO.getTypeNotification());

        notificationService.envoyerNotification(organisateurId, evenementId, notificationDTO);
    }
    @PostMapping("/sms")
    public String envoyerSms(@RequestParam Long organisateurId, @RequestParam Long evenementId, @RequestParam String message) {
        try {
            notificationServiceSms.envoyerNotificationParSms(organisateurId, evenementId, message);
            return "✅ SMS envoyés avec succès !";
        } catch (Exception e) {
            return "❌ Erreur: " + e.getMessage();
        }
    }

    @PostMapping("/whatsApp")
    public String envoyerNotificationWhatsApp(
            @RequestParam Long organisateurId,
            @RequestParam Long evenementId,
            @RequestBody NotificationDTO notificationDTO) {

        notificationServiceWhatsApp.envoyerNotificationWhatsApp(organisateurId, evenementId, notificationDTO);
        return "📩 Notification WhatsApp envoyée à tous les participants de l'événement " + evenementId;
    }

    @GetMapping("/user/{userId}")
    public List<SendNotification> getUserNotifications(@PathVariable Long userId) {
        return notificationService.getUnreadNotifications(userId);
    }

    @PostMapping("/mark-read/{id}")
    public void markNotificationAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }


}