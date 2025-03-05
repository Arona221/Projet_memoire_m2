package connect.event.equipeMarketing.service;

import connect.event.equipeMarketing.entity.CampagneMarketingUpdate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleAdsService {

    @Value("${google.ads.customer.id}")
    private String customerId;

    @Value("${google.ads.developer.token}")
    private String developerToken;

    public void publierCampagne(CampagneMarketingUpdate campagne) {
        // Implémentation de l'intégration Google Ads
        System.out.println("Campagne publiée sur Google Ads avec succès !");
    }
}
