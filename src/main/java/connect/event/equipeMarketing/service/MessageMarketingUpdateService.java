package connect.event.equipeMarketing.service;

import connect.event.entity.Evenement;
import connect.event.equipeMarketing.emuns.Canal;
import connect.event.equipeMarketing.entity.Contact;
import connect.event.equipeMarketing.entity.MessageMarketingUpdate;
import connect.event.equipeMarketing.repository.MessageMarketingUpdateRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.*;
import java.time.format.DateTimeFormatter;


@Service
public class MessageMarketingUpdateService {

    @Autowired
    private MessageMarketingUpdateRepository messageRepository;

    /**
     * Crée un message de campagne personnalisé et l'enregistre dans la base de données.
     *
     * @param contacts  Les contacts à inclure dans la campagne.
     * @param message   Le message marketing de base.
     * @param evenement L'événement associé.
     * @return Le message de campagne personnalisé enregistré.
     */
    public MessageMarketingUpdate creerMessagePersonnaliser(List<Contact> contacts, MessageMarketingUpdate message, Evenement evenement) {
        // Générer le contenu personnalisé
        String contenuPersonnalise = genererContenuCampagne(contacts, message, evenement);

        // Créer un nouveau message avec le contenu personnalisé
        MessageMarketingUpdate messagePersonnalise = new MessageMarketingUpdate();
        messagePersonnalise.setSujet(message.getSujet());
        messagePersonnalise.setTemplate(contenuPersonnalise);
        messagePersonnalise.setCanal(message.getCanal());

        // Enregistrer le message dans la base de données
        return messageRepository.save(messagePersonnalise);
    }

    /**
     * Génère le contenu HTML de la campagne avec des merge tags Mailchimp.
     *
     * @param contacts  Les contacts à inclure dans la campagne.
     * @param message   Le message marketing.
     * @param evenement L'événement associé.
     * @return Le contenu HTML de la campagne.
     */
    String genererContenuCampagne(List<Contact> contacts, MessageMarketingUpdate message, Evenement evenement) {
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<html><body>");
        htmlContent.append("<h1>").append(message.getSujet()).append("</h1>");
        htmlContent.append("<p>Bonjour *|FNAME|*,</p>");
        htmlContent.append("<p>Nous sommes ravis de vous inviter à notre événement exclusif : *|EVENT_NAME|* !</p>");
        htmlContent.append("<p>Date : *|EVENT_DATE|* à *|EVENT_TIME|*</p>");
        htmlContent.append("<p>Lieu : *|EVENT_LOCATION|*</p>");
        htmlContent.append("<p>Cet événement est l'occasion idéale pour échanger avec des experts et découvrir les dernières innovations.</p>");
        htmlContent.append("<p>Pour réserver votre place, cliquez ici : <a href=\"*|EVENT_LINK|*\">*|EVENT_LINK|*</a></p>");
        htmlContent.append("<p>Nous avons hâte de vous y voir !</p>");
        htmlContent.append("<p>Cordialement,<br>L'équipe ConnectEvent</p>");
        htmlContent.append("</body></html>");
        return htmlContent.toString();
    }

    public MessageMarketingUpdate creerMessage(MessageMarketingUpdate message) {
        return messageRepository.save(message);
    }

    public List<MessageMarketingUpdate> getAllMessages() {
        return messageRepository.findAll();
    }

    public MessageMarketingUpdate getMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message non trouvé avec l'ID: " + id));
    }


    /**
     * Crée un message marketing personnalisé pour un événement donné.
     *
     * @param contacts  Les contacts à inclure dans la campagne.
     * @param message   Le message marketing existant (optionnel).
     * @param evenement L'événement pour lequel générer le message.
     * @return Le message marketing personnalisé.
     */
    @Transactional
    public MessageMarketingUpdate creerMessagePersonnalise(List<Contact> contacts, MessageMarketingUpdate message, Evenement evenement) {
        if (message == null) {
            message = new MessageMarketingUpdate();
        }

        // Définir le sujet du message
        message.setSujet("Ne manquez pas notre événement : " + evenement.getNom());

        // Construction du template HTML avec personnalisation
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<html><body>");
        htmlContent.append("<h1>").append(evenement.getNom()).append("</h1>");
        htmlContent.append("<p>Bonjour *|FNAME|*,</p>");  // Personnalisation du prénom
        htmlContent.append("<p>").append(evenement.getDescription()).append("</p>");
        htmlContent.append("<p><strong>Date :</strong> ").append(evenement.getDate()).append("</p>");
        htmlContent.append("<p><strong>Lieu :</strong> ").append(evenement.getLieu()).append("</p>");
        htmlContent.append("<p><strong>Catégorie :</strong> ").append(evenement.getCategorie()).append("</p>");
        htmlContent.append("<p><strong>Types de billets :</strong></p>");
        htmlContent.append("<ul>");

        evenement.getBillets().forEach(billet -> {
            htmlContent.append("<li>").append(billet.getTypeBillet()).append(" - ").append(billet.getPrix()).append("</li>");
        });

        htmlContent.append("</ul>");
        htmlContent.append("<p>Nous espérons vous voir à cet événement !</p>");
        htmlContent.append("<p>Cordialement,<br>L'équipe ConnectEvent</p>");
        htmlContent.append("</body></html>");

        // Définir le contenu HTML dans le message
        message.setTemplate(htmlContent.toString());
        message.setCanal(Canal.MAILCHIMP);

        // Enregistrer le message en base de données
        return messageRepository.save(message);
    }
}