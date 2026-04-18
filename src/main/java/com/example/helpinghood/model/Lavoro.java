package com.example.helpinghood.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "lavori")
public class Lavoro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidatura_id", nullable = false, unique = true)
    private Long candidaturaId;

    @Column(name = "annuncio_id", nullable = false)
    private Long annuncioId;

    @Column(name = "giovane_id", nullable = false)
    private Long giovaneId;

    @Column(name = "richiedente_id", nullable = false)
    private Long richiedenteId;

    @Column(name = "data_inizio_eff")
    private LocalDateTime dataInizioEff;

    @Column(name = "data_fine_eff")
    private LocalDateTime dataFineEff;

    @Column(name = "ore_effettive")
    private BigDecimal oreEffettive;

    @Column(length = 20)
    private String stato = "confermato";

    @Column(name = "note_chiusura", columnDefinition = "TEXT")
    private String noteChiusura;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
