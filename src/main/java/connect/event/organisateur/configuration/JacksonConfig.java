package connect.event.organisateur.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Module pour Java 8 date/time
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Locale;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Enregistre le module JavaTimeModule pour gérer LocalDateTime, LocalDate, etc.
        mapper.registerModule(new JavaTimeModule());

        // Désactive la sérialisation des dates en timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Active l'indentation pour une sortie JSON lisible (optionnel)
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Configure Jackson pour écrire les BigDecimal sous forme de chaînes brutes (évite la notation scientifique)
        mapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);

        // Définit la locale par défaut (optionnel)
        mapper.setLocale(Locale.US);

        return mapper;
    }
}