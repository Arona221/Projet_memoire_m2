package connect.event.service;

import connect.event.dto.ReservationDTO;
import connect.event.entity.Lieu;
import connect.event.entity.Reservation;
import connect.event.entity.Utilisateur;
import connect.event.enums.TypeUtilisateur;
import connect.event.repository.LieuRepository;
import connect.event.repository.ReservationRepository;
import connect.event.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private LieuRepository lieuRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Reservation reserverLieu(ReservationDTO reservationDTO) {
        Lieu lieu = lieuRepository.findById(reservationDTO.getIdLieu())
                .orElseThrow(() -> new IllegalArgumentException("Lieu introuvable"));

        Utilisateur utilisateur = utilisateurRepository.findById(reservationDTO.getIdUtilisateur())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        if (utilisateur.getTypeUtilisateur() != TypeUtilisateur.ORGANISATEUR) {
            throw new IllegalStateException("Seuls les utilisateurs de type ORGANISATEUR peuvent réserver un lieu.");
        }

        List<Reservation> conflits = reservationRepository.verifierDisponibilite(
                lieu.getIdLieu(),
                reservationDTO.getDateReservation(),
                reservationDTO.getHeureDebut(),
                reservationDTO.getHeureFin()
        );

        if (!conflits.isEmpty()) {
            throw new IllegalStateException("Le lieu est déjà réservé pour ce créneau horaire");
        }

        Reservation reservation = new Reservation();
        reservation.setLieu(lieu);
        reservation.setUtilisateur(utilisateur);
        reservation.setDateReservation(reservationDTO.getDateReservation());
        reservation.setHeureDebut(reservationDTO.getHeureDebut());
        reservation.setHeureFin(reservationDTO.getHeureFin());

        return reservationRepository.save(reservation);
    }
}
