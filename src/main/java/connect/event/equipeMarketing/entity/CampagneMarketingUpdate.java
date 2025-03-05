package connect.event.equipeMarketing.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import connect.event.entity.Evenement;
import connect.event.equipeMarketing.emuns.StatutCampagne;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "campagnes_marketingUpdate")
@ToString(exclude = {"evenement", "message", "segment"}) // Exclure les champs problématiques
public class CampagneMarketingUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private double budget;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String expediteurEmail;

    @Enumerated(EnumType.STRING)
    private StatutCampagne statut;

    @ManyToOne
    @JoinColumn(name = "id_evenement", nullable = false)
    @JsonIgnoreProperties({"billets", "organisateur"})
    private Evenement evenement;

    @OneToOne
    @JoinColumn(name = "id_message", nullable = false)
    private MessageMarketingUpdate message;

    @OneToOne
    @JoinColumn(name = "id_segment", nullable = false)
    private SegmentAudienceUpdate segment;
}