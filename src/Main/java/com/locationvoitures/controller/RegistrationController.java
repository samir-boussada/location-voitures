/*

 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.security.crypto.password.PasswordEncoder
 *  org.springframework.stereotype.Controller
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestParam
 */
package com.locationvoitures.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value={"/inscription"})
    public String inscrire(@RequestParam String nom, @RequestParam String username, @RequestParam String email, @RequestParam String motDePasse) {
        if (nom == null || nom.isBlank() || username == null || username.isBlank() || email == null || email.isBlank() || motDePasse == null || motDePasse.length() < 6) {
            return "redirect:/registration.html?error=validation";
        }
        Integer doublon = (Integer)this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM utilisateurs\nWHERE LOWER(username) = LOWER(?) OR LOWER(email) = LOWER(?)\n", Integer.class, new Object[]{username, email});
        if (doublon != null && doublon > 0) {
            return "redirect:/registration.html?error=existe";
        }
        this.jdbcTemplate.update("INSERT INTO utilisateurs (nom, username, email, mot_de_passe, role)\nVALUES (?, ?, ?, ?, ?)\n", new Object[]{nom.trim(), username.trim(), email.trim().toLowerCase(), this.passwordEncoder.encode((CharSequence)motDePasse), "CLIENT"});
        return "redirect:/login.html?inscrit=1";
    }
}
