package connect.event.participant.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class BilletParticipantDTO {
    private Long id;
    private String eventNom;
    private String eventLieu;
    private Date eventDate;
    private String heure;
    private String typeBillet;
    private int quantite;
    private BigDecimal montantTotal;
    private String statut;
    private String referenceTransaction;
    private String participantNom;
    private String participantPrenom;
    private String participantEmail;
    private String qrCodeUrl;
    private String qrCodeEncrypted;
}