package com.example.helpinghood.repository;

import com.example.helpinghood.model.ProfiloGiovane;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfiloGiovaneRepository extends JpaRepository<ProfiloGiovane, Long> {

    boolean existsByUtenteId(Long utenteId);

    Optional<ProfiloGiovane> findByUtenteId(Long utenteId);
}
