/*
 * Decompiled with CFR 0.152.
 */
package com.locationvoitures.modele;

import java.math.BigDecimal;

public class Voiture {
    private Integer id;
    private String marque;
    private String modele;
    private BigDecimal prixJour;
    private String statut;
    private String photo;
    private String moteur;
    private String transmission;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMarque() {
        return this.marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return this.modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public BigDecimal getPrixJour() {
        return this.prixJour;
    }

    public void setPrixJour(BigDecimal prixJour) {
        this.prixJour = prixJour;
    }

    public String getStatut() {
        return this.statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getMoteur() {
        return this.moteur;
    }

    public void setMoteur(String moteur) {
        this.moteur = moteur;
    }

    public String getTransmission() {
        return this.transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getLibelle() {
        return this.marque + " " + this.modele;
    }
}
