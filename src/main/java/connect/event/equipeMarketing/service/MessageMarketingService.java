package connect.event.equipeMarketing.service;

import connect.event.entity.Evenement;
import connect.event.equipeMarketing.emuns.Canal;
import connect.event.equipeMarketing.entity.MessageMarketing;
import connect.event.equipeMarketing.repository.MessageMarketingRepository;
import connect.event.repository.EvenementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageMarketingService {

    @Autowired
    private MessageMarketingRepository messageRepository;

    @Autowired
    private EvenementRepository evenementRepository;

    public MessageMarketing creerMessage(Long idEvenement, String contenu, Canal canal) {
        // Récupérer l'événement par son ID
        Evenement evenement = evenementRepository.findById(idEvenement)
                .orElseThrow(() -> new RuntimeException("Événement non trouvé"));

        // Créer le message avec les détails de l'événement
        MessageMarketing message = new MessageMarketing();
        message.setContenu(contenu);
        message.setCanal(canal);
        message.setEvenement(evenement);

        // Ajouter les détails de l'événement au contenu du message
        String contenuAvecEvenement = contenu + "\n\nDétails de l'événement :\n"
                + "Nom : " + evenement.getNom() + "\n"
                + "Description : " + evenement.getDescription() + "\n"
                + "Date :" + evenement.getDate() + "\n"
                + "Lieu : " + evenement.getLieu() + "\n"
                + "Billet: " + evenement.getBillets() + "\n";

        message.setContenu(contenuAvecEvenement);

        return messageRepository.save(message);
    }

    public MessageMarketing getMessageById(Long id) {
        return messageRepository.findById(id).orElse(null);
    }
}
