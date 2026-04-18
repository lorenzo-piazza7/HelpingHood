package com.example.helpinghood.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "candidature")
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annuncio_id", nullable = false)
    private Long annuncioId;

    @Column(name = "giovane_utente_id", nullable = false)
    private Long giovaneUtenteId;

    @Column(columnDefinition = "TEXT")
    private String messaggio;

    @Column(length = 20)
    private String stato = "inviata";

    @Column(name = "data_candidatura", insertable = false, updatable = false)
    private LocalDateTime dataCandidatura;

    @Column(name = "data_risposta")
    private LocalDateTime dataRisposta;
}
