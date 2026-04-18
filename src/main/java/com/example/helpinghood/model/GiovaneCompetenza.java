package com.example.helpinghood.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "giovane_competenze")
public class GiovaneCompetenza {

    @EmbeddedId
    private GiovaneCompetenzaId id;

    @Column(length = 20)
    private String livello = "base";
}
