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
@Table(name = "notifiche")
public class Notifica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "utente_id", nullable = false)
    private Long utenteId;

    @Column(nullable = false, length = 40)
    private String tipo;

    @Column(length = 150)
    private String titolo;

    @Column(columnDefinition = "TEXT")
    private String testo;

    @Column(name = "link_interno", length = 300)
    private String linkInterno;

    private boolean letto;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
