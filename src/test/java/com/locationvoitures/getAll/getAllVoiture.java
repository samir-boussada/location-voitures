package com.locationvoitures.getAll;

import com.locationvoitures.modele.Voiture;
import com.locationvoitures.repository.VoitureRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class getAllVoiture {

    @Autowired
    private VoitureRepository voitureRepository;

    @Test
    public void testGetAllVoitures() {
        Voiture voiture = new Voiture();
        voiture.setMarque("Toyota");
        voiture.setModele("Corolla");
        voiture.setPrixJour(new BigDecimal("50.00"));
        voiture.setStatut("DISPONIBLE");
        voitureRepository.ajouter(voiture);

        List<Voiture> voitures = voitureRepository.trouverToutes(null);
        assertTrue(voitures.stream().anyMatch(v -> "Toyota".equals(v.getMarque()) && "Corolla".equals(v.getModele())));

        voitures.stream()
                .filter(v -> "Toyota".equals(v.getMarque()))
                .findFirst()
                .ifPresent(v -> {
                    assertEquals("Corolla", v.getModele());
                    assertEquals(new BigDecimal("50.00"), v.getPrixJour());
                    assertEquals("DISPONIBLE", v.getStatut());
                });
    }
}