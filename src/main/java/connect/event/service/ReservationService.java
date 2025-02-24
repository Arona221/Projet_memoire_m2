package connect.event.service;
import connect.event.admin.entity.Ressource;
import connect.event.enums.StatutReservation;
import connect.event.repository.ReservationRepository;
import connect.event.admin.repository.RessourceRepository;
import connect.event.dto.ReservationDTO;
import connect.event.entity.Reservation;
import connect.event.entity.Utilisateur;
import connect.event.enums.TypeUtilisateur;
import connect.event.repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RessourceRepository ressourceRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private NotificationpartService notificationService;

    @Transactional
    public ReservationDTO reserverRessource(Long idOrganisateur, Long idRessource, ReservationDTO reservationDTO) {
        Utilisateur organisateur = utilisateurRepository.findById(idOrganisateur)
                .orElseThrow(() -> new RuntimeException("Organisateur non trouvé"));

        if (organisateur.getTypeUtilisateur() != TypeUtilisateur.ORGANISATEUR) {
            throw new RuntimeException("Seul un organisateur peut réserver une ressource.");
        }

        Ressource ressource = ressourceRepository.findById(idRessource)
                .orElseThrow(() -> new RuntimeException("Ressource non trouvée"));

        // Vérifier si la ressource est déjà réservée pour cette période
        if (reservationDTO.getStartDate() != null && reservationDTO.getEndDate() != null) {
            // Réservation multi-jours
            if (reservationRepository.existsByRessourceIdAndStartDateBetween(idRessource, reservationDTO.getStartDate(), reservationDTO.getEndDate())) {
                throw new RuntimeException("Ressource déjà réservée pour cette période.");
            }
        } else if (reservationDTO.getStartDate() != null && reservationDTO.getStartTime() != null) {
            // Réservation quotidienne
            if (reservationRepository.findByRessourceIdAndStartDateAndStartTime(
                    idRessource,
                    reservationDTO.getStartDate(),
                    reservationDTO.getStartTime()
            ).isPresent()) {
                throw new RuntimeException("Ressource déjà réservée pour cet horaire.");
            }
        }

        Reservation reservation = new Reservation();
        reservation.setOrganisateur(organisateur);
        reservation.setRessource(ressource);

// Si tu veux réserver pour une période avec une date de début et de fin
        reservation.setStartDate(reservationDTO.getStartDate());
        reservation.setEndDate(reservationDTO.getEndDate());

// Et les horaires de début et de fin
        reservation.setStartTime(reservationDTO.getStartTime());
        reservation.setEndTime(reservationDTO.getEndTime());

// Statut de la réservation
        reservation.setStatut(StatutReservation.EN_ATTENTE);

// Sauvegarde de la réservation
        Reservation savedReservation = reservationRepository.save(reservation);


        // Créer une notification
        String message = "Votre réservation pour " + ressource.getNom() + " est en attente.";
        notificationService.creerNotification(idOrganisateur, message);

        return ReservationDTO.fromEntity(savedReservation);
    }
    // Nouvelle méthode pour admin
    @Transactional
    public ReservationDTO updateStatutReservation(Long reservationId, StatutReservation nouveauStatut, Long adminId) {
        Utilisateur admin = utilisateurRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin non trouvé"));

        if (admin.getTypeUtilisateur() != TypeUtilisateur.EQUIPEMARKETING) {
            throw new RuntimeException("Accès refusé");
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        reservation.setStatut(nouveauStatut);
        Reservation updatedReservation = reservationRepository.save(reservation);

        // Notification à l'utilisateur
        String message = String.format(
                "Bonjour %s %s, votre réservation pour %s est maintenant %s.",
                reservation.getOrganisateur().getPrenom(),
                reservation.getOrganisateur().getNom(),
                reservation.getRessource().getNom(),
                nouveauStatut.toString()
        );
        notificationService.creerNotification(
                reservation.getOrganisateur().getIdUtilisateur(),
                message
        );

        return ReservationDTO.fromEntity(updatedReservation);
    }

    // Tâche planifiée pour le statut PASSER
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void mettreAJourReservationsPassees() {
        LocalDate aujourdhui = LocalDate.now();
        List<Reservation> reservations = reservationRepository
                .findByStartDateBeforeAndStatut(aujourdhui, StatutReservation.EN_ATTENTE);

        reservations.forEach(reservation -> {
            reservation.setStatut(StatutReservation.PASSER);
            reservationRepository.save(reservation);

            String message = String.format(
                    "Votre réservation pour %s est expirée (date passée).",
                    reservation.getRessource().getNom()
            );
            notificationService.creerNotification(
                    reservation.getOrganisateur().getIdUtilisateur(),
                    message
            );
        });
    }
    public List<ReservationDTO> getReservationsByOrganisateur(Long organisateurId) {
        List<Reservation> reservations = reservationRepository.findByOrganisateurIdUtilisateur(organisateurId);
        return reservations.stream()
                .map(ReservationDTO::fromEntity)
                .collect(Collectors.toList());
    }
    @Transactional
    public ReservationDTO annulerReservation(Long reservationId, Long organisateurId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        // Vérifier que l'utilisateur est bien l'organisateur de la réservation
        if (!reservation.getOrganisateur().getIdUtilisateur().equals(organisateurId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à annuler cette réservation.");
        }

        // Vérifier si la réservation est déjà passée
        if (reservation.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Impossible d'annuler une réservation déjà passée.");
        }

        // Modifier le statut à ANNULÉ
        reservation.setStatut(StatutReservation.ANNULLER);
        Reservation updatedReservation = reservationRepository.save(reservation);

        // Envoyer une notification
        String message = String.format(
                "Votre réservation pour %s a été annulée.",
                reservation.getRessource().getNom()
        );
        notificationService.creerNotification(organisateurId, message);

        return ReservationDTO.fromEntity(updatedReservation);
    }


}

