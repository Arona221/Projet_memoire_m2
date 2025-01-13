package connect.event.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and() // Activer le CORS
                .csrf().disable() // Désactiver CSRF (puisque vous utilisez des tokens JWT)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Politique de gestion de session stateless
                .and()
                .authorizeRequests() // Configuration des autorisations
                .requestMatchers("/auth/**").permitAll() // Autorise certaines URL sans authentification
                .anyRequest().authenticated() // Toutes les autres requêtes nécessitent une authentification
                .and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // Ajoute un filtre JWT

        return http.build(); // Retourne la configuration construite
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(); // Filtre d'authentification JWT
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Utilisation de BCrypt pour l'encodage des mots de passe
    }
}
