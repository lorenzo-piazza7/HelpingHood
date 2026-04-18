package com.example.helpinghood.service;

import com.example.helpinghood.model.Utente;
import com.example.helpinghood.repository.UtenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtenteService {

    private final UtenteRepository utenteRepository;

    public UtenteService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    public Optional<Utente> findById(Long id) {
        return utenteRepository.findById(id);
    }

    public Optional<Utente> findActiveByEmail(String email) {
        // Utilizzato per risolvere l'utente corrente ignorando account disattivati.
        return utenteRepository.findByEmailIgnoreCaseAndAttivoTrue(email);
    }

    public List<Utente> findByRuolo(String ruolo) {
        // Lista utenti attivi ordinata per UI e directory.
        return utenteRepository.findByRuoloAndAttivoTrueOrderByCognomeAscNomeAsc(ruolo);
    }

    public boolean existsByEmail(String email) {
        // Controllo unicita email nei form di registrazione e aggiornamento profilo.
        return utenteRepository.existsByEmailIgnoreCase(email);
    }

    public Utente save(Utente utente) {
        // Persistenza centralizzata dell'entita utente.
        return utenteRepository.save(utente);
    }
}
