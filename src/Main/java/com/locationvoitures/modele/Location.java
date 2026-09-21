/*
 * Decompiled with CFR 0.152.
 */
package com.locationvoitures.modele;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Location {
    private Integer id;
    private Integer voitureId;
    private Integer clientId;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String pointDepart;
    private String pointRetour;
    private BigDecimal remise;
    private Integer nombreJours;
    private BigDecimal prixTotal;
    private String voitureLabel;
    private String clientLabel;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVoitureId() {
        return this.voitureId;
    }

    public void setVoitureId(Integer voitureId) {
        this.voitureId = voitureId;
    }

    public Integer getClientId() {
        return this.clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public LocalDate getDateDebut() {
        return this.dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return this.dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getPointDepart() {
        return this.pointDepart;
    }

    public void setPointDepart(String pointDepart) {
        this.pointDepart = pointDepart;
    }

    public String getPointRetour() {
        return this.pointRetour;
    }

    public void setPointRetour(String pointRetour) {
        this.pointRetour = pointRetour;
    }

    public BigDecimal getRemise() {
        return this.remise;
    }

    public void setRemise(BigDecimal remise) {
        this.remise = remise;
    }

    public BigDecimal getTotalRevenue() {
        BigDecimal base = this.prixTotal != null ? this.prixTotal : BigDecimal.ZERO;
        BigDecimal remiseValue = this.remise != null ? this.remise : BigDecimal.ZERO;
        return base.subtract(remiseValue);
    }

    public Integer getNombreJours() {
        return this.nombreJours;
    }

    public void setNombreJours(Integer nombreJours) {
        this.nombreJours = nombreJours;
    }

    public BigDecimal getPrixTotal() {
        return this.prixTotal;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }

    public String getVoitureLabel() {
        return this.voitureLabel;
    }

    public void setVoitureLabel(String voitureLabel) {
        this.voitureLabel = voitureLabel;
    }

    public String getClientLabel() {
        return this.clientLabel;
    }

    public void setClientLabel(String clientLabel) {
        this.clientLabel = clientLabel;
    }
}
