package connect.event.equipeMarketing.DTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class PlanificationRequest {

    @Future(message = "La date doit être dans le futur")
    private LocalDate date;

    @NotNull(message = "L'heure est obligatoire")
    private LocalTime heure;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHeure() {
        return heure;
    }

    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }
}
