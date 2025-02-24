package connect.event.repository;

import connect.event.entity.Reservation;
import connect.event.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByRessourceIdAndStartDateAndStartTime(Long idRessource, LocalDate startDate, LocalTime startTime);
    List<Reservation> findByOrganisateurIdUtilisateur(Long organisateurId);
    List<Reservation> findByStartDateBeforeAndStatut(LocalDate date, StatutReservation statut);
    boolean existsByRessourceIdAndStartDateBetween(Long ressourceId, LocalDate startDate, LocalDate endDate);  // Updated method
}
