package com.locationvoitures.modele;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LocationTest {

    @Test
    void champsSontNulsParDefaut() {
        Location location = new Location();
        assertNull(location.getId());
        assertNull(location.getVoitureId());
        assertNull(location.getClientId());
    }

    @Test
    void setterEtGetterFonctionnent() {
        LocalDate debut = LocalDate.of(2026, 9, 20);
        LocalDate fin = LocalDate.of(2026, 9, 25);

        Location location = new Location();
        location.setVoitureId(3);
        location.setClientId(7);
        location.setDateDebut(debut);
        location.setDateFin(fin);
        location.setNombreJours(5);
        location.setPrixTotal(new BigDecimal("600.00"));

        assertEquals(3, location.getVoitureId());
        assertEquals(7, location.getClientId());
        assertEquals(debut, location.getDateDebut());
        assertEquals(fin, location.getDateFin());
        assertEquals(5, location.getNombreJours());
        assertEquals(new BigDecimal("600.00"), location.getPrixTotal());
    }

    @Test
    void totalRevenueSoustraitLaRemise() {
        Location location = new Location();
        location.setPrixTotal(new BigDecimal("600.00"));
        location.setRemise(new BigDecimal("50.00"));

        assertEquals(new BigDecimal("550.00"), location.getTotalRevenue());
    }

    @Test
    void totalRevenueSansRemise() {
        Location location = new Location();
        location.setPrixTotal(new BigDecimal("600.00"));

        assertEquals(new BigDecimal("600.00"), location.getTotalRevenue());
    }
}