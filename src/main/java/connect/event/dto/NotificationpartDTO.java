package connect.event.dto;

import connect.event.entity.Notificationpart;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationpartDTO {
    private Long id;
    private String message;
    private LocalDateTime dateEnvoi;
    private Long utilisateurId;

    public static NotificationpartDTO fromEntity(Notificationpart notification) {
        return new NotificationpartDTO(
                notification.getId(),
                notification.getMessage(),
                notification.getDateEnvoi(),
                notification.getUtilisateur().getIdUtilisateur()
        );
    }
}