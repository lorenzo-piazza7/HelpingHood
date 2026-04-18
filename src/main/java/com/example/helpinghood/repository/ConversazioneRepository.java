package com.example.helpinghood.repository;

import com.example.helpinghood.model.Conversazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversazioneRepository extends JpaRepository<Conversazione, Long> {

    @Query("""
            SELECT c
            FROM Conversazione c
            WHERE c.partecipante1 = :utenteId OR c.partecipante2 = :utenteId
            ORDER BY c.ultimoMessaggioAt DESC, c.createdAt DESC
            """)
    List<Conversazione> findByPartecipanteOrderByUltimoMessaggioAtDescCreatedAtDesc(Long utenteId);

    @Query("""
            SELECT c
            FROM Conversazione c
            WHERE c.annuncioId = :annuncioId
              AND (c.partecipante1 = :utenteId OR c.partecipante2 = :utenteId)
            ORDER BY c.ultimoMessaggioAt DESC, c.createdAt DESC
            """)
    List<Conversazione> findByAnnuncioIdAndPartecipanteOrderByUltimoMessaggioAtDescCreatedAtDesc(Long annuncioId, Long utenteId);

    @Query("""
            SELECT c
            FROM Conversazione c
            WHERE c.annuncioId = :annuncioId
              AND ((c.partecipante1 = :utenteA AND c.partecipante2 = :utenteB)
                OR (c.partecipante1 = :utenteB AND c.partecipante2 = :utenteA))
            """)
    Optional<Conversazione> findByAnnuncioIdAndParticipants(Long annuncioId, Long utenteA, Long utenteB);
}
