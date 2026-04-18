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
public class GiovaneCompetenzaId implements Serializable {

    @Column(name = "giovane_id")
    private Long giovaneId;

    @Column(name = "competenza_id")
    private Long competenzaId;
}
