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
        return utenteRepository.findByEmailIgnoreCaseAndAttivoTrue(email);
    }

    public List<Utente> findByRuolo(String ruolo) {
        return utenteRepository.findByRuoloAndAttivoTrueOrderByCognomeAscNomeAsc(ruolo);
    }

    public Utente save(Utente utente) {
        return utenteRepository.save(utente);
    }
}
