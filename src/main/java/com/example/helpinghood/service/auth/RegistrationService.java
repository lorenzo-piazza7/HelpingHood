package com.example.helpinghood.service.auth;

import com.example.helpinghood.model.ProfiloGiovane;
import com.example.helpinghood.model.ProfiloRichiedente;
import com.example.helpinghood.model.Utente;
import com.example.helpinghood.repository.ProfiloGiovaneRepository;
import com.example.helpinghood.repository.ProfiloRichiedenteRepository;
import com.example.helpinghood.repository.UtenteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Service
@ConditionalOnProperty(name = "app.auth.enabled", havingValue = "true", matchIfMissing = true)
public class RegistrationService {

    private final UtenteRepository utenteRepository;
    private final ProfiloGiovaneRepository profiloGiovaneRepository;
    private final ProfiloRichiedenteRepository profiloRichiedenteRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UtenteRepository utenteRepository,
                               ProfiloGiovaneRepository profiloGiovaneRepository,
                               ProfiloRichiedenteRepository profiloRichiedenteRepository,
                               PasswordEncoder passwordEncoder) {
        this.utenteRepository = utenteRepository;
        this.profiloGiovaneRepository = profiloGiovaneRepository;
        this.profiloRichiedenteRepository = profiloRichiedenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegistrationResult register(RegistrationRequest request) {
        if (!StringUtils.hasText(request.nome()) || !StringUtils.hasText(request.cognome())
                || !StringUtils.hasText(request.email()) || !StringUtils.hasText(request.password())) {
            return new RegistrationResult(false, "Compila tutti i campi obbligatori.");
        }

        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        if (utenteRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            return new RegistrationResult(false, "Email già registrata.");
        }

        String role = normalizeRole(request.ruolo());

        Utente utente = new Utente();
        utente.setNome(request.nome().trim());
        utente.setCognome(request.cognome().trim());
        utente.setEmail(normalizedEmail);
        utente.setPasswordHash(passwordEncoder.encode(request.password()));
        utente.setRuolo(role);
        utente.setAttivo(true);
        utente.setVerificato(false);

        Utente saved = utenteRepository.save(utente);

        if ("giovane".equals(role)) {
            ProfiloGiovane profiloGiovane = new ProfiloGiovane();
            profiloGiovane.setUtenteId(saved.getId());
            profiloGiovaneRepository.save(profiloGiovane);
        } else {
            ProfiloRichiedente profiloRichiedente = new ProfiloRichiedente();
            profiloRichiedente.setUtenteId(saved.getId());
            profiloRichiedente.setTipo("privato");
            profiloRichiedenteRepository.save(profiloRichiedente);
        }

        return new RegistrationResult(true, "Registrazione completata. Ora puoi accedere.");
    }

    private String normalizeRole(String rawRole) {
        if (rawRole == null) {
            return "giovane";
        }
        String role = rawRole.trim().toLowerCase(Locale.ROOT);
        if ("richiedente".equals(role)) {
            return "richiedente";
        }
        return "giovane";
    }

    public record RegistrationRequest(String nome, String cognome, String email, String password, String ruolo) {
    }

    public record RegistrationResult(boolean success, String message) {
    }
}
