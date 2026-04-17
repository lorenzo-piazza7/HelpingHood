package com.example.helpinghood.repository;

import com.example.helpinghood.model.Conversazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConversazioneRepository extends JpaRepository<Conversazione, Long> {

    @Query("""
            SELECT c
            FROM Conversazione c
            WHERE c.partecipante1 = :utenteId OR c.partecipante2 = :utenteId
            ORDER BY c.ultimoMessaggioAt DESC, c.createdAt DESC
            """)
    List<Conversazione> findByPartecipanteOrderByUltimoMessaggioAtDescCreatedAtDesc(Long utenteId);
}
