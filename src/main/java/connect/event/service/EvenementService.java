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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

@Service
public class EvenementService {

    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(EvenementService.class);

    private static final String UPLOAD_DIR = "uploads"; // Chemin relatif au projet

    /**
     * Récupère tous les événements et les retourne sous forme de DTOs.
     */
    public List<EvenementDTO> getAllEvenements() {
        return evenementRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un événement par son ID.
     */
    public EvenementDTO getEvenementById(Long id) {
        validateId(id);
        return evenementRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    /**
     * Enregistre ou met à jour un événement.
     */
    public Evenement createEvenement(EvenementDTO evenementDTO, Long idUtilisateur, MultipartFile imageFile) throws IOException {
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        if (utilisateur.getTypeUtilisateur() != TypeUtilisateur.ORGANISATEUR) {
            throw new IllegalArgumentException("L'utilisateur doit être un organisateur.");
        }

        Evenement evenement = new Evenement();
        evenement.setNom(evenementDTO.getNom());
        evenement.setDate(evenementDTO.getDate());
        evenement.setLieu(evenementDTO.getLieu());
        evenement.setDescription(evenementDTO.getDescription());
        evenement.setCategorie(evenementDTO.getCategorie());
        evenement.setStatus(Status.EN_ATTENTE);
        evenement.setNombrePlaces(evenementDTO.getNombrePlaces());
        evenement.setOrganisateur(utilisateur);
        evenement.setHeure(evenementDTO.getHeure());

        // Gestion de l'image
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            evenement.setImagePath(imagePath); // Stocke uniquement le nom du fichier
        }

        // Conversion des billets DTO en entités
        List<Billet> billets = evenementDTO.getBillets().stream().map(billetDTO -> {
            Billet billet = new Billet();
            billet.setTypeBillet(billetDTO.getTypeBillet());
            billet.setPrix(billetDTO.getPrix());
            billet.setQuantite(billetDTO.getQuantite());
            billet.setEvenement(evenement);
            return billet;
        }).collect(Collectors.toList());

        evenement.setBillets(billets);

        return evenementRepository.save(evenement);
    }
    /**
     * Supprime un événement par son ID.
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
                evenement.getImagePath(),
                billetsDTO,
                evenement.getId_evenement(),
                evenement.getHeure()  // Incluez l'heure ici
        );
    }

    @Transactional
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
     */
    public List<EvenementDTO> searchEvenements(Categorie categorie, Date date, String lieu) {
        if (categorie == null && date == null && lieu == null) {
            return evenementRepository.findAll()
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        if (categorie != null && date != null && lieu != null) {
            return evenementRepository.findByCategorieAndDateAndLieu(categorie, date, lieu)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

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

        return Collections.emptyList();
    }

    public Page<EvenementDTO> getEvenementsByOrganisateur(Long idOrganisateur, int page, int size, String search, Status status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Evenement> evenementsPage = evenementRepository.findByOrganisateur_IdUtilisateur(idOrganisateur, pageable);
        return evenementsPage.map(this::convertToDTO);
    }

    public String saveImage(MultipartFile imageFile) throws IOException {
        if (imageFile.isEmpty()) {
            throw new IOException("Le fichier est vide.");
        }

        String uploadDirectory = "C:/Users/Arona Ndiaye/OneDrive/Documents/Document/Memoire_M2/Projet_memoire_m2/images"; // Chemin de stockage des images
        File directory = new File(uploadDirectory);
        if (!directory.exists()) {
            directory.mkdirs(); // Crée le répertoire s'il n'existe pas
        }

        String originalFilename = imageFile.getOriginalFilename();
        String fileName = UUID.randomUUID() + "_" + originalFilename; // Nom unique pour éviter les conflits
        Path path = Paths.get(uploadDirectory, fileName);
        imageFile.transferTo(path.toFile());

        return fileName; // Retourne uniquement le nom du fichier
    }
}
