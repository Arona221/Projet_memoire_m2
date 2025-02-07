package connect.event.equipeMarketing.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HubSpotService {

    @Value("${hubspot.api.key}")
    private String hubspotApiKey;

    public void suivreConversion(Long idCampagne, String type) {
        // Intégration HubSpot API pour suivre les conversions
        System.out.println("Conversion suivie pour la campagne " + idCampagne + " : " + type);
    }
}
