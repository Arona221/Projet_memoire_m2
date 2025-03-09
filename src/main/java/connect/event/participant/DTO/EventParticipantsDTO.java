package connect.event.participant.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventParticipantsDTO {
    private Long eventId;
    private String eventName;
    private LocalDateTime eventDate;
    private Long participantCount; // Changé de Integer à Long
    private Long totalTicketsSold; // Changé de Integer à Long
}
