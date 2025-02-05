package uasz.sn.GestionEnseignement.authentification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/resources/**", "/css/**", "/js/**").permitAll()  // Autoriser toutes les ressources statiques
                        .requestMatchers("/Vacataire/**").hasAuthority("VACATAIRE")
                        .requestMatchers("/Permanent/**").hasAuthority("PERMANENT")
                        .requestMatchers("/Etudiant/**").hasAuthority("ETUDIANT")
                        .requestMatchers("/ResponsableClasse/**").hasAuthority("RESPONSABLECLASSE")
                        .requestMatchers("/ChefDepartement/**").hasAuthority("CHEFDEPARTEMENT")
                        .requestMatchers("/Batiment/**").hasAuthority("CHEFDEPARTEMENT")
                        .requestMatchers("/UE/**").hasAuthority("CHEFDEPARTEMENT")
                        .requestMatchers("/Salle/**").hasAuthority("CHEFDEPARTEMENT")
                        .requestMatchers("/Choix/**").hasAuthority("CHEFDEPARTEMENT")
                        .requestMatchers("/EC/**").hasAuthority("CHEFDEPARTEMENT")
                        .requestMatchers("/Maquette/**").hasAuthority("CHEFDEPARTEMENT")
                        .requestMatchers("/Classe/**").hasAuthority("CHEFDEPARTEMENT")
                        .requestMatchers("/Formation/**").hasAuthority("CHEFDEPARTEMENT")
                        .requestMatchers("/login", "/logout").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );

        return http.build();
    }
}
