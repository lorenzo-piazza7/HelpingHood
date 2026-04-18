package com.example.helpinghood.service;

import com.example.helpinghood.model.Recensione;
import com.example.helpinghood.repository.RecensioneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecensioneService {

    private final RecensioneRepository recensioneRepository;

    public RecensioneService(RecensioneRepository recensioneRepository) {
        this.recensioneRepository = recensioneRepository;
    }

    public List<Recensione> findVisibleByDestinatario(Long destinatarioId) {
        // Le pagine pubbliche usano solo feedback marcati come visibili.
        return recensioneRepository.findByDestinatarioIdAndVisibileTrueOrderByCreatedAtDesc(destinatarioId);
    }

    public Optional<Recensione> findByLavoroAndAutore(Long lavoroId, Long autoreId) {
        // Permette un solo feedback per coppia (lavoro, autore) nel flusso di aggiornamento recensione.
        return recensioneRepository.findByLavoroIdAndAutoreId(lavoroId, autoreId);
    }

    public Recensione save(Recensione recensione) {
        // Salvataggio usato sia in creazione sia in modifica.
        return recensioneRepository.save(recensione);
    }
}
