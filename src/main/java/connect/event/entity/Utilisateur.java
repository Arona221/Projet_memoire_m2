package connect.event.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import connect.event.enums.TypeUtilisateur;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import lombok.ToString;

@Entity
@Data
@Table(name = "utilisateur")
@ToString(exclude = "evenements")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUtilisateur;

    @Column(nullable = false)
        private String prenom;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String email;
    private String phoneNumber;
    @JsonIgnore
    @Column(nullable = false)
    private String motDePasse;
    private String resetPasswordCode;
    private LocalDateTime resetPasswordCodeExpiration;

    @Enumerated(EnumType.STRING)
    private TypeUtilisateur typeUtilisateur;
    @OneToMany(mappedBy = "organisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Empêche la sérialisation infinie
    private List<Evenement> evenements;


    private Boolean active = false;
    private String codeValidation;
    private LocalDateTime codeValidationExpiration;

    @Version
    private Long version;

}
