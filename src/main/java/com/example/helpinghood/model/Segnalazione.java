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
@Table(name = "segnalazioni")
public class Segnalazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "segnalante_id", nullable = false)
    private Long segnalanteId;

    @Column(name = "tipo_target", nullable = false, length = 20)
    private String tipoTarget;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(nullable = false, length = 40)
    private String motivo;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @Column(length = 20)
    private String stato = "in_attesa";

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
