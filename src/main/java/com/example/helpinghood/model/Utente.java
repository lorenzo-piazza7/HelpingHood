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

@Getter
@Setter
@Entity
@Table(name = "utenti")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false, length = 80)
    private String cognome;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String ruolo = "giovane";

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(length = 20)
    private String telefono;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "data_nascita")
    private LocalDate dataNascita;

    @Column(length = 100)
    private String citta = "Lecco";

    @Column(length = 5)
    private String cap;

    @Column(length = 200)
    private String indirizzo;

    private BigDecimal latitudine;

    private BigDecimal longitudine;

    @Column(nullable = false)
    private boolean verificato;

    @Column(nullable = false)
    private boolean attivo = true;

    @Column(name = "ultimo_accesso")
    private LocalDateTime ultimoAccesso;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
