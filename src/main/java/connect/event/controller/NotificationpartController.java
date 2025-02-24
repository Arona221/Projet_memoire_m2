package connect.event.controller;

import connect.event.dto.NotificationpartDTO;
import connect.event.service.NotificationpartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationpartController {
    @Autowired
    private NotificationpartService notificationService;

    @GetMapping("/utilisateur/{id}")
    public ResponseEntity<List<NotificationpartDTO>> getNotificationsUtilisateur(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                notificationService.getNotificationsUtilisateur(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerNotification(@PathVariable Long id) {
        notificationService.softDeleteNotification(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/marquer-lues/{userId}")
    public ResponseEntity<List<NotificationpartDTO>> markAllNotificationsAsRead(
            @PathVariable Long userId) {

        List<NotificationpartDTO> notifications = notificationService.markAllAsRead(userId);

        return ResponseEntity.ok()
                .header("Message", "Toutes les notifications marquées comme lues")
                .body(notifications);
    }
}