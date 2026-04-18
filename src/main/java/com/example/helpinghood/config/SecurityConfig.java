package com.example.helpinghood.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Rotte pubbliche: landing, autenticazione e asset statici.
                        .requestMatchers("/", "/auth", "/register", "/css/**", "/js/**", "/favicon.ico", "/error").permitAll()
                        // Area privata del giovane.
                        .requestMatchers("/dashboard/giovane", "/profilo/giovane", "/profilo/giovane/**")
                        .hasRole("GIOVANE")
                        // Area privata del richiedente + gestione annunci.
                        .requestMatchers(
                                "/dashboard/richiedente",
                                "/dashboard/richiedente/lavori",
                                "/dashboard/richiedente/lavori/**",
                                "/dashboard/richiedente/giovani/**",
                                "/profilo/richiedente",
                                "/profilo/richiedente/**",
                                "/annunci",
                                "/annunci/nuovo",
                                "/annunci/nuovo/**",
                                "/annunci/*/modifica",
                                "/giovani"
                        )
                        .hasRole("RICHIEDENTE")
                        // Sezioni condivise ai ruoli applicativi autenticati.
                        .requestMatchers("/lavori", "/lavori/**", "/chat", "/chat/lavori", "/chat/lavori/**")
                        .hasAnyRole("GIOVANE", "RICHIEDENTE", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth")
                        .loginProcessingUrl("/auth")
                        .successHandler((request, response, authentication) -> {
                            // Dopo login si atterra sulla dashboard coerente con il ruolo.
                            String targetUrl = "/home";
                            if (authentication.getAuthorities().stream()
                                    .anyMatch(authority -> "ROLE_GIOVANE".equals(authority.getAuthority()))) {
                                targetUrl = "/dashboard/giovane";
                            } else if (authentication.getAuthorities().stream()
                                    .anyMatch(authority -> "ROLE_RICHIEDENTE".equals(authority.getAuthority()))) {
                                targetUrl = "/dashboard/richiedente";
                            }
                            response.sendRedirect(request.getContextPath() + targetUrl);
                        })
                        .failureUrl("/auth?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
