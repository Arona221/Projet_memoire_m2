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
     * Crée un message de campagne marketing personnalisé et l'enregistre dans la base de données.
     *
     * @param contacts  Liste des contacts ciblés.
     * @param message   Modèle du message marketing.
     * @param evenement L'événement concerné.
     * @return Un message marketing personnalisé.
     */
    @Transactional
    public MessageMarketingUpdate creerMessagePersonnalise(List<Contact> contacts, MessageMarketingUpdate message, Evenement evenement) {
        if (message == null) {
            message = new MessageMarketingUpdate();
        }

        // ✅ Définir le sujet du message
        message.setSujet("📢 Invitation Exclusive : " + evenement.getNom());

        // ✅ Construction du template HTML professionnel
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<html><body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>");

        // ✅ En-tête stylisé avec une bannière colorée
        htmlContent.append("<div style='background-color: #ff6600; padding: 20px; text-align: center; color: #fff;'>");
        htmlContent.append("<h1 style='margin: 0;'>" + evenement.getNom() + "</h1>");
        htmlContent.append("</div>");

        htmlContent.append("<div style='padding: 20px;'>");

        // ✅ Message d’introduction engageant
        htmlContent.append("<p>Bonjour <strong>*|FNAME|*</strong>,</p>");
        htmlContent.append("<p>Nous avons le plaisir de vous inviter à un événement exclusif qui promet une expérience inoubliable :</p>");

        // ✅ Détails de l’événement sous forme de tableau bien structuré
        htmlContent.append("<table style='width: 100%; border-collapse: collapse; margin-top: 20px;'>");
        htmlContent.append("<tr><td style='padding: 10px; font-weight: bold;'>📅 Date :</td><td>" + evenement.getDate() + "</td></tr>");
        htmlContent.append("<tr><td style='padding: 10px; font-weight: bold;'>📍 Lieu :</td><td>" + evenement.getLieu() + "</td></tr>");
        htmlContent.append("<tr><td style='padding: 10px; font-weight: bold;'>🎭 Catégorie :</td><td>" + evenement.getCategorie() + "</td></tr>");
        htmlContent.append("</table>");

        // ✅ Description de l’événement avec un ton engageant
        htmlContent.append("<p style='margin-top: 20px;'>" + evenement.getDescription() + "</p>");

        // ✅ Liste des types de billets avec un design amélioré
        htmlContent.append("<h3 style='color: #ff6600;'>🎟️ Types de Billets Disponibles :</h3>");
        htmlContent.append("<ul style='list-style-type: none; padding: 0;'>");
        evenement.getBillets().forEach(billet -> {
            htmlContent.append("<li style='padding: 10px; background-color: #f8f8f8; margin-bottom: 5px; border-left: 4px solid #ff6600;'>");
            htmlContent.append("<strong>" + billet.getTypeBillet() + "</strong> - <span style='color: #ff6600; font-weight: bold;'>" + billet.getPrix() + " FCFA</span>");
            htmlContent.append("</li>");
        });
        htmlContent.append("</ul>");

        // ✅ Bouton d'inscription bien visible
        htmlContent.append("<div style='text-align: center; margin-top: 30px;'>");
        htmlContent.append("<a href='*|EVENT_LINK|*' style='display: inline-block; padding: 15px 25px; background-color: #ff6600; color: #fff; text-decoration: none; font-size: 16px; border-radius: 5px;'>");
        htmlContent.append("🎟️ Réservez Votre Place");
        htmlContent.append("</a>");
        htmlContent.append("</div>");

        // ✅ Message de clôture
        htmlContent.append("<p style='margin-top: 30px;'>Nous espérons vous voir parmi nous et partager ensemble une expérience inoubliable !</p>");
        htmlContent.append("<p><em>Cordialement,<br>L'équipe ConnectEvent</em></p>");

        htmlContent.append("</div>"); // Fermeture de la div principale
        htmlContent.append("</body></html>");

        // ✅ Définir le contenu HTML dans le message
        message.setTemplate(htmlContent.toString());
        message.setCanal(Canal.MAILCHIMP);

        // ✅ Enregistrer le message en base de données
        return messageRepository.save(message);
    }
}