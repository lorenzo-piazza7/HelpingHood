package com.example.helpinghood.repository;

import com.example.helpinghood.model.Conversazione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversazioneRepository extends JpaRepository<Conversazione, Long> {

    List<Conversazione> findByPartecipante1OrPartecipante2OrderByUltimoMessaggioAtDescCreatedAtDesc(Long partecipante1,
                                                                                                     Long partecipante2);
}
