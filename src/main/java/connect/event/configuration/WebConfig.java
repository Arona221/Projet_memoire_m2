package connect.event.configuration; // Ajustez le package selon votre structure

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig implements WebMvcConfigurer {

    private static final String UPLOAD_DIR = "file:C:/Users/Arona Ndiaye/OneDrive/Documents/Document/Memoire_M2/Projet_memoire_m2/images/"; // Chemin absolu du répertoire

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Associe l'URL "/uploads/**" au répertoire "C:/Users/Arona Ndiaye/OneDrive/Documents/Document/Memoire_M2/Projet_memoire_m2/images"
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(UPLOAD_DIR);
    }
}