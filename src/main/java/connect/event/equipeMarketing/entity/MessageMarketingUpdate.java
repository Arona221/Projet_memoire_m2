package connect.event.equipeMarketing.entity;

import connect.event.equipeMarketing.emuns.Canal;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MessageMarketingUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMessage;

    private String sujet; // Sujet de l'e-mail

    @Lob
    @Column(columnDefinition = "TEXT") // Stockage en format TEXT en base
    private String template; // Contenu du message avec variables

    @Enumerated(EnumType.STRING)
    private Canal canal;
}
