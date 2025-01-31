package connect.event.service;
import connect.event.admin.entity.Ressource;
import connect.event.repository.ReservationRepository;
import connect.event.admin.repository.RessourceRepository;
import connect.event.dto.ReservationDTO;
import connect.event.entity.Reservation;
import connect.event.entity.Utilisateur;
import connect.event.enums.TypeUtilisateur;
import connect.event.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RessourceRepository ressourceRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public ReservationDTO reserverRessource(Long idOrganisateur, Long idRessource, ReservationDTO reservationDTO) {
        Optional<Utilisateur> organisateurOpt = utilisateurRepository.findById(idOrganisateur);
        Optional<Ressource> ressourceOpt = ressourceRepository.findById(idRessource);

        if (organisateurOpt.isEmpty() || ressourceOpt.isEmpty()) {
            throw new RuntimeException("Organisateur ou ressource introuvable.");
        }

        Utilisateur organisateur = organisateurOpt.get();
        if (organisateur.getTypeUtilisateur() != TypeUtilisateur.ORGANISATEUR) {
            throw new RuntimeException("Seul un organisateur peut réserver une ressource.");
        }

        if (reservationRepository.findByRessourceIdAndDateAndHeure(idRessource, reservationDTO.getDate(), reservationDTO.getHeure()).isPresent()) {
            throw new RuntimeException("Ressource déjà réservée pour cet horaire.");
        }

        Reservation reservation = new Reservation();
        reservation.setOrganisateur(organisateur);
        reservation.setRessource(ressourceOpt.get());
        reservation.setDate(reservationDTO.getDate());
        reservation.setHeure(reservationDTO.getHeure());

        return ReservationDTO.fromEntity(reservationRepository.save(reservation));
    }
}

