package com.example.helpinghood.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "profili_giovani")
public class ProfiloGiovane {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "utente_id", nullable = false, unique = true)
    private Long utenteId;

    @Column(name = "raggio_km")
    private Integer raggioKm = 5;

    @Column(nullable = false)
    private boolean disponibile = true;

    @Column(name = "ore_settimana_max")
    private Integer oreSettimanaMax = 10;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUtenteId() {
        return utenteId;
    }

    public void setUtenteId(Long utenteId) {
        this.utenteId = utenteId;
    }

    public Integer getRaggioKm() {
        return raggioKm;
    }

    public void setRaggioKm(Integer raggioKm) {
        this.raggioKm = raggioKm;
    }

    public boolean isDisponibile() {
        return disponibile;
    }

    public void setDisponibile(boolean disponibile) {
        this.disponibile = disponibile;
    }

    public Integer getOreSettimanaMax() {
        return oreSettimanaMax;
    }

    public void setOreSettimanaMax(Integer oreSettimanaMax) {
        this.oreSettimanaMax = oreSettimanaMax;
    }
}
