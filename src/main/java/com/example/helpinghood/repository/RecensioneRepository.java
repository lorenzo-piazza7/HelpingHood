package com.example.helpinghood.repository;

import com.example.helpinghood.model.Recensione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

    List<Recensione> findByDestinatarioIdAndVisibileTrueOrderByCreatedAtDesc(Long destinatarioId);

    Optional<Recensione> findByLavoroIdAndAutoreId(Long lavoroId, Long autoreId);
}
