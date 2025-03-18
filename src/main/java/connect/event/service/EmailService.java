package connect.event.service;

public interface EmailService {
    void envoyerCodeValidation(String to, String code);
    void envoyerNotificationStatut(String to, String prenom, String nom, String nomEvenement, String statut);
    void envoyerResetPasswordEmail(String to, String code);
}
