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
@Table(name = "recensioni")
public class Recensione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lavoro_id", nullable = false)
    private Long lavoroId;

    @Column(name = "autore_id", nullable = false)
    private Long autoreId;

    @Column(name = "destinatario_id", nullable = false)
    private Long destinatarioId;

    @Column(nullable = false)
    private Integer voto;

    @Column(columnDefinition = "TEXT")
    private String testo;

    private Integer puntualita;

    private Integer qualita;

    private Integer comunicazione;

    private Integer rispetto;

    private boolean visibile = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
