package com.example.helpinghood.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class AnnuncioTagId implements Serializable {

    @Column(name = "annuncio_id")
    private Long annuncioId;

    @Column(name = "tag_id")
    private Long tagId;
}
