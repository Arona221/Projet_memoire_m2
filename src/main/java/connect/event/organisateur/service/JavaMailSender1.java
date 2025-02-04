package connect.event.organisateur.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class JavaMailSender1 {
    @Autowired
    private JavaMailSender javaMailSender;

    public MimeMessage createMimeMessage() {
        return javaMailSender.createMimeMessage();
    }

    public void send(MimeMessage mimeMessage) {
        javaMailSender.send(mimeMessage);
    }
}
