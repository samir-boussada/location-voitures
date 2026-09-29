package com.locationvoitures.remise;

import com.locationvoitures.modele.Client;
import com.locationvoitures.modele.Location;
import com.locationvoitures.modele.Voiture;
import com.locationvoitures.repository.ClientRepository;
import com.locationvoitures.repository.LocationRepository;
import com.locationvoitures.repository.VoitureRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class testRemise {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private VoitureRepository voitureRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    public void testEnregistreRemiseEtCalculeTotalRevenue() {
        Voiture voiture = new Voiture();
        voiture.setMarque("Toyota");
        voiture.setModele("Corolla");
        voiture.setPrixJour(new BigDecimal("120.00"));
        voiture.setStatut("DISPONIBLE");
        voitureRepository.ajouter(voiture);

        Client client = new Client();
        client.setNomComplet("Remise Test Client");
        client.setEmail("remisetest@mail.com");
        client.setTelephone("0600-000000");
        client.setNumeroPermis("DZ-TEST");
        clientRepository.ajouter(client);

        Voiture voitureSauvee = voitureRepository.trouverToutes(null).stream()
                .filter(v -> "Toyota".equals(v.getMarque()) && "Corolla".equals(v.getModele()))
                .findFirst()
                .orElseThrow();

        Client clientSauve = clientRepository.trouverTous().stream()
                .filter(c -> "remisetest@mail.com".equals(c.getEmail()))
                .findFirst()
                .orElseThrow();

        Location location = new Location();
        location.setVoitureId(voitureSauvee.getId());
        location.setClientId(clientSauve.getId());
        location.setDateDebut(LocalDate.of(2026, 9, 20));
        location.setDateFin(LocalDate.of(2026, 9, 25));
        location.setPointDepart("Aeroport");
        location.setPointRetour("Agence");
        location.setRemise(new BigDecimal("50.00"));
        location.setNombreJours(5);
        location.setPrixTotal(new BigDecimal("600.00"));

        locationRepository.enregistrer(location);

        List<Location> locations = locationRepository.trouverToutes();
        assertTrue(locations.stream().anyMatch(l -> l.getVoitureId() != null
                && l.getVoitureId().equals(location.getVoitureId())
                && l.getClientId() != null
                && l.getClientId().equals(location.getClientId())
                && l.getRemise() != null && new BigDecimal("50.00").compareTo(l.getRemise()) == 0));

        Location retrouvee = locations.stream()
                .filter(l -> l.getRemise() != null && new BigDecimal("50.00").compareTo(l.getRemise()) == 0)
                .findFirst()
                .orElseThrow();
        assertEquals(new BigDecimal("600.00"), retrouvee.getPrixTotal());
        assertEquals(new BigDecimal("550.00"), retrouvee.getTotalRevenue());
    }
}