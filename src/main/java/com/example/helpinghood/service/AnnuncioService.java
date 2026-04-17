package com.example.helpinghood.service;

import com.example.helpinghood.model.Annuncio;
import com.example.helpinghood.repository.AnnuncioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnnuncioService {

    private final AnnuncioRepository annuncioRepository;

    public AnnuncioService(AnnuncioRepository annuncioRepository) {
        this.annuncioRepository = annuncioRepository;
    }

    public List<Annuncio> findAttiviVisibili() {
        return annuncioRepository.findByStatoAndVisibileTrueOrderByUrgenteDescDataInizioAscOraInizioAsc("attivo");
    }

    public List<Annuncio> findByRichiedente(Long richiedenteId) {
        return annuncioRepository.findByRichiedenteIdOrderByCreatedAtDesc(richiedenteId);
    }

    public Optional<Annuncio> findById(Long id) {
        return annuncioRepository.findById(id);
    }

    public Annuncio save(Annuncio annuncio) {
        return annuncioRepository.save(annuncio);
    }
}
