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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "annunci")
public class Annuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "richiedente_id", nullable = false)
    private Long richiedenteId;

    @Column(name = "categoria_id")
    private Long categoriaId;

    @Column(nullable = false, length = 150)
    private String titolo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descrizione;

    @Column(length = 100)
    private String citta = "Lecco";

    @Column(length = 5)
    private String cap;

    @Column(length = 200)
    private String indirizzo;

    private BigDecimal latitudine;

    private BigDecimal longitudine;

    @Column(name = "luogo_note", length = 200)
    private String luogoNote;

    @Column(name = "compenso_tipo", length = 20)
    private String compensoTipo = "orario";

    @Column(name = "compenso_importo")
    private BigDecimal compensoImporto;

    @Column(length = 3)
    private String valuta = "EUR";

    @Column(name = "data_inizio")
    private LocalDate dataInizio;

    @Column(name = "ora_inizio")
    private LocalTime oraInizio;

    @Column(name = "durata_ore")
    private BigDecimal durataOre;

    @Column(length = 20)
    private String ricorrenza = "una_tantum";

    @Column(name = "n_giovani_cercati")
    private Integer nGiovaniCercati;

    @Column(length = 20)
    private String stato = "attivo";

    private boolean urgente;

    private boolean visibile = true;

    @Column(name = "scade_il")
    private LocalDateTime scadeIl;

    @Column(name = "n_candidature")
    private Integer nCandidature;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
