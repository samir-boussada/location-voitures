/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.boot.ApplicationArguments
 *  org.springframework.boot.ApplicationRunner
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.security.crypto.password.PasswordEncoder
 *  org.springframework.stereotype.Component
 */
package com.locationvoitures.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitialiserAdmin
implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(InitialiserAdmin.class);
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public InitialiserAdmin(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public void run(ApplicationArguments args) {
        Integer nombre = (Integer)this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM utilisateurs WHERE username = 'admin'", Integer.class);
        if (nombre != null && nombre > 0) {
            return;
        }
        this.jdbcTemplate.update("INSERT INTO utilisateurs (nom, username, email, mot_de_passe, role)\nVALUES (?, ?, ?, ?, ?)\n", new Object[]{"Administrateur", "admin", "admin@location-voitures.local", this.passwordEncoder.encode((CharSequence)"triztech123"), "ADMIN"});
        log.info("Admin user created (admin / triztech123)");
    }
}
