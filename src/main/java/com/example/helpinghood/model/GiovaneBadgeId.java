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
public class GiovaneBadgeId implements Serializable {

    @Column(name = "giovane_id")
    private Long giovaneId;

    @Column(name = "badge_id")
    private Long badgeId;
}
