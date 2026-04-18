package com.example.helpinghood.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "disponibilita")
public class Disponibilita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "giovane_id", nullable = false)
    private Long giovaneId;

    @Column(nullable = false, length = 20)
    private String giorno;

    @Column(name = "ora_inizio", nullable = false)
    private LocalTime oraInizio;

    @Column(name = "ora_fine", nullable = false)
    private LocalTime oraFine;
}
