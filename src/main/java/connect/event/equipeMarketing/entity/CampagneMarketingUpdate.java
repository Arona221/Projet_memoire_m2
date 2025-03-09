package connect.event.equipeMarketing.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonProperty;
import connect.event.entity.Evenement;
import connect.event.equipeMarketing.emuns.StatutCampagne;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "campagnes_marketingUpdate")
@ToString(exclude = {"evenement", "message", "segment"}) // Exclure les champs problématiques
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class CampagneMarketingUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private double budget;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String expediteurEmail;

    @Enumerated(EnumType.STRING)
    private StatutCampagne statut;

    @ManyToOne
    @JoinColumn(name = "id_evenement", referencedColumnName = "id_evenement")
    @JsonIgnoreProperties({"billets", "organisateur"})
    private Evenement evenement;

    @OneToOne
    @JoinColumn(name = "id_message")
    // Retirer @MapsId car cela peut causer des problèmes de récursion
    private MessageMarketingUpdate message;

    @OneToOne
    @JoinColumn(name = "id_segment")
    // Retirer @MapsId car cela peut causer des problèmes de récursion
    private SegmentAudienceUpdate segment;
}