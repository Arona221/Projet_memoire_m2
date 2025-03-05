package connect.event.equipeMarketing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Data
@NoArgsConstructor @AllArgsConstructor
@Entity
public class SegmentAudience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSegment;

    @ElementCollection
    @CollectionTable(name = "segment_critere", joinColumns = @JoinColumn(name = "idSegment"))
    @Column(name = "critere")
    private List<String> criteres; // Ex: ["age > 25", "localisation = Paris", "interet = musique"]

}
