package connect.event.organisateur.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioWhatsAppService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String fromWhatsAppNumber;

    public void sendWhatsAppMessage(String toNumber, String message) {
        // Initialiser Twilio
        Twilio.init(accountSid, authToken);

        // Envoyer un message WhatsApp
        Message.creator(
                new PhoneNumber("whatsapp:" + toNumber),
                new PhoneNumber(fromWhatsAppNumber),
                message
        ).create();
    }
}
