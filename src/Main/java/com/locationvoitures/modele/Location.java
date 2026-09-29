/**
 * Model representing a rental operation.
 * Links a car to a client for a specific period
 * with automatic calculation of days and total price.
 */
package com.locationvoitures.modele;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Location {

    /** Rental's unique identifier */
    private Integer id;

    /** Rented car ID (foreign key to voitures) */
    private Integer voitureId;

    /** Tenant client ID (foreign key to clients) */
    private Integer clientId;

    /** Rental start date */
    private LocalDate dateDebut;

    /** Rental end date */
    private LocalDate dateFin;

    /** Vehicle handover location at rental start */
    private String pointDepart;

    /** Vehicle return location at rental end */
    private String pointRetour;

    /** Total number of rental days */
    private Integer nombreJours;

    /** Calculated total price (daily_price x days) in DZD */
    private BigDecimal prixTotal;

    /** Discount applied to the total price in DZD */
    private BigDecimal remise;

    /** Car display label (brand model) - not persisted in DB */
    private String voitureLabel;

    /** Client full name for display - not persisted in DB */
    private String clientLabel;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVoitureId() {
        return voitureId;
    }

    public void setVoitureId(Integer voitureId) {
        this.voitureId = voitureId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getPointDepart() {
        return pointDepart;
    }

    public void setPointDepart(String pointDepart) {
        this.pointDepart = pointDepart;
    }

    public String getPointRetour() {
        return pointRetour;
    }

    public void setPointRetour(String pointRetour) {
        this.pointRetour = pointRetour;
    }

    public Integer getNombreJours() {
        return nombreJours;
    }

    public void setNombreJours(Integer nombreJours) {
        this.nombreJours = nombreJours;
    }

    public BigDecimal getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }

    public BigDecimal getRemise() {
        return remise;
    }

    public void setRemise(BigDecimal remise) {
        this.remise = remise;
    }

    public BigDecimal getTotalRevenue() {
        BigDecimal base = prixTotal != null ? prixTotal : BigDecimal.ZERO;
        BigDecimal remiseValue = remise != null ? remise : BigDecimal.ZERO;
        return base.subtract(remiseValue);
    }

    public String getVoitureLabel() {
        return voitureLabel;
    }

    public void setVoitureLabel(String voitureLabel) {
        this.voitureLabel = voitureLabel;
    }

    public String getClientLabel() {
        return clientLabel;
    }

    public void setClientLabel(String clientLabel) {
        this.clientLabel = clientLabel;
    }
}