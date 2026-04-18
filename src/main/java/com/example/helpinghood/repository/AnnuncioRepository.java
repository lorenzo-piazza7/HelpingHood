package com.example.helpinghood.repository;

import com.example.helpinghood.model.Annuncio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnnuncioRepository extends JpaRepository<Annuncio, Long> {

    List<Annuncio> findByStatoAndVisibileTrueOrderByUrgenteDescDataInizioAscOraInizioAsc(String stato);

    List<Annuncio> findByRichiedenteIdAndVisibileTrueOrderByCreatedAtDesc(Long richiedenteId);

    Optional<Annuncio> findByIdAndRichiedenteId(Long id, Long richiedenteId);
}
