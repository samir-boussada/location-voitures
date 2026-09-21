package com.locationvoitures.modele;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class VoitureTest {

    @Test
    void getLibelleCombineMarqueEtModele() {
        Voiture voiture = new Voiture();
        voiture.setMarque("BMW");
        voiture.setModele("Serie 3");

        assertEquals("BMW Serie 3", voiture.getLibelle());
    }

    @Test
    void nouveauxChampsSontNulsParDefaut() {
        Voiture voiture = new Voiture();
        assertNull(voiture.getId());
        assertNull(voiture.getPrixJour());
        assertNull(voiture.getStatut());
    }

    @Test
    void setterEtGetterFonctionnent() {
        Voiture voiture = new Voiture();
        voiture.setId(5);
        voiture.setPrixJour(new BigDecimal("120.00"));
        voiture.setStatut("DISPONIBLE");

        assertEquals(5, voiture.getId());
        assertEquals(new BigDecimal("120.00"), voiture.getPrixJour());
        assertEquals("DISPONIBLE", voiture.getStatut());
    }
}