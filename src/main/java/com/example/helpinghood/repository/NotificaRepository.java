package com.example.helpinghood.repository;

import com.example.helpinghood.model.Notifica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificaRepository extends JpaRepository<Notifica, Long> {

    List<Notifica> findByUtenteIdOrderByCreatedAtDesc(Long utenteId);
}
