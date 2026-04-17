package com.example.helpinghood.repository;

import com.example.helpinghood.model.Recensione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

    List<Recensione> findByDestinatarioIdAndVisibileTrueOrderByCreatedAtDesc(Long destinatarioId);
}
