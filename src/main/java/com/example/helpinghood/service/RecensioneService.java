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
        return recensioneRepository.findByDestinatarioIdAndVisibileTrueOrderByCreatedAtDesc(destinatarioId);
    }

    public Optional<Recensione> findByLavoroAndAutore(Long lavoroId, Long autoreId) {
        return recensioneRepository.findByLavoroIdAndAutoreId(lavoroId, autoreId);
    }

    public Recensione save(Recensione recensione) {
        return recensioneRepository.save(recensione);
    }
}
