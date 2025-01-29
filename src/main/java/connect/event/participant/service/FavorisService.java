package connect.event.participant.service;

import connect.event.entity.Utilisateur;
import connect.event.entity.Evenement;
import connect.event.participant.entity.Favoris;
import connect.event.participant.repository.FavorisRepository;
import connect.event.repository.UtilisateurRepository;
import connect.event.repository.EvenementRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FavorisService {

    @Autowired
    private FavorisRepository favorisRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private EvenementRepository evenementRepository;

    /**
     * Ajoute un événement aux favoris d'un utilisateur.
     *
     * @param idUtilisateur L'ID de l'utilisateur (participant).
     * @param idEvenement   L'ID de l'événement à ajouter aux favoris.
     */
    public void addToFavorites(Long idUtilisateur, Long idEvenement) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        Evenement evenement = evenementRepository.findById(idEvenement)
                .orElseThrow(() -> new IllegalArgumentException("Événement non trouvé"));

        // Vérifier si l'événement est déjà dans les favoris
        Optional<Favoris> favoris = favorisRepository.findByUtilisateurAndEvenement(utilisateur, evenement);
        if (favoris.isPresent()) {
            throw new IllegalArgumentException("Cet événement est déjà dans vos favoris.");
        }

        // Ajouter à la liste des favoris
        Favoris newFavoris = new Favoris();
        newFavoris.setUtilisateur(utilisateur);
        newFavoris.setEvenement(evenement);
        favorisRepository.save(newFavoris);
    }

    /**
     * Retire un événement des favoris d'un utilisateur.
     *
     * @param idUtilisateur L'ID de l'utilisateur (participant).
     * @param idEvenement   L'ID de l'événement à retirer des favoris.
     */
    @Transactional
    public void removeFromFavorites(Long idUtilisateur, Long idEvenement) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        Evenement evenement = evenementRepository.findById(idEvenement)
                .orElseThrow(() -> new IllegalArgumentException("Événement non trouvé"));

        // Vérifier si l'événement est dans les favoris
        Optional<Favoris> favoris = favorisRepository.findByUtilisateurAndEvenement(utilisateur, evenement);
        if (favoris.isEmpty()) {
            throw new IllegalArgumentException("Cet événement n'est pas dans vos favoris.");
        }

        // Supprimer de la liste des favoris
        favorisRepository.deleteByUtilisateurIdAndEvenementId(idUtilisateur, idEvenement);
    }
}
