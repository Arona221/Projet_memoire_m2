package connect.event.organisateur.service;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

public class OrangeTokenGenerator {

    private static final String TOKEN_URL = "https://api.orange.com/oauth/v3/token";
    private static final String CLIENT_ID = "HWjdyYz7GTaE50fVWSInQZe7H1WaY2VK";
    private static final String CLIENT_SECRET = "zP9QcQjpg8JJ253ZCYnGeS7D7gQl2EL04RTbeTyEwk2I";

    public static String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        // Encodage Base64 avec suppression du padding
        String authString = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedAuth = Base64.getEncoder().withoutPadding().encodeToString(authString.getBytes());
        System.out.println("Encoded Auth: " + encodedAuth); // Log pour vérifier l'encodage

        // Configuration des headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        System.out.println("Headers: " + headers); // Log pour vérifier les en-têtes

        // Encodage correct du body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        System.out.println("Body: " + body); // Log pour vérifier le corps

        // Création de la requête
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            // Appel à l'API Orange
            ResponseEntity<Map> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, entity, Map.class);

            // Vérification du statut HTTP et extraction du token
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                System.out.println("Réponse API : " + response.getBody());
                return response.getBody().get("access_token").toString();
            } else {
                throw new RuntimeException("Erreur lors de l'obtention du token: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Erreur Orange API : " + e.getStatusCode());
            System.err.println("Réponse de l'API : " + e.getResponseBodyAsString()); // Log pour afficher la réponse complète
            throw new RuntimeException("Erreur lors de l'obtention du token: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String accessToken = getAccessToken();
        System.out.println("Access Token: " + accessToken);
    }
}