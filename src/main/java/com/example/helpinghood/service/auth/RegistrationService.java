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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Service
@ConditionalOnProperty(name = "app.auth.enabled", havingValue = "true", matchIfMissing = true)
public class RegistrationService {

    private static final String ROLE_GIOVANE = "giovane";
    private static final String ROLE_RICHIEDENTE = "richiedente";
    private static final String TIPO_RICHIEDENTE_DEFAULT = "privato";

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
        // Validazione minima richiesta dal form di registrazione.
        if (!StringUtils.hasText(request.nome()) || !StringUtils.hasText(request.cognome())
                || !StringUtils.hasText(request.email()) || !StringUtils.hasText(request.password())
                || !StringUtils.hasText(request.confermaPassword()) || !StringUtils.hasText(request.ruolo())
                || !StringUtils.hasText(request.telefono()) || !StringUtils.hasText(request.dataNascita())
                || !StringUtils.hasText(request.citta()) || !StringUtils.hasText(request.indirizzo())) {
            return new RegistrationResult(false, "Compila tutti i campi obbligatori.");
        }

        if (!request.password().equals(request.confermaPassword())) {
            return new RegistrationResult(false, "La conferma password non coincide.");
        }

        String role = normalizeRole(request.ruolo());
        if (role == null) {
            return new RegistrationResult(false, "Ruolo non valido.");
        }

        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(request.dataNascita().trim());
        } catch (DateTimeParseException ex) {
            return new RegistrationResult(false, "Data di nascita non valida.");
        }

        if (ROLE_GIOVANE.equals(role)) {
            LocalDate today = LocalDate.now();
            LocalDate minBirthDate = today.minusYears(25);
            LocalDate maxBirthDate = today.minusYears(14);
            if (birthDate.isBefore(minBirthDate) || birthDate.isAfter(maxBirthDate)) {
                return new RegistrationResult(false, "La data di nascita del giovane deve corrispondere a un'età tra 14 e 25 anni.");
            }
        }

        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        if (utenteRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            return new RegistrationResult(false, "Email già registrata.");
        }

        Utente utente = new Utente();
        utente.setNome(request.nome().trim());
        utente.setCognome(request.cognome().trim());
        utente.setEmail(normalizedEmail);
        utente.setPasswordHash(passwordEncoder.encode(request.password()));
        utente.setRuolo(role);
        utente.setTelefono(request.telefono().trim());
        utente.setDataNascita(birthDate);
        utente.setCitta(request.citta().trim());
        utente.setIndirizzo(request.indirizzo().trim());
        utente.setAttivo(true);
        utente.setVerificato(false);

        Utente saved = utenteRepository.save(utente);

        // Dopo la creazione dell'utente, inizializziamo il profilo specifico per ruolo.
        if (ROLE_GIOVANE.equals(role)) {
            ProfiloGiovane profiloGiovane = new ProfiloGiovane();
            profiloGiovane.setUtenteId(saved.getId());
            profiloGiovaneRepository.save(profiloGiovane);
        } else {
            ProfiloRichiedente profiloRichiedente = new ProfiloRichiedente();
            profiloRichiedente.setUtenteId(saved.getId());
            profiloRichiedente.setTipo(TIPO_RICHIEDENTE_DEFAULT);
            profiloRichiedenteRepository.save(profiloRichiedente);
        }

        return new RegistrationResult(true, "Registrazione completata. Ora puoi accedere.");
    }

    private String normalizeRole(String rawRole) {
        if (rawRole == null) {
            return null;
        }
        // Manteniamo esplicitamente una whitelist per evitare ruoli arbitrari dal client.
        String role = rawRole.trim().toLowerCase(Locale.ROOT);
        if (ROLE_GIOVANE.equals(role)) {
            return ROLE_GIOVANE;
        }
        if (ROLE_RICHIEDENTE.equals(role)) {
            return ROLE_RICHIEDENTE;
        }
        return null;
    }

    public record RegistrationRequest(String nome,
                                      String cognome,
                                      String email,
                                      String password,
                                      String confermaPassword,
                                      String ruolo,
                                      String telefono,
                                      String dataNascita,
                                      String citta,
                                      String indirizzo) {
    }

    public record RegistrationResult(boolean success, String message) {
    }
}
