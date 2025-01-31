package connect.event.participant.service;

import connect.event.entity.Billet;
import connect.event.exception.PayDunyaException;
import connect.event.repository.BilletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PayDunyaService {

    @Value("${paydunya.master-key}")
    private String masterKey;

    @Value("${paydunya.private-key}")
    private String privateKey;

    @Value("${paydunya.public-key}")
    private String publicKey;

    @Value("${paydunya.token}")
    private String token;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private BilletRepository billetRepository;

    /**
     * Création d'une facture PayDunya pour l'achat d'un billet
     */
    public String createInvoice(Long billetId, int quantite, String callbackUrl) {
        String apiUrl = "https://app.paydunya.com/sandbox-api/v1/checkout-invoice/create";

        try {
            BigDecimal montantTotal = calculateTotalAmount(billetId, quantite);

            HttpHeaders headers = new HttpHeaders();
            headers.set("PAYDUNYA-MASTER-KEY", masterKey);
            headers.set("PAYDUNYA-PRIVATE-KEY", privateKey);
            headers.set("PAYDUNYA-PUBLIC-KEY", publicKey);
            headers.set("PAYDUNYA-TOKEN", token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Infos sur la boutique
            Map<String, Object> store = new HashMap<>();
            store.put("name", "ConnectEvent");
            store.put("tagline", "Rassemblez, vibrez, partagez");
            store.put("postal_address", "88888888");
            store.put("phone", "785923513");
            store.put("logo_url", "https://votre-site.com/logo.png");

            // Infos sur la facture
            Map<String, Object> invoice = new HashMap<>();
            invoice.put("total_amount", montantTotal);
            invoice.put("description", "Achat de " + quantite + " billets");
            invoice.put("callback_url", callbackUrl);
            invoice.put("return_url", "https://votre-site.com/success");
            invoice.put("cancel_url", "https://votre-site.com/cancel");
            invoice.put("customer_name", "Arona Ndiaye");
            invoice.put("customer_email", "arona10ndiaye@gmail.com");

            // Construction du JSON
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("store", store);  // Ajout de la boutique
            requestBody.put("invoice", invoice);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("response_code")) {
                throw new PayDunyaException("Réponse invalide de PayDunya");
            }

            // Vérification du code de réponse
            if (!"00".equals(responseBody.get("response_code").toString())) {
                throw new PayDunyaException("Erreur PayDunya : " + responseBody.get("response_text"));
            }

            // Récupération du lien de paiement
            Object invoiceUrl = responseBody.get("response_text");
            if (invoiceUrl == null) {
                throw new PayDunyaException("Erreur PayDunya : 'response_text' absent. Réponse complète : " + responseBody);
            }

            return invoiceUrl.toString();

        } catch (Exception e) {
            throw new PayDunyaException("Erreur lors de la création de la facture : " + e.getMessage(), e);
        }
    }

    /**
     * Calcule le montant total en fonction du prix du billet et de la quantité achetée
     */
    public BigDecimal calculateTotalAmount(Long billetId, int quantite) {
        Billet billet = findBilletById(billetId);
        return billet.getPrix().multiply(BigDecimal.valueOf(quantite));
    }

    /**
     * Recherche un billet par son ID
     */
    private Billet findBilletById(Long billetId) {
        Optional<Billet> optionalBillet = billetRepository.findById(billetId);
        if (optionalBillet.isEmpty()) {
            throw new RuntimeException("Billet non trouvé");
        }
        return optionalBillet.get();
    }

    /**
     * Vérifie si un paiement PayDunya est validé
     */
    public boolean verifierPaiement(String referenceTransaction) {
        String apiUrl = "https://app.paydunya.com/sandbox-api/v1/checkout-invoice/confirm/" + referenceTransaction;

        HttpHeaders headers = new HttpHeaders();
        headers.set("PAYDUNYA-MASTER-KEY", masterKey);
        headers.set("PAYDUNYA-PRIVATE-KEY", privateKey);
        headers.set("PAYDUNYA-PUBLIC-KEY", publicKey);
        headers.set("PAYDUNYA-TOKEN", token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, request, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("response_code")) {
            throw new PayDunyaException("Réponse invalide lors de la vérification du paiement");
        }

        return "00".equals(responseBody.get("response_code").toString());
    }
}
