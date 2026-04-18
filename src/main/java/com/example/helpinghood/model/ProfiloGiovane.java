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
@Table(name = "profili_giovani")
public class ProfiloGiovane {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "utente_id", nullable = false, unique = true)
    private Long utenteId;

    private Integer anni;

    @Column(name = "scuola_o_universita", length = 150)
    private String scuolaOUniversita;

    @Column(name = "anno_studio", length = 60)
    private String annoStudio;

    @Column(name = "raggio_km")
    private Integer raggioKm = 5;

    @Column(name = "mezzo_spostamento")
    private String mezzoSpostamento;

    @Column(nullable = false)
    private boolean disponibile = true;

    @Column(name = "ore_settimana_max")
    private Integer oreSettimanaMax = 10;

    @Column(name = "compenso_min_ora")
    private BigDecimal compensoMinOra;

    @Column(name = "punteggio_medio")
    private BigDecimal punteggioMedio;

    @Column(name = "n_lavori_completati")
    private Integer nLavoriCompletati;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
