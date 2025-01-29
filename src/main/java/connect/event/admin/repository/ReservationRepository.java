package connect.event.admin.repository;

import connect.event.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByRessourceIdAndDateAndHeure(Long idRessource, LocalDate date, LocalTime heure);
}

