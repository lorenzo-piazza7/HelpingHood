package com.example.helpinghood.repository;

import com.example.helpinghood.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {

    Optional<Utente> findByEmailIgnoreCaseAndAttivoTrue(String email);

    boolean existsByEmailIgnoreCase(String email);
}
