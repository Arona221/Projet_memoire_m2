package connect.event.equipeMarketing.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String prenom;
    private String nom;

    // Relation bidirectionnelle avec SegmentAudienceUpdate

    @ManyToOne
    @JoinColumn(name = "segment_id")
    @JsonBackReference // ◄▲ Gère la désérialisation côté enfant
    private SegmentAudienceUpdate segment;
}