package connect.event.repository;

import connect.event.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.lieu.idLieu = :idLieu " +
            "AND r.dateReservation = :dateReservation " +
            "AND (r.heureDebut < :heureFin AND r.heureFin > :heureDebut)")
    List<Reservation> verifierDisponibilite(Long idLieu, LocalDate dateReservation, LocalTime heureDebut, LocalTime heureFin);
}
