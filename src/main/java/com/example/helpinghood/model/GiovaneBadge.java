package com.example.helpinghood.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "giovane_badge")
public class GiovaneBadge {

    @EmbeddedId
    private GiovaneBadgeId id;

    @Column(name = "assegnato_at", insertable = false, updatable = false)
    private LocalDateTime assegnatoAt;
}
