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
@Table(name = "categorie")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nome;

    @Column(nullable = false, length = 60)
    private String icona;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @Column(name = "colore_hex", length = 7)
    private String coloreHex;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
