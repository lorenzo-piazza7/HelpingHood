package com.example.helpinghood.repository;

import com.example.helpinghood.model.Messaggio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessaggioRepository extends JpaRepository<Messaggio, Long> {

    List<Messaggio> findByConversazioneIdOrderByInviatoAtAsc(Long conversazioneId);
}
