package connect.event.organisateur.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;
import java.util.Map;

@Service
public class OrangeApiService {

    @Value("${orange.client_id}")
    private String clientId;

    @Value("${orange.client_secret}")
    private String clientSecret;

    private static final String TOKEN_URL = "https://api.orange.com/oauth/v3/token";

    private final RestTemplate restTemplate;

    public OrangeApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getAccessToken() {
        try {
            // Encodage Base64 de client_id:client_secret
            String authHeader = Base64.getEncoder()
                    .encodeToString((clientId + ":" + clientSecret).getBytes());

            // Configuration des headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + authHeader);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Création de la requête
            HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);

            // Appel à l'API Orange
            ResponseEntity<Map> response = restTemplate.exchange(
                    TOKEN_URL, HttpMethod.POST, entity, Map.class);

            // Vérification du statut HTTP
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().get("access_token").toString();
            } else {
                throw new RuntimeException("Erreur lors de l'obtention du token: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur d'authentification à Orange API: " + e.getMessage());
        }
    }
}
