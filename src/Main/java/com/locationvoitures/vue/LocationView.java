/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.annotation.PostConstruct
 *  jakarta.faces.application.FacesMessage
 *  jakarta.faces.application.FacesMessage$Severity
 *  jakarta.faces.context.FacesContext
 *  jakarta.faces.view.ViewScoped
 *  org.springframework.stereotype.Component
 */
package com.locationvoitures.vue;

import com.locationvoitures.modele.Client;
import com.locationvoitures.modele.Location;
import com.locationvoitures.modele.Voiture;
import com.locationvoitures.repository.ClientRepository;
import com.locationvoitures.repository.LocationRepository;
import com.locationvoitures.repository.VoitureRepository;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component(value="locationView")
@ViewScoped
public class LocationView
implements Serializable {
    private final VoitureRepository voitureRepository;
    private final ClientRepository clientRepository;
    private final LocationRepository locationRepository;
    private List<Voiture> voitures;
    private List<Client> clients;
    private List<Location> locations;
    private String statutFiltre = "TOUTES";
    private BigDecimal prixMax;
    private Voiture voiture;
    private Voiture ficheSelectionnee;
    private Client client;
    private Location location;
    private String recherche = "";
    private Integer voitureSelectionneeId;
    private LocalDate dateDebut = LocalDate.now();
    private LocalDate dateFin = LocalDate.now().plusDays(1L);
    private String pointDepart;
    private String pointRetour;
    private BigDecimal remise = BigDecimal.ZERO;
    private String photoBase64;
    private String photoNom;
    private Location locationSelectionnee = new Location();
    private Integer editVoitureId;
    private Integer editClientId;
    private int totalVoitures;
    private int nombreVoituresDisponibles;
    private int nombreVoituresOccupees;
    private int nombreVoituresEnPanne;
    private int totalClients;
    private int totalLocations;
    private BigDecimal chiffreAffaires = BigDecimal.ZERO;

    public LocationView(VoitureRepository voitureRepository, ClientRepository clientRepository, LocationRepository locationRepository) {
        this.voitureRepository = voitureRepository;
        this.clientRepository = clientRepository;
        this.locationRepository = locationRepository;
    }

    @PostConstruct
    public void initialiser() {
        this.actualiser();
        this.nouvelleVoiture();
        this.nouveauClient();
        this.location = new Location();
    }

    public void actualiser() {
        this.statutFiltre = "TOUTES";
        this.prixMax = null;
        this.appliquerFiltres();
        this.clients = this.clientRepository.trouverTous();
        this.locations = this.locationRepository.trouverToutes();
        this.totalVoitures = this.voitureRepository.compter();
        this.nombreVoituresDisponibles = this.voitureRepository.compterParStatut("DISPONIBLE");
        this.nombreVoituresOccupees = this.voitureRepository.compterParStatut("OCCUPE");
        this.nombreVoituresEnPanne = this.voitureRepository.compterParStatut("EN_PANNE");
        this.totalClients = this.clientRepository.compter();
        this.totalLocations = this.locationRepository.compter();
        this.chiffreAffaires = this.locationRepository.chiffreAffaires();
    }

    public void appliquerFiltres() {
        this.voitures = this.voitureRepository.trouverAvecFiltres(this.recherche, this.prixMax, this.statutFiltre);
    }

    public void rechercher() {
        this.appliquerFiltres();
    }

    public void filtrerParStatut(String statut) {
        this.statutFiltre = statut == null || statut.isBlank() ? "TOUTES" : statut;
        this.prixMax = null;
        this.appliquerFiltres();
    }

    public void voirClients() {
    }

    public void voirLocations() {
    }

    public void nouvelleVoiture() {
        this.voiture = new Voiture();
        this.voiture.setStatut("DISPONIBLE");
        this.voiture.setPrixJour(BigDecimal.ZERO);
    }

    public void modifierVoiture(Voiture voitureSelectionnee) {
        this.voiture = voitureSelectionnee;
    }

    public void sauvegarderVoiture() {
        if (this.photoBase64 != null && !this.photoBase64.isBlank()) {
            this.sauvegarderPhotoDepuisBase64();
        }
        if (this.voiture.getId() == null) {
            this.voitureRepository.ajouter(this.voiture);
        } else {
            this.voitureRepository.modifier(this.voiture);
        }
        this.photoBase64 = null;
        this.photoNom = null;
        this.actualiser();
        this.nouvelleVoiture();
        this.ajouterMessage(FacesMessage.SEVERITY_INFO, "Voiture enregistr\u00e9e", this.voiture.getLibelle());
    }

    public void supprimerVoiture(Integer id) {
        this.voitureRepository.supprimer(id);
        this.actualiser();
        this.ajouterMessage(FacesMessage.SEVERITY_INFO, "Voiture supprim\u00e9e", "");
    }

    public void voirFiche(Voiture voitureSelectionnee) {
        this.ficheSelectionnee = voitureSelectionnee;
    }

    private void sauvegarderPhotoDepuisBase64() {
        try {
            String donnees = this.photoBase64.contains(",") ? this.photoBase64.substring(this.photoBase64.indexOf(44) + 1) : this.photoBase64;
            byte[] octets = Base64.getDecoder().decode(donnees.trim());
            if (octets.length == 0) {
                return;
            }
            String nomUnique = UUID.randomUUID().toString().replace("-", "").substring(0, 12) + ".jpg";
            Path dossier = Path.of(this.dossierImagesVoitures(), new String[0]);
            Files.createDirectories(dossier, new FileAttribute[0]);
            Files.write(dossier.resolve(nomUnique), octets, new OpenOption[0]);
            this.voiture.setPhoto(nomUnique);
        }
        catch (IOException | IllegalArgumentException e) {
            this.ajouterMessage(FacesMessage.SEVERITY_ERROR, "Impossible d'enregistrer la photo", "L'image choisie est illisible.");
        }
    }

    private String dossierImagesVoitures() {
        String base = System.getProperty("catalina.home", System.getProperty("java.io.tmpdir"));
        return base + File.separator + "webapps" + File.separator + "location-voitures" + File.separator + "images" + File.separator + "voitures";
    }

    public void nouveauClient() {
        this.client = new Client();
    }

    public void modifierClient(Client clientSelectionne) {
        this.client = clientSelectionne;
    }

    public void sauvegarderClient() {
        boolean ajout;
        if (this.client.getEmail() != null && this.client.getEmail().isBlank()) {
            this.client.setEmail(null);
        }
        if (this.client.getNumeroPermis() != null && this.client.getNumeroPermis().isBlank()) {
            this.client.setNumeroPermis(null);
        }
        String nom = this.client.getNomComplet();
        boolean bl = ajout = this.client.getId() == null;
        if (ajout) {
            this.clientRepository.ajouter(this.client);
        } else {
            this.clientRepository.modifier(this.client);
        }
        this.actualiser();
        this.nouveauClient();
        this.ajouterMessage(FacesMessage.SEVERITY_INFO, ajout ? "Client ajout\u00e9" : "Client modifi\u00e9", nom);
    }

    public void supprimerClient(Integer id) {
        this.clientRepository.supprimer(id);
        this.actualiser();
        this.ajouterMessage(FacesMessage.SEVERITY_INFO, "Client supprim\u00e9", "");
    }

    private void ajouterMessage(FacesMessage.Severity severite, String resume, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severite, resume, detail));
    }

    public List<Voiture> getVoituresDisponibles() {
        return this.voitureRepository.trouverToutes("").stream().filter(v -> "DISPONIBLE".equals(v.getStatut())).toList();
    }

    public int getNombreJours() {
        if (this.dateDebut == null || this.dateFin == null || this.dateFin.isBefore(this.dateDebut)) {
            return 0;
        }
        return (int)ChronoUnit.DAYS.between(this.dateDebut, this.dateFin) + 1;
    }

    public BigDecimal getPrixTotal() {
        if (this.voitureSelectionneeId == null || this.getNombreJours() == 0) {
            return BigDecimal.ZERO;
        }
        return this.voitureRepository.trouverToutes("").stream().filter(v -> v.getId().equals(this.voitureSelectionneeId)).findFirst().map(v -> v.getPrixJour().multiply(BigDecimal.valueOf(this.getNombreJours()))).orElse(BigDecimal.ZERO);
    }

    public void confirmerLocation() {
        if (this.voitureSelectionneeId == null || this.location.getClientId() == null) {
            return;
        }
        this.location.setVoitureId(this.voitureSelectionneeId);
        this.location.setDateDebut(this.dateDebut);
        this.location.setDateFin(this.dateFin);
        this.location.setPointDepart(this.pointDepart);
        this.location.setPointRetour(this.pointRetour);
        this.location.setRemise(this.remise);
        this.location.setNombreJours(this.getNombreJours());
        this.location.setPrixTotal(this.getPrixTotal());
        this.locationRepository.enregistrer(this.location);
        this.location = new Location();
        this.voitureSelectionneeId = null;
        this.actualiser();
        this.ajouterMessage(FacesMessage.SEVERITY_INFO, "Location confirm\u00e9e", "La voiture est maintenant occup\u00e9e.");
    }

    public void modifierLocation(Location locationChoisie) {
        this.locationSelectionnee = locationChoisie;
        this.editVoitureId = locationChoisie.getVoitureId();
        this.editClientId = locationChoisie.getClientId();
    }
    
    public int getJoursModification() {
        if (this.locationSelectionnee == null || this.locationSelectionnee.getDateDebut() == null || this.locationSelectionnee.getDateFin() == null || this.locationSelectionnee.getDateFin().isBefore(this.locationSelectionnee.getDateDebut())) {
            return 0;
        }
        return (int)ChronoUnit.DAYS.between(this.locationSelectionnee.getDateDebut(), this.locationSelectionnee.getDateFin()) + 1;
    }

    public BigDecimal getPrixTotalModification() {
        if (this.editVoitureId == null || this.getJoursModification() == 0) {
            return BigDecimal.ZERO;
        }
        return this.prixVoiture(this.editVoitureId).multiply(BigDecimal.valueOf(this.getJoursModification()));
    }

    public void sauvegarderLocationModifiee() {
        if (this.locationSelectionnee == null || this.editVoitureId == null || this.editClientId == null) {
            this.ajouterMessage(FacesMessage.SEVERITY_ERROR, "Champs incomplets", "Choisissez une voiture et un client.");
            return;
        }
        this.locationSelectionnee.setVoitureId(this.editVoitureId);
        this.locationSelectionnee.setClientId(this.editClientId);
        this.locationSelectionnee.setNombreJours(this.getJoursModification());
        this.locationSelectionnee.setPrixTotal(this.prixVoiture(this.editVoitureId).multiply(BigDecimal.valueOf(this.getJoursModification())));
        this.locationRepository.modifier(this.locationSelectionnee);
        this.actualiser();
        this.ajouterMessage(FacesMessage.SEVERITY_INFO, "Location modifi\u00e9e", "");
    }

    public void supprimerLocation(Integer id) {
        this.locationRepository.supprimer(id);
        this.actualiser();
        this.ajouterMessage(FacesMessage.SEVERITY_INFO, "Location supprim\u00e9e", "La voiture est de nouveau disponible.");
    }

    private BigDecimal prixVoiture(Integer id) {
        return this.voitureRepository.trouverAvecFiltres("", null, "TOUTES").stream().filter(v -> v.getId().equals(id)).findFirst().map(Voiture::getPrixJour).orElse(BigDecimal.ZERO);
    }

    public List<String> getStatuts() {
        return List.of("DISPONIBLE", "OCCUPE", "EN_PANNE", "MAINTENANCE");
    }

    public List<String> getTransmissions() {
        return List.of("Manuelle", "Automatique");
    }

    public List<Voiture> getVoitures() {
        return this.voitures;
    }

    public List<Client> getClients() {
        return this.clients;
    }

    public List<Location> getLocations() {
        return this.locations;
    }

    public String getStatutFiltre() {
        return this.statutFiltre;
    }

    public void setStatutFiltre(String statutFiltre) {
        this.statutFiltre = statutFiltre;
    }

    public BigDecimal getPrixMax() {
        return this.prixMax;
    }

    public void setPrixMax(BigDecimal prixMax) {
        this.prixMax = prixMax;
    }

    public Voiture getVoiture() {
        return this.voiture;
    }

    public void setVoiture(Voiture voiture) {
        this.voiture = voiture;
    }

    public Voiture getFicheSelectionnee() {
        return this.ficheSelectionnee;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocationSelectionnee() {
        return this.locationSelectionnee;
    }

    public void setLocationSelectionnee(Location locationSelectionnee) {
        this.locationSelectionnee = locationSelectionnee;
    }

    public Integer getEditVoitureId() {
        return this.editVoitureId;
    }

    public void setEditVoitureId(Integer editVoitureId) {
        this.editVoitureId = editVoitureId;
    }

    public Integer getEditClientId() {
        return this.editClientId;
    }

    public void setEditClientId(Integer editClientId) {
        this.editClientId = editClientId;
    }

    public String getRecherche() {
        return this.recherche;
    }

    public void setRecherche(String recherche) {
        this.recherche = recherche;
    }

    public Integer getVoitureSelectionneeId() {
        return this.voitureSelectionneeId;
    }

    public void setVoitureSelectionneeId(Integer voitureSelectionneeId) {
        this.voitureSelectionneeId = voitureSelectionneeId;
    }

    public List<Voiture> getVoituresPourEdit() {
        return this.voitureRepository.trouverAvecFiltres("", null, "TOUTES").stream().filter(v -> "DISPONIBLE".equals(v.getStatut()) || v.getId().equals(this.editVoitureId)).toList();
    }

    public LocalDate getDateDebut() {
        return this.dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return this.dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public String getPointDepart() {
        return this.pointDepart;
    }

    public void setPointDepart(String pointDepart) {
        this.pointDepart = pointDepart;
    }

    public String getPointRetour() {
        return this.pointRetour;
    }

    public void setPointRetour(String pointRetour) {
        this.pointRetour = pointRetour;
    }

    public BigDecimal getRemise() {
        return this.remise;
    }

    public void setRemise(BigDecimal remise) {
        this.remise = remise != null ? remise : BigDecimal.ZERO;
    }

    public BigDecimal getTotalRevenue() {
        BigDecimal base = this.getPrixTotal() != null ? this.getPrixTotal() : BigDecimal.ZERO;
        BigDecimal remiseValue = this.remise != null ? this.remise : BigDecimal.ZERO;
        return base.subtract(remiseValue);
    }

    public String getPhotoBase64() {
        return this.photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }

    public String getPhotoNom() {
        return this.photoNom;
    }

    public void setPhotoNom(String photoNom) {
        this.photoNom = photoNom;
    }

    public int getTotalVoitures() {
        return this.totalVoitures;
    }

    public int getNombreVoituresDisponibles() {
        return this.nombreVoituresDisponibles;
    }

    public int getNombreVoituresOccupees() {
        return this.nombreVoituresOccupees;
    }

    public int getNombreVoituresEnPanne() {
        return this.nombreVoituresEnPanne;
    }

    public int getTotalClients() {
        return this.totalClients;
    }

    public int getTotalLocations() {
        return this.totalLocations;
    }

    public BigDecimal getChiffreAffaires() {
        return this.chiffreAffaires;
    }
}
