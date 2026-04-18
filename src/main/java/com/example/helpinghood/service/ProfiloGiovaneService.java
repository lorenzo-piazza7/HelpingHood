package com.example.helpinghood.service;

import com.example.helpinghood.model.ProfiloGiovane;
import com.example.helpinghood.repository.ProfiloGiovaneRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfiloGiovaneService {

    private final ProfiloGiovaneRepository profiloGiovaneRepository;

    public ProfiloGiovaneService(ProfiloGiovaneRepository profiloGiovaneRepository) {
        this.profiloGiovaneRepository = profiloGiovaneRepository;
    }

    public Optional<ProfiloGiovane> findByUtenteId(Long utenteId) {
        return profiloGiovaneRepository.findByUtenteId(utenteId);
    }

    public ProfiloGiovane save(ProfiloGiovane profiloGiovane) {
        return profiloGiovaneRepository.save(profiloGiovane);
    }
}
