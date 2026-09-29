package com.locationvoitures.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UtilisateurDetailsServiceTest {

    private JdbcTemplate jdbcTemplate;
    private UtilisateurDetailsService service;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        service = new UtilisateurDetailsService(jdbcTemplate);
    }

    private Map<String, Object> utilisateur(String username, String motDePasse, String role) {
        Map<String, Object> ligne = new LinkedHashMap<>();
        ligne.put("username", username);
        ligne.put("mot_de_passe", motDePasse);
        ligne.put("role", role);
        return ligne;
    }

    @Test
    void loadUserByUsername_trouveUtilisateurParUsername() {
        when(jdbcTemplate.queryForList(any(String.class), any(Object[].class)))
                .thenReturn(new ArrayList<>(Collections.singletonList(utilisateur("admin", "{noop}secret", "ADMIN"))));

        UserDetails details = service.loadUserByUsername("admin");

        assertEquals("admin", details.getUsername());
        assertEquals("{noop}secret", details.getPassword());
        assertEquals(1, details.getAuthorities().size());
        assertEquals("ROLE_ADMIN", details.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_chercheAussiParEmail() {
        when(jdbcTemplate.queryForList(any(String.class), any(Object[].class)))
                .thenReturn(new ArrayList<>(Collections.singletonList(utilisateur("admin", "{noop}secret", "ADMIN"))));

        UserDetails details = service.loadUserByUsername("admin@location-voitures.local");

        assertEquals("admin", details.getUsername());
    }

    @Test
    void loadUserByUsername_roleParDefautClientQuandNull() {
        when(jdbcTemplate.queryForList(any(String.class), any(Object[].class)))
                .thenReturn(new ArrayList<>(Collections.singletonList(utilisateur("client1", "{noop}secret", null))));

        UserDetails details = service.loadUserByUsername("client1");

        assertEquals("ROLE_CLIENT", details.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_utilisateurInexistant_leveException() {
        when(jdbcTemplate.queryForList(any(String.class), any(Object[].class)))
                .thenReturn(new ArrayList<>());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("personne"));
    }
}