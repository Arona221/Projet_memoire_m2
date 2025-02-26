package connect.event.organisateur.DTO;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor @NoArgsConstructor @Getter
@Setter
@ToString
public class NotificationDTO {
    private Long eventId;
    private Long idNotification;
    private LocalDate date;
    private String contenu;
    private String typeNotification;
}
