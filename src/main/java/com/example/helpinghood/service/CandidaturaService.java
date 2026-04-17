package com.example.helpinghood.service;

import com.example.helpinghood.model.Candidatura;
import com.example.helpinghood.repository.CandidaturaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidaturaService {

    private final CandidaturaRepository candidaturaRepository;

    public CandidaturaService(CandidaturaRepository candidaturaRepository) {
        this.candidaturaRepository = candidaturaRepository;
    }

    public List<Candidatura> findByGiovane(Long giovaneUtenteId) {
        return candidaturaRepository.findByGiovaneUtenteIdOrderByDataCandidaturaDesc(giovaneUtenteId);
    }

    public List<Candidatura> findByAnnuncio(Long annuncioId) {
        return candidaturaRepository.findByAnnuncioIdOrderByDataCandidaturaDesc(annuncioId);
    }

    public Optional<Candidatura> findByAnnuncioAndGiovane(Long annuncioId, Long giovaneUtenteId) {
        return candidaturaRepository.findByAnnuncioIdAndGiovaneUtenteId(annuncioId, giovaneUtenteId);
    }

    public Candidatura save(Candidatura candidatura) {
        return candidaturaRepository.save(candidatura);
    }
}
