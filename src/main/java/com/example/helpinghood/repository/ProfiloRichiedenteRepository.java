package com.example.helpinghood.repository;

import com.example.helpinghood.model.ProfiloRichiedente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfiloRichiedenteRepository extends JpaRepository<ProfiloRichiedente, Long> {

    boolean existsByUtenteId(Long utenteId);
}
