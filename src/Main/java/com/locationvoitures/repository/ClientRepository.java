/*
 
 * 

 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.core.RowMapper
 *  org.springframework.stereotype.Repository
 */
package com.locationvoitures.repository;

import com.locationvoitures.modele.Client;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ClientRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Client> clientRowMapper = (resultSet, rowNum) -> {
        Client client = new Client();
        client.setId(resultSet.getInt("id"));
        client.setNomComplet(resultSet.getString("nom_complet"));
        client.setEmail(resultSet.getString("email"));
        client.setTelephone(resultSet.getString("telephone"));
        client.setNumeroPermis(resultSet.getString("numero_permis"));
        return client;
    };

    public ClientRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Client> trouverTous() {
        String sql = "SELECT * FROM clients ORDER BY nom_complet";
        return this.jdbcTemplate.query(sql, this.clientRowMapper);
    }

    public void ajouter(Client client) {
        String sql = "INSERT INTO clients\n(nom_complet, email, telephone, numero_permis)\nVALUES (?, ?, ?, ?)\n";
        this.jdbcTemplate.update(sql, new Object[]{client.getNomComplet(), client.getEmail(), client.getTelephone(), client.getNumeroPermis()});
    }

    public void modifier(Client client) {
        String sql = "UPDATE clients\nSET nom_complet = ?,\n    email = ?,\n    telephone = ?,\n    numero_permis = ?\nWHERE id = ?\n";
        this.jdbcTemplate.update(sql, new Object[]{client.getNomComplet(), client.getEmail(), client.getTelephone(), client.getNumeroPermis(), client.getId()});
    }

    public void supprimer(Integer id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        this.jdbcTemplate.update(sql, new Object[]{id});
    }

    public int compter() {
        return (Integer)this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM clients", Integer.class);
    }
}
