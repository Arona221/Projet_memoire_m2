package connect.event.equipeMarketing.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import connect.event.equipeMarketing.entity.CampagneMarketingUpdate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class FacebookAdsService {

    @Value("${facebook.page.access.token}") // Utilisez un jeton de page
    private String facebookPageAccessToken;

    @Value("${facebook.page.id}")
    private String facebookPageId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void publierCampagne(CampagneMarketingUpdate campagne) {
        try {
            String url = "https://graph.facebook.com/v15.0/" + facebookPageId + "/feed";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(facebookPageAccessToken);  // Utilisez le jeton de page
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = "{"
                    + "\"message\": \"" + campagne.getMessage().getTemplate() + "\","
                    + "\"link\": \"https://votresite.com/evenements/" + campagne.getEvenement().getId_evenement() + "\""
                    + "}";

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            try {
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    JsonNode jsonResponse = new ObjectMapper().readTree(response.getBody());
                    String postId = jsonResponse.get("id").asText();
                    System.out.println("Post Facebook créé ! ID: " + postId);
                    System.out.println("Lien direct: https://facebook.com/" + postId);
                    System.out.println("Campagne publiée sur Facebook avec succès !");
                } else {
                    System.err.println("Erreur lors de la publication sur Facebook : " + response.getStatusCode() + " - " + response.getBody());
                }
            } catch (HttpClientErrorException e) {
                System.err.println("Erreur détaillée API Facebook : "
                        + e.getStatusCode() + " - "
                        + e.getResponseBodyAsString());
                throw e;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la préparation de la requête Facebook : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
