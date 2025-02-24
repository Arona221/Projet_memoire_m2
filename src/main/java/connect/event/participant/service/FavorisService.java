package connect.event.participant.service;

import connect.event.entity.Evenement;
import connect.event.entity.Utilisateur;
import connect.event.participant.entity.Favoris;
import connect.event.participant.repository.FavorisRepository;
import connect.event.repository.EvenementRepository;
import connect.event.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavorisService {

    private final FavorisRepository favorisRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final EvenementRepository evenementRepository;

    @Transactional
    public void addToFavorites(Long idUtilisateur, Long idEvenement) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        Evenement evenement = evenementRepository.findById(idEvenement)
                .orElseThrow(() -> new IllegalArgumentException("Événement non trouvé"));

        if (favorisRepository.existsByUtilisateurIdAndEvenementId(idUtilisateur, idEvenement)) {
            throw new IllegalArgumentException("Événement déjà en favoris");
        }

        Favoris favoris = new Favoris();
        favoris.setUtilisateur(utilisateur);
        favoris.setEvenement(evenement);
        favorisRepository.save(favoris);
    }

    @Transactional
    public void removeFromFavorites(Long idUtilisateur, Long idEvenement) {
        if (!favorisRepository.existsByUtilisateurIdAndEvenementId(idUtilisateur, idEvenement)) {
            throw new IllegalArgumentException("Événement non trouvé dans les favoris");
        }
        favorisRepository.deleteByUtilisateurIdAndEvenementId(idUtilisateur, idEvenement);
    }

    public boolean isFavorite(Long idUtilisateur, Long idEvenement) {
        return favorisRepository.existsByUtilisateurIdAndEvenementId(idUtilisateur, idEvenement);
    }

    public Page<Evenement> getFavorisUtilisateur(Long utilisateurId, Pageable pageable) {
        return favorisRepository.findByUtilisateurIdUtilisateur(utilisateurId, pageable)
                .map(Favoris::getEvenement);
    }
}