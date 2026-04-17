package com.example.helpinghood.service.auth;

import com.example.helpinghood.model.Utente;
import com.example.helpinghood.repository.UtenteRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.auth.enabled", havingValue = "true", matchIfMissing = true)
public class HelpingHoodUserDetailsService implements UserDetailsService {

    private final UtenteRepository utenteRepository;

    public HelpingHoodUserDetailsService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utente utente = utenteRepository.findByEmailIgnoreCaseAndAttivoTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));

        String authority = "ROLE_" + utente.getRuolo().toUpperCase();
        return User.withUsername(utente.getEmail())
                .password(utente.getPasswordHash())
                .authorities(authority)
                .disabled(!utente.isAttivo())
                .build();
    }
}
