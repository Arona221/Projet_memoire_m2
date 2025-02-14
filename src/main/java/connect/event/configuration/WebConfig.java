package connect.event.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String UPLOAD_DIR = "file:///C:/Users/Arona%20Ndiaye/OneDrive/Documents/Document/Memoire_M2/Projet_memoire_m2/images/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/uploads/**", "/api/ConnectEvent/uploads/**")
                .addResourceLocations(UPLOAD_DIR)
                .setCachePeriod(3600)
                .resourceChain(true);
    }
}