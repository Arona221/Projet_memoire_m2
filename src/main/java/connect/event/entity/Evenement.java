package connect.event.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import connect.event.enums.Categorie;
import connect.event.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Evenement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_evenement;
    private String nom;

    @Temporal(TemporalType.DATE)
    private Date date;
    private String lieu;
    private String heure;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Categorie categorie;

    @Enumerated(EnumType.STRING)
    private Status status = Status.EN_ATTENTE;

    private String imagePath; // Ajoutez ce champ

    @Column(name = "nombre_places", nullable = false)
    private int nombrePlaces;

    public Evenement(Long id_evenement) {
        this.id_evenement = id_evenement;
    }

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Gère la sérialisation dans un seul sens
    private List<Billet> billets = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "idOrganisateur", nullable = false)
    private Utilisateur organisateur;
}
