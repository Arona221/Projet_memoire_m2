package connect.event.equipeMarketing.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class EmailServiceMailchimp {

    @Value("${mailchimp.api.key}")
    private String mailchimpApiKey;

    @Value("${mailchimp.list.id}")
    private String mailchimpListId;

    private final RestTemplate restTemplate = new RestTemplate();

    public void envoyerEmail(List<String> criteres, String message) {
        String url = "https://usX.api.mailchimp.com/3.0/campaigns"; // Remplacez usX par votre région
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("anystring", mailchimpApiKey); // Utilisez "anystring" comme nom d'utilisateur
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construire le corps de la requête pour créer une campagne
        String requestBody = "{"
                + "\"type\": \"regular\","
                + "\"recipients\": {\"list_id\": \"" + mailchimpListId + "\"},"
                + "\"settings\": {"
                + "\"subject_line\": \"Promotion d'événement\","
                + "\"from_name\": \"Équipe Marketing\","
                + "\"reply_to\": \"marketing@example.com\","
                + "\"title\": \"Campagne de promotion\""
                + "},"
                + "\"content_type\": \"plaintext\","
                + "\"content\": {"
                + "\"plain_text\": \"" + message + "\""
                + "}"
                + "}";

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
            System.out.println("Email envoyé avec succès !");
        } else {
            System.out.println("Erreur lors de l'envoi de l'email : " + response.getBody());
        }
    }
}