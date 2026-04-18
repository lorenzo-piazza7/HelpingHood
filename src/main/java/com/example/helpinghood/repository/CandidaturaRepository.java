package com.example.helpinghood.repository;

import com.example.helpinghood.model.Candidatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {

    List<Candidatura> findByGiovaneUtenteIdOrderByDataCandidaturaDesc(Long giovaneUtenteId);

    List<Candidatura> findByAnnuncioIdOrderByDataCandidaturaDesc(Long annuncioId);

    Optional<Candidatura> findByAnnuncioIdAndGiovaneUtenteId(Long annuncioId, Long giovaneUtenteId);
}
