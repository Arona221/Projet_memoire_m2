package connect.event.organisateur.controller;

import connect.event.organisateur.DTO.NotificationDTO;
import connect.event.organisateur.service.NotificationService;
import connect.event.organisateur.service.NotificationServiceSms;
import connect.event.organisateur.service.NotificationServiceWhatsApp;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private NotificationServiceSms notificationServiceSms;
    @Autowired
    private NotificationServiceWhatsApp notificationServiceWhatsApp;

    @PostMapping("/email")
    public void envoyerNotification(@RequestParam Long organisateurId, @RequestParam Long evenementId,
                                    @RequestBody NotificationDTO notificationDTO) throws MessagingException {
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


}