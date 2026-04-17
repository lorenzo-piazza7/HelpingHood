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
@Table(name = "conversazioni")
public class Conversazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annuncio_id")
    private Long annuncioId;

    @Column(name = "partecipante_1", nullable = false)
    private Long partecipante1;

    @Column(name = "partecipante_2", nullable = false)
    private Long partecipante2;

    @Column(name = "ultimo_messaggio_at")
    private LocalDateTime ultimoMessaggioAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
