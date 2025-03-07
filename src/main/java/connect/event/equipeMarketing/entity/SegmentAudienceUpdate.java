package connect.event.equipeMarketing.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "segments_audienceUpdate")
public class SegmentAudienceUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_segment")
    @JsonProperty("id_segment") // Correspondance avec JSON
    private Long idSegment;

    @ElementCollection
    @CollectionTable(name = "segment_criteresUpdate",
            joinColumns = @JoinColumn(name = "segment_id"))
    @Column(name = "critere")
    private List<String> criteres; // Ex: ["age > 25", "localisation = Paris"]

    @OneToMany(mappedBy = "segment", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private List<Contact> contacts;
}
