package com.example.helpinghood.service;

import com.example.helpinghood.model.ProfiloRichiedente;
import com.example.helpinghood.repository.ProfiloRichiedenteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfiloRichiedenteService {

    private final ProfiloRichiedenteRepository profiloRichiedenteRepository;

    public ProfiloRichiedenteService(ProfiloRichiedenteRepository profiloRichiedenteRepository) {
        this.profiloRichiedenteRepository = profiloRichiedenteRepository;
    }

    public Optional<ProfiloRichiedente> findByUtenteId(Long utenteId) {
        return profiloRichiedenteRepository.findByUtenteId(utenteId);
    }

    public ProfiloRichiedente save(ProfiloRichiedente profiloRichiedente) {
        return profiloRichiedenteRepository.save(profiloRichiedente);
    }
}
