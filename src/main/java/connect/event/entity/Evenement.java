package connect.event.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import connect.event.enums.Categorie;
import connect.event.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;


import lombok.ToString;

@Entity
@Table(name = "Evenement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"billets", "organisateur"}) // Exclure les champs problématiques
public class Evenement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evenement")
    private Long id_evenement;
    private String nom;

    @Column(name = "date")
    private LocalDateTime date;
    private String heure;
    private String lieu;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Categorie categorie;

    @Enumerated(EnumType.STRING)
    private Status status = Status.EN_ATTENTE;

    private String imagePath;
    private int nombrePlaces;


    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Billet> billets = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "idOrganisateur", nullable = false)
    private Utilisateur organisateur;


}