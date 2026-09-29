package com.locationvoitures.modele;

import com.locationvoitures.repository.ClientRepository;
import com.locationvoitures.repository.LocationRepository;
import com.locationvoitures.repository.VoitureRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class LocationStatutOccupeTest {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private VoitureRepository voitureRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    public void nouvelleLocationMarqueLaVoitureOccupe() {
        Voiture voiture = new Voiture();
        voiture.setMarque("Renault");
        voiture.setModele("Clio");
        voiture.setPrixJour(new BigDecimal("80.00"));
        voiture.setStatut("DISPONIBLE");
        voitureRepository.ajouter(voiture);

        Client client = new Client();
        client.setNomComplet("Client Statut Test");
        client.setEmail("statuttest@mail.com");
        client.setTelephone("0601-111111");
        client.setNumeroPermis("DZ-STATUT");
        clientRepository.ajouter(client);

        Voiture voitureSauvee = voitureRepository.trouverToutes(null).stream()
                .filter(v -> "Renault".equals(v.getMarque()) && "Clio".equals(v.getModele()))
                .findFirst()
                .orElseThrow();
        assertEquals("DISPONIBLE", voitureSauvee.getStatut());

        Client clientSauve = clientRepository.trouverTous().stream()
                .filter(c -> "statuttest@mail.com".equals(c.getEmail()))
                .findFirst()
                .orElseThrow();

        Location location = new Location();
        location.setVoitureId(voitureSauvee.getId());
        location.setClientId(clientSauve.getId());
        location.setDateDebut(LocalDate.of(2026, 9, 20));
        location.setDateFin(LocalDate.of(2026, 9, 25));
        location.setPointDepart("Agence");
        location.setPointRetour("Agence");
        location.setRemise(new BigDecimal("0.00"));
        location.setNombreJours(5);
        location.setPrixTotal(new BigDecimal("400.00"));

        locationRepository.enregistrer(location);

        Voiture voitureApres = voitureRepository.trouverToutes(null).stream()
                .filter(v -> v.getId().equals(voitureSauvee.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals("OCCUPE", voitureApres.getStatut());

        assertTrue(voitureRepository.trouverParStatut("DISPONIBLE").stream()
                .noneMatch(v -> v.getId().equals(voitureSauvee.getId())));
    }
}