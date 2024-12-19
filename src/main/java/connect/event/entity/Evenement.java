package connect.event.entity;

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

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Categorie categorie;

    @Enumerated(EnumType.STRING)
    private Status status = Status.EN_ATTENTE;

    private String image;

    @Column(name = "nombre_places", nullable = false)
    private int nombrePlaces;

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Billet> billets = new ArrayList<>();

    //@ManyToOne
    //@JoinColumn(name = "idOrganisateur", nullable = false)
    //private Organisateur organisateur;
}