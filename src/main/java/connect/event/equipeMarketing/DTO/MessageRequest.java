package connect.event.equipeMarketing.DTO;

import connect.event.equipeMarketing.emuns.Canal;
import lombok.*;

@Data @NoArgsConstructor
@AllArgsConstructor
@Setter @Getter
public class MessageRequest {
    private Long idEvenement;
    private String contenu;
    private Canal canal;

    // Getters and Setters
}
