/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.core.RowMapper
 *  org.springframework.stereotype.Repository
 *  org.springframework.transaction.annotation.Transactional
 */
package com.locationvoitures.repository;

import com.locationvoitures.modele.Location;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class LocationRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Location> locationRowMapper = (resultSet, rowNum) -> {
        Location location = new Location();
        location.setId(resultSet.getInt("id"));
        location.setVoitureId(resultSet.getInt("voiture_id"));
        location.setClientId(resultSet.getInt("client_id"));
        location.setDateDebut(resultSet.getObject("date_debut", LocalDate.class));
        location.setDateFin(resultSet.getObject("date_fin", LocalDate.class));
        location.setPointDepart(resultSet.getString("point_depart"));
        location.setPointRetour(resultSet.getString("point_retour"));
        location.setRemise(resultSet.getBigDecimal("remise"));
        location.setNombreJours(resultSet.getInt("nombre_jours"));
        location.setPrixTotal(resultSet.getBigDecimal("prix_total"));
        location.setVoitureLabel(resultSet.getString("voiture_label"));
        location.setClientLabel(resultSet.getString("client_label"));
        return location;
    };

    public LocationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Location> trouverToutes() {
        String sql = "SELECT l.id, l.voiture_id, l.client_id, l.date_debut, l.date_fin,\n       l.point_depart, l.point_retour, l.remise,\n       l.nombre_jours, l.prix_total,\n       CONCAT(v.marque, ' ', v.modele) AS voiture_label,\n       c.nom_complet AS client_label\nFROM locations l\nJOIN voitures v ON v.id = l.voiture_id\nJOIN clients c ON c.id = l.client_id\nORDER BY l.id DESC\n";
        return this.jdbcTemplate.query(sql, this.locationRowMapper);
    }

    @Transactional
    public void enregistrer(Location location) {
        String sqlLocation = "INSERT INTO locations\n(voiture_id, client_id, date_debut, date_fin, point_depart, point_retour,\n remise, nombre_jours, prix_total)\nVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)\n";
        this.jdbcTemplate.update(sqlLocation, new Object[]{location.getVoitureId(), location.getClientId(), location.getDateDebut(), location.getDateFin(), location.getPointDepart(), location.getPointRetour(), location.getRemise(), location.getNombreJours(), location.getPrixTotal()});
        String sqlStatut = "UPDATE voitures\nSET statut = 'OCCUPE'\nWHERE id = ?\n";
        this.jdbcTemplate.update(sqlStatut, new Object[]{location.getVoitureId()});
    }

    @Transactional
    public void modifier(Location location) {
        Integer ancienVoitureId = (Integer)this.jdbcTemplate.queryForObject("SELECT voiture_id FROM locations WHERE id = ?", Integer.class, new Object[]{location.getId()});
        String sql = "UPDATE locations\nSET voiture_id = ?, client_id = ?, date_debut = ?, date_fin = ?,\n    point_depart = ?, point_retour = ?, remise = ?,\n    nombre_jours = ?, prix_total = ?\nWHERE id = ?\n";
        this.jdbcTemplate.update(sql, new Object[]{location.getVoitureId(), location.getClientId(), location.getDateDebut(), location.getDateFin(), location.getPointDepart(), location.getPointRetour(), location.getRemise(), location.getNombreJours(), location.getPrixTotal(), location.getId()});
        if (ancienVoitureId != null && !ancienVoitureId.equals(location.getVoitureId())) {
            this.remettreDisponibleSiLibre(ancienVoitureId);
        }
        this.jdbcTemplate.update("UPDATE voitures SET statut = 'OCCUPE' WHERE id = ?", new Object[]{location.getVoitureId()});
    }

    @Transactional
    public void supprimer(Integer id) {
        Integer voitureId = (Integer)this.jdbcTemplate.queryForObject("SELECT voiture_id FROM locations WHERE id = ?", Integer.class, new Object[]{id});
        this.jdbcTemplate.update("DELETE FROM locations WHERE id = ?", new Object[]{id});
        if (voitureId != null) {
            this.remettreDisponibleSiLibre(voitureId);
        }
    }

    private void remettreDisponibleSiLibre(Integer voitureId) {
        Integer autres = (Integer)this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM locations WHERE voiture_id = ?", Integer.class, new Object[]{voitureId});
        if (autres == null || autres == 0) {
            this.jdbcTemplate.update("UPDATE voitures SET statut = 'DISPONIBLE' WHERE id = ?", new Object[]{voitureId});
        }
    }

    public int compter() {
        return (Integer)this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM locations", Integer.class);
    }

    public BigDecimal chiffreAffaires() {
        BigDecimal total = (BigDecimal)this.jdbcTemplate.queryForObject("SELECT COALESCE(SUM(prix_total - COALESCE(remise, 0)), 0) FROM locations", BigDecimal.class);
        return total != null ? total : BigDecimal.ZERO;
    }
}
