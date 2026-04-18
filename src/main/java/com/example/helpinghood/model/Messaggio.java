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
@Table(name = "messaggi")
public class Messaggio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversazione_id", nullable = false)
    private Long conversazioneId;

    @Column(name = "mittente_id", nullable = false)
    private Long mittenteId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String testo;

    @Column(name = "allegato_url", length = 500)
    private String allegatoUrl;

    private boolean letto;

    @Column(name = "inviato_at", insertable = false, updatable = false)
    private LocalDateTime inviatoAt;
}
