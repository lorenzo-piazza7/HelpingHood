package com.example.helpinghood.repository;

import com.example.helpinghood.model.Competenza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetenzaRepository extends JpaRepository<Competenza, Long> {

    List<Competenza> findByCategoriaId(Long categoriaId);
}
