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
@Table(name = "profili_richiedenti")
public class ProfiloRichiedente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "utente_id", nullable = false, unique = true)
    private Long utenteId;

    @Column(length = 40)
    private String tipo = "privato";

    @Column(name = "nome_org", length = 150)
    private String nomeOrg;

    @Column(name = "sito_web", length = 300)
    private String sitoWeb;

    @Column(name = "punteggio_medio")
    private BigDecimal punteggioMedio;

    @Column(name = "n_annunci_totali")
    private Integer nAnnunciTotali;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
