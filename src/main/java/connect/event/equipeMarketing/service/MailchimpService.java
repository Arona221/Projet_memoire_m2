package connect.event.equipeMarketing.service;

import connect.event.entity.Evenement;
import connect.event.equipeMarketing.entity.Contact;
import connect.event.equipeMarketing.entity.MessageMarketingUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import connect.event.equipeMarketing.repository.MessageMarketingUpdateRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class MailchimpService {

    private static final Logger logger = LoggerFactory.getLogger(MailchimpService.class);

    @Value("${mailchimp.api.key}")
    private String apiKey;

    @Value("${mailchimp.list.id}")
    private String listId;

    @Autowired
    private MessageMarketingUpdateService messageMarketingUpdateService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Envoie une campagne Mailchimp groupée.
     *
     * @param contacts  Les contacts à inclure dans la campagne.
     * @param message   Le message marketing à envoyer.
     * @param evenement L'événement associé à la campagne.
     */
    public void envoyerCampagneMailchimp(List<Contact> contacts, MessageMarketingUpdate message, Evenement evenement) {
        try {
            // Générer un message de campagne personnalisé
            MessageMarketingUpdate messagePersonnalise = messageMarketingUpdateService.creerMessagePersonnalise(contacts, message, evenement);

            // Vérification et extraction des composants de la clé API
            String[] apiKeyParts = apiKey.split("-");
            if (apiKeyParts.length < 2) {
                throw new IllegalArgumentException("Format de clé API invalide. Attendu : 'prefixe-datacenter'");
            }
            String apiKeyPrefix = apiKeyParts[0];
            String dataCenter = apiKeyParts[1];

            String apiUrl = "https://" + dataCenter + ".api.mailchimp.com/3.0";

            // Configuration des headers avec encodage UTF-8
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8));
            headers.setBasicAuth("apikey", apiKeyPrefix);

            // Étape 1 : Créer une campagne
            String campaignUrl = apiUrl + "/campaigns";
            ObjectNode campaignBody = objectMapper.createObjectNode();
            campaignBody.put("type", "regular");
            campaignBody.putObject("recipients").put("list_id", listId);
            campaignBody.putObject("settings")
                    .put("subject_line", messagePersonnalise.getSujet())
                    .put("from_name", "ConnectEvent")
                    .put("reply_to", "arona10ndiaye@gmail.com");

            HttpEntity<String> campaignRequest = new HttpEntity<>(objectMapper.writeValueAsString(campaignBody), headers);
            ResponseEntity<String> campaignResponse = restTemplate.postForEntity(campaignUrl, campaignRequest, String.class);

            if (!campaignResponse.getStatusCode().is2xxSuccessful()) {
                logger.error("Erreur lors de la création de la campagne : {}", campaignResponse.getBody());
                throw new RuntimeException("Erreur lors de la création de la campagne");
            }

            // Récupérer l'ID de la campagne créée
            String campaignId = objectMapper.readTree(campaignResponse.getBody()).path("id").asText();
            logger.info("Campagne créée avec l'ID : {}", campaignId);

            // Étape 2 : Ajouter le contenu à la campagne
            String contentUrl = apiUrl + "/campaigns/" + campaignId + "/content";
            ObjectNode contentBody = objectMapper.createObjectNode();
            contentBody.put("html", messagePersonnalise.getTemplate());

            HttpEntity<String> contentRequest = new HttpEntity<>(objectMapper.writeValueAsString(contentBody), headers);
            ResponseEntity<String> contentResponse = restTemplate.exchange(contentUrl, HttpMethod.PUT, contentRequest, String.class);

            if (!contentResponse.getStatusCode().is2xxSuccessful()) {
                logger.error("Erreur lors de l'ajout du contenu à la campagne : {}", contentResponse.getBody());
                throw new RuntimeException("Erreur lors de l'ajout du contenu à la campagne");
            }

            // Étape 3 : Envoyer la campagne
            String sendUrl = apiUrl + "/campaigns/" + campaignId + "/actions/send";
            restTemplate.postForObject(sendUrl, new HttpEntity<>("{}", headers), Void.class);

            logger.info("Campagne envoyée avec succès : {}", campaignId);

        } catch (HttpClientErrorException e) {
            logger.error("Erreur Mailchimp : {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Erreur Mailchimp : " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de la campagne Mailchimp", e);
            throw new RuntimeException("Erreur lors de l'envoi de la campagne Mailchimp: " + e.getMessage(), e);
        }
    }

    // Validation des adresses e-mail
    private boolean isEmailValid(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
}