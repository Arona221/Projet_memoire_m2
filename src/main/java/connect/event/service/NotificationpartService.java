package connect.event.service;

import connect.event.dto.NotificationpartDTO;
import connect.event.entity.Notificationpart;
import connect.event.entity.Utilisateur;
import connect.event.repository.NotificationpartRepository;
import connect.event.repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationpartService {
    @Autowired
    private NotificationpartRepository notificationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public NotificationpartDTO creerNotification(Long utilisateurId, String message) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Notificationpart notification = new Notificationpart();
        notification.setMessage(message);
        notification.setDateEnvoi(LocalDateTime.now());
        notification.setUtilisateur(utilisateur);

        return NotificationpartDTO.fromEntity(notificationRepository.save(notification));
    }

    public List<NotificationpartDTO> getNotificationsUtilisateur(Long utilisateurId) {
        return notificationRepository.findNotificationsForUser(utilisateurId)
                .stream()
                .map(NotificationpartDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void softDeleteNotification(Long id) {
        notificationRepository.findById(id).ifPresent(notification -> {
            notification.setDeleted(true);
            notificationRepository.save(notification);
        });
    }
    @Transactional
    public List<NotificationpartDTO> markAllAsRead(Long userId) {
        // Marquer toutes les notifications comme lues (soft delete)
        notificationRepository.markAllAsRead(userId);

        // Retourner les notifications non lues restantes (devrait être une liste vide)
        return notificationRepository.findByUtilisateurIdAndDeletedFalse(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private NotificationpartDTO convertToDTO(Notificationpart notification) {
        return new NotificationpartDTO(
                notification.getId(),
                notification.getMessage(),
                notification.getDateEnvoi(),
                notification.getUtilisateur().getIdUtilisateur()
        );
    }
}
