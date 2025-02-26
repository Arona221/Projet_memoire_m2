package connect.event.participant.service;

import connect.event.entity.Billet;
import connect.event.exception.PayDunyaException;
import connect.event.participant.entity.BilletAcheter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

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

    public String createInvoice(List<BilletAcheter> billets,
                                BigDecimal montantTotal,
                                String customerEmail,
                                String callbackUrl) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("PAYDUNYA-MASTER-KEY", masterKey);
            headers.set("PAYDUNYA-PRIVATE-KEY", privateKey);
            headers.set("PAYDUNYA-PUBLIC-KEY", publicKey);
            headers.set("PAYDUNYA-TOKEN", token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Configuration de la boutique
            Map<String, Object> store = new HashMap<>();
            store.put("name", "ConnectEvent");
            store.put("website_url", "https://connect-event.com");
            store.put("logo_url", "https://connect-event.com/logo.png");

            // Items de la facture
            List<Map<String, Object>> items = new ArrayList<>();
            for (BilletAcheter billet : billets) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", billet.getBillet().getTypeBillet());
                item.put("quantity", billet.getQuantite());
                item.put("unit_price", billet.getBillet().getPrix().toString());
                item.put("total_price", billet.getMontantTotal().toString());
                items.add(item);
            }

            // Configuration de la facture
            Map<String, Object> invoice = new HashMap<>();
            invoice.put("items", items);
            invoice.put("total_amount", montantTotal.toString());
            invoice.put("description", "Achat de billets pour événement");
            invoice.put("callback_url", callbackUrl);
            invoice.put("return_url", "https://connect-event.com/paiement/success");
            invoice.put("cancel_url", "https://connect-event.com/paiement/cancel");
            invoice.put("customer", Map.of(
                    "email", customerEmail,
                    "name", billets.get(0).getParticipant().getNom()
            ));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("store", store);
            requestBody.put("invoice", invoice);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://app.paydunya.com/sandbox-api/v1/checkout-invoice/create",
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK &&
                    "00".equals(Objects.requireNonNull(response.getBody()).get("response_code").toString())) {
                return response.getBody().get("response_text").toString();
            } else {
                throw new PayDunyaException("Erreur PayDunya: " + response.getBody());
            }
        } catch (Exception e) {
            throw new PayDunyaException("Échec de création de facture: " + e.getMessage());
        }
    }

    public boolean verifierPaiement(String referenceTransaction) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("PAYDUNYA-MASTER-KEY", masterKey);
            headers.set("PAYDUNYA-PRIVATE-KEY", privateKey);
            headers.set("PAYDUNYA-PUBLIC-KEY", publicKey);
            headers.set("PAYDUNYA-TOKEN", token);

            HttpEntity<?> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://app.paydunya.com/sandbox-api/v1/checkout-invoice/confirm/" + referenceTransaction,
                    HttpMethod.GET,
                    request,
                    Map.class
            );

            return response.getStatusCode() == HttpStatus.OK &&
                    "success".equalsIgnoreCase(
                            Objects.requireNonNull(response.getBody()).get("status").toString()
                    );
        } catch (Exception e) {
            throw new PayDunyaException("Échec de vérification: " + e.getMessage());
        }
    }
    public boolean demanderRemboursement(String referenceTransaction, BigDecimal montant) {
        try {
            HttpHeaders headers = new HttpHeaders();
            // ... configuration des headers comme avant

            Map<String, Object> refundRequest = new HashMap<>();
            refundRequest.put("amount", montant.toString());
            refundRequest.put("reason", "Annulation billet");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(refundRequest, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://app.paydunya.com/sandbox-api/v1/refund/" + referenceTransaction,
                    request,
                    Map.class
            );

            return response.getStatusCode() == HttpStatus.OK
                    && "success".equalsIgnoreCase(response.getBody().get("status").toString());

        } catch (Exception e) {
            throw new PayDunyaException("Erreur de remboursement: " + e.getMessage());
        }
    }
}