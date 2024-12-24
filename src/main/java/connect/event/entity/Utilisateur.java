package connect.event.entity;

import connect.event.enums.TypeUtilisateur;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "utilisateur")
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

    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    private TypeUtilisateur typeUtilisateur;

    private Boolean active = false;
    private String codeValidation;
    private LocalDateTime codeValidationExpiration;

    @Version
    private Long version;
}
