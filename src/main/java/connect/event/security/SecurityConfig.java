package connect.event.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    @Qualifier("customCorsFilter")
    private CorsFilter corsFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        // Permettre l'accès aux images sans authentification
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/ConnectEvent/ws/**").permitAll() // Autoriser le WebSocket
                        .requestMatchers("/api/ConnectEvent/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/ws/info/**").permitAll()
                        .requestMatchers("/ws/*/websocket/**").permitAll()
                        .requestMatchers("/ws/*/*/**").permitAll()
                        .requestMatchers("/ws/info/**").permitAll()
                        .requestMatchers("/ws/info").permitAll()
                        .requestMatchers("/api/ConnectEvent/**").permitAll()
                        .requestMatchers("/api/ConnectEvent/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ConnectEvent/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Autres configurations
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ConnectEvent/evenements/approved").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/ConnectEvent/evenements").hasAnyRole("ORGANISATEUR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/ConnectEvent/evenements/**").hasAnyRole("ORGANISATEUR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/ConnectEvent/evenements/**").hasAnyRole("ORGANISATEUR", "ADMIN")
                        // Routes organisateur
                        .requestMatchers("/api/ConnectEvent/billets/organizer/*/events").hasRole("ORGANISATEUR")
                        .requestMatchers("/api/ConnectEvent/billets/event/*/participants").hasRole("ORGANISATEUR")
                        .requestMatchers("/api/ConnectEvent/notification/**").authenticated()
                        // Par défaut, exiger l'authentification pour les autres requêtes
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}