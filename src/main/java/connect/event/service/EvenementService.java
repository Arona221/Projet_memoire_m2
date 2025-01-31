package connect.event.service;

import connect.event.dto.EvenementDTO;
import connect.event.dto.BilletDTO;
import connect.event.entity.Billet;
import connect.event.entity.Evenement;
import connect.event.entity.Utilisateur;
import connect.event.enums.Categorie;
import connect.event.enums.Status;
import connect.event.enums.TypeUtilisateur;
import connect.event.repository.EvenementRepository;
import connect.event.repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvenementService {

    @Autowired
    private EvenementRepository evenementRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private EmailService emailService;
    @Transactional

    /**
     * Récupère tous les événements et les retourne sous forme de DTOs.
     *
     * @return Liste des EvenementDTO.
     */
    public List<EvenementDTO> getAllEvenements() {
        return evenementRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un événement par son ID.
     *
     * @param id Identifiant de l'événement.
     * @return EvenementDTO correspondant, ou null si introuvable.
     */
    public EvenementDTO getEvenementById(Long id) {
        validateId(id);
        return evenementRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * Enregistre ou met à jour un événement.
     *
     * @param evenementDTO Les données de l'événement sous forme de DTO.
     * @return Le DTO de l'événement enregistré ou mis à jour.
     */



    public Evenement createEvenement(EvenementDTO evenementDTO, Long idUtilisateur) {
        // Récupérer l'utilisateur par son ID
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        // Vérifier si l'utilisateur est bien un organisateur
        if (utilisateur.getTypeUtilisateur() != TypeUtilisateur.ORGANISATEUR) {
            throw new IllegalArgumentException("L'utilisateur doit être un organisateur.");
        }

        Evenement evenement = new Evenement();
        evenement.setNom(evenementDTO.getNom());
        evenement.setDate(evenementDTO.getDate());
        evenement.setLieu(evenementDTO.getLieu());
        evenement.setDescription(evenementDTO.getDescription());
        evenement.setCategorie(evenementDTO.getCategorie());
        evenement.setStatus(evenementDTO.getStatus());
        evenement.setImage(evenementDTO.getImage());
        evenement.setNombrePlaces(evenementDTO.getNombrePlaces());

        // Définir le statut par défaut à EN_ATTENTE
        evenement.setStatus(Status.EN_ATTENTE);
        // Associer l'organisateur à l'événement
        evenement.setOrganisateur(utilisateur);

        // Conversion des billets DTO en entités
        List<Billet> billets = evenementDTO.getBillets().stream().map(billetDTO -> {
            Billet billet = new Billet();
            billet.setTypeBillet(billetDTO.getTypeBillet());
            billet.setPrix(billetDTO.getPrix());
            billet.setQuantite(billetDTO.getQuantite());
            billet.setEvenement(evenement); // Lien avec l'événement
            return billet;
        }).collect(Collectors.toList());

        evenement.setBillets(billets);

        // Sauvegarde dans le dépôt
        return evenementRepository.save(evenement);
    }


    /**
     * Supprime un événement par son ID.
     *
     * @param id Identifiant de l'événement à supprimer.
     */
    public void deleteEvenement(Long id) {
        validateId(id);
        evenementRepository.deleteById(id);
    }

    // Méthodes privées

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'identifiant ne peut pas être null.");
        }
    }

    private void validateEvenementDTO(EvenementDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("L'objet EvenementDTO ne peut pas être null.");
        }
        if (!StringUtils.hasText(dto.getNom())) {
            throw new IllegalArgumentException("Le nom de l'événement est obligatoire.");
        }
        if (dto.getDate() == null) {
            throw new IllegalArgumentException("La date de l'événement est obligatoire.");
        }
        if (!StringUtils.hasText(dto.getLieu())) {
            throw new IllegalArgumentException("Le lieu de l'événement est obligatoire.");
        }
    }

    private EvenementDTO convertToDTO(Evenement evenement) {
        List<BilletDTO> billetsDTO = evenement.getBillets()
                .stream()
                .map(billet -> new BilletDTO(
                        billet.getTypeBillet(),
                        billet.getPrix(),
                        billet.getQuantite()))
                .collect(Collectors.toList());

        return new EvenementDTO(
                evenement.getNom(),
                evenement.getDate(),
                evenement.getDescription(),
                evenement.getLieu(),
                evenement.getCategorie(),
                evenement.getStatus(),
                evenement.getNombrePlaces(),
                evenement.getImage(),
                billetsDTO,
                evenement.getId_evenement()
        );
    }

    private Evenement convertToEntity(EvenementDTO dto) {
        Evenement evenement = new Evenement();
        evenement.setNom(dto.getNom());
        evenement.setDate(dto.getDate());
        evenement.setLieu(dto.getLieu());
        evenement.setDescription(dto.getDescription());
        evenement.setCategorie(dto.getCategorie());
        evenement.setNombrePlaces(dto.getNombrePlaces());
        evenement.setImage(dto.getImage());
        evenement.setStatus(dto.getStatus());
        return evenement;
    }

    public boolean updateStatus(Long idEvenement, Status status) {
        Evenement evenement = evenementRepository.findById(idEvenement)
                .orElseThrow(() -> new IllegalArgumentException("Événement introuvable"));

        // Vérifier si l'événement est déjà approuvé ou annulé
        if (evenement.getStatus() == Status.APPROUVE || evenement.getStatus() == Status.ANNULE) {
            throw new IllegalStateException("L'événement a déjà un statut final.");
        }

        evenement.setStatus(status);
        evenementRepository.save(evenement);

        // Envoyer un email à l'organisateur
        Utilisateur organisateur = evenement.getOrganisateur();
        emailService.envoyerNotificationStatut(
                organisateur.getEmail(),
                organisateur.getPrenom(),
                organisateur.getNom(),
                evenement.getNom(),
                status.name()
        );

        return true;
    }
    /**
     * Recherche des événements en fonction des filtres.
     *
     * @param categorie La catégorie d'événements (optionnel).
     * @param date      La date de l'événement (optionnel).
     * @param lieu      Le lieu de l'événement (optionnel).
     * @return Liste des événements filtrés.
     */
    public List<EvenementDTO> searchEvenements(Categorie categorie, Date date, String lieu) {
        // Si tous les paramètres sont null, on renvoie tous les événements
        if (categorie == null && date == null && lieu == null) {
            return evenementRepository.findAll()
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        // Si la catégorie est non nulle, on la filtre
        if (categorie != null && date != null && lieu != null) {
            // Tous les paramètres sont fournis
            return evenementRepository.findByCategorieAndDateAndLieu(categorie, date, lieu)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        // Recherche avec un seul ou deux filtres
        if (categorie != null && date != null) {
            return evenementRepository.findByCategorieAndDate(categorie, date)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        if (categorie != null && lieu != null) {
            return evenementRepository.findByCategorieAndLieu(categorie, lieu)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        if (date != null && lieu != null) {
            return evenementRepository.findByDateAndLieu(date, lieu)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        // Recherche avec un seul filtre
        if (categorie != null) {
            return evenementRepository.findByCategorie(categorie)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        if (date != null) {
            return evenementRepository.findByDate(date)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        if (lieu != null) {
            return evenementRepository.findByLieu(lieu)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        // Par défaut, si aucun paramètre n'est donné
        return Collections.emptyList(); // Aucun événement trouvé
    }



}
