/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.core.RowMapper
 *  org.springframework.stereotype.Repository
 */
package com.locationvoitures.repository;

import com.locationvoitures.modele.Voiture;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class VoitureRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Voiture> voitureRowMapper = (resultSet, rowNum) -> {
        Voiture voiture = new Voiture();
        voiture.setId(resultSet.getInt("id"));
        voiture.setMarque(resultSet.getString("marque"));
        voiture.setModele(resultSet.getString("modele"));
        voiture.setPrixJour(resultSet.getBigDecimal("prix_jour"));
        voiture.setStatut(resultSet.getString("statut"));
        voiture.setPhoto(resultSet.getString("photo"));
        voiture.setMoteur(resultSet.getString("moteur"));
        voiture.setTransmission(resultSet.getString("transmission"));
        return voiture;
    };

    public VoitureRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Voiture> trouverToutes(String recherche) {
        return this.trouverAvecFiltres(recherche, null, null);
    }

    public List<Voiture> trouverAvecFiltres(String recherche, BigDecimal prixMax, String statut) {
        StringBuilder sql = new StringBuilder("SELECT * FROM voitures WHERE 1=1");
        ArrayList<Object> parametres = new ArrayList<Object>();
        if (recherche != null && !recherche.isBlank()) {
            sql.append(" AND (LOWER(marque) LIKE ? OR LOWER(modele) LIKE ?)");
            String mot = "%" + recherche.toLowerCase() + "%";
            parametres.add(mot);
            parametres.add(mot);
        }
        if (prixMax != null) {
            sql.append(" AND prix_jour <= ?");
            parametres.add(prixMax);
        }
        if (statut != null && !statut.isBlank() && !"TOUTES".equalsIgnoreCase(statut)) {
            sql.append(" AND statut = ?");
            parametres.add(statut);
        }
        sql.append(" ORDER BY marque, modele");
        return this.jdbcTemplate.query(sql.toString(), this.voitureRowMapper, parametres.toArray());
    }

    public void ajouter(Voiture voiture) {
        String sql = "INSERT INTO voitures (marque, modele, prix_jour, statut, photo, moteur, transmission)\nVALUES (?, ?, ?, ?, ?, ?, ?)\n";
        this.jdbcTemplate.update(sql, new Object[]{voiture.getMarque(), voiture.getModele(), voiture.getPrixJour(), voiture.getStatut(), voiture.getPhoto(), voiture.getMoteur(), voiture.getTransmission()});
    }

    public void modifier(Voiture voiture) {
        String sql = "UPDATE voitures\nSET marque = ?,\n    modele = ?,\n    prix_jour = ?,\n    statut = ?,\n    photo = ?,\n    moteur = ?,\n    transmission = ?\nWHERE id = ?\n";
        this.jdbcTemplate.update(sql, new Object[]{voiture.getMarque(), voiture.getModele(), voiture.getPrixJour(), voiture.getStatut(), voiture.getPhoto(), voiture.getMoteur(), voiture.getTransmission(), voiture.getId()});
    }

    public void supprimer(Integer id) {
        String sql = "DELETE FROM voitures WHERE id = ?";
        this.jdbcTemplate.update(sql, new Object[]{id});
    }

    public int compter() {
        return (Integer)this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM voitures", Integer.class);
    }

    public int compterParStatut(String statut) {
        return (Integer)this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM voitures WHERE statut = ?", Integer.class, new Object[]{statut});
    }

    public List<Voiture> trouverParStatut(String statut) {
        String sql = "SELECT * FROM voitures\nWHERE statut = ?\nORDER BY marque, modele\n";
        return this.jdbcTemplate.query(sql, this.voitureRowMapper, new Object[]{statut});
    }
}
