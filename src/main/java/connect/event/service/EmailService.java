package connect.event.service;

public interface EmailService {
    void envoyerCodeValidation(String to, String code);
}
