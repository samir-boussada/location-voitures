/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.security.core.userdetails.User
 *  org.springframework.security.core.userdetails.UserDetails
 *  org.springframework.security.core.userdetails.UserDetailsService
 *  org.springframework.security.core.userdetails.UsernameNotFoundException
 *  org.springframework.stereotype.Service
 */
package com.locationvoitures.service;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurDetailsService
implements UserDetailsService {
    private final JdbcTemplate jdbcTemplate;

    public UtilisateurDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserDetails loadUserByUsername(String identifiant) throws UsernameNotFoundException {
        List resultats = this.jdbcTemplate.queryForList("SELECT username, mot_de_passe, role FROM utilisateurs\nWHERE username = ? OR email = ?\n", new Object[]{identifiant, identifiant});
        if (resultats.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + identifiant);
        }
        Map ligne = (Map)resultats.get(0);
        String username = (String)ligne.get("username");
        String motDePasse = (String)ligne.get("mot_de_passe");
        String role = (String)ligne.get("role");
        return User.withUsername((String)username).password(motDePasse).roles(new String[]{role == null || role.isBlank() ? "CLIENT" : role}).build();
    }
}
