package com.locationvoitures.modele;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientTest {

    @Test
    void champsSontNulsParDefaut() {
        Client client = new Client();
        assertNull(client.getId());
        assertNull(client.getNomComplet());
        assertNull(client.getEmail());
        assertNull(client.getTelephone());
        assertNull(client.getNumeroPermis());
    }

    @Test
    void setterEtGetterFonctionnent() {
        Client client = new Client();
        client.setNomComplet("Samir Benali");
        client.setEmail("samir@example.com");
        client.setTelephone("0550123456");

        assertEquals("Samir Benali", client.getNomComplet());
        assertEquals("samir@example.com", client.getEmail());
        assertEquals("0550123456", client.getTelephone());
    }
}