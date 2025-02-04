package connect.event.organisateur.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class SmsService {

    @Value("${orange.api_url}")
    private String apiUrl;  // L'URL de l'API d'Orange pour l'envoi de SMS

    private final RestTemplate restTemplate;

    public SmsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendSms(String accessToken, String recipientPhone, String message) {
        try {
            // Construction du payload
            Map<String, Object> smsRequest = new HashMap<>();
            smsRequest.put("outboundSMSMessageRequest", new HashMap<String, Object>() {{
                put("address", "tel:" + recipientPhone);
                put("senderAddress", "tel:+221785923513");  // Remplacez par votre numéro d'envoi
                put("outboundSMSTextMessage", new HashMap<String, String>() {{
                    put("message", message);
                }});
            }});

            // Configuration des headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Création de la requête
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(smsRequest, headers);

            // Envoi de la requête à l'API Orange
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("✅ SMS envoyé avec succès");
            } else {
                System.out.println("❌ Échec de l'envoi du SMS : " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi du SMS: " + e.getMessage());
        }
    }
}
