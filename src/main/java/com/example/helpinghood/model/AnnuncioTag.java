package com.example.helpinghood.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "annuncio_tags")
public class AnnuncioTag {

    @EmbeddedId
    private AnnuncioTagId id;
}
