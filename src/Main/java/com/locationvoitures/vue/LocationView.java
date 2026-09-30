/**
 * LocationView  Main view bean of the application.
 *
 * This JSF component handles all server-side business logic:
 *   - Dashboard with statistics (cars, clients, revenue)
 *   - Car CRUD with photo / engine / transmission and technical sheet
 *   - Client CRUD (direct add / update)
 *   - Rental creation with total price calculation and car status set to OCCUPE
 *   - Combined filters on the car table (search, price, status)
 *
 * Annotated @ViewScoped to keep state while navigating
 * between tabs of the same page.
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
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component("locationView")
@ViewScoped
public class LocationView implements Serializable {

    // =========================================================================
    //  DEPENDENCIES (constructor injection)
    // =========================================================================

    private final VoitureRepository voitureRepository;
    private final ClientRepository clientRepository;
    private final LocationRepository locationRepository;

    // =========================================================================
    //  DATA — Lists and form-bound objects
    // =========================================================================

    private List<Voiture> voitures;
    private List<Client> clients;
    private List<Location> locations;

    /** Status used to filter the car table (TOUTES = all) */
    private String statutFiltre = "TOUTES";

    /** Max daily price to filter cars (null = no limit) */
    private BigDecimal prixMax;

    /** Car object bound to the add/edit form */
    private Voiture voiture;

    /** Car whose technical sheet is shown in the window */
    private Voiture ficheSelectionnee;

    /** Client object bound to the add/edit form */
    private Client client;

    /** Rental object being created */
    private Location location;

    // =========================================================================
    //  RENTAL FORM FIELDS
    // =========================================================================

    private String recherche = "";
    private Integer voitureSelectionneeId;
private LocalDate dateDebut = LocalDate.now();

    private LocalDate dateFin = LocalDate.now().plusDays(1);

    private String pointDepart;

    private String pointRetour;

    /** Discount (DZD) applied on the rental being created */
    private BigDecimal remise = BigDecimal.ZERO;

    /** Photo picked on the computer, resized and base64-encoded by the browser */
    private String photoBase64;

    /** Original filename of the chosen photo */
    private String photoNom;

    /** Rental being edited in the dedicated window */
    private Location locationSelectionnee = new Location();

    /** Values entered in the rental edit window */
    private Integer editVoitureId;
    private Integer editClientId;

    // =========================================================================
    //  DASHBOARD STATISTICS
    // =========================================================================

    private int totalVoitures;
    private int nombreVoituresDisponibles;
    private int nombreVoituresOccupees;
    private int nombreVoituresEnPanne;
    private int totalClients;
    private int totalLocations;
    private BigDecimal chiffreAffaires = BigDecimal.ZERO;

    // =========================================================================
    //  CONSTRUCTOR
    // =========================================================================

    public LocationView(
            VoitureRepository voitureRepository,
            ClientRepository clientRepository,
            LocationRepository locationRepository) {
        this.voitureRepository = voitureRepository;
        this.clientRepository = clientRepository;
        this.locationRepository = locationRepository;
    }

    // =========================================================================
    //  INITIALIZATION
    // =========================================================================

    @PostConstruct
    public void initialiser() {
        actualiser();
        nouvelleVoiture();
        nouveauClient();
        location = new Location();
    }

    // =========================================================================
    //  METHODS — Dashboard, search and filters
    // =========================================================================

    /** Reloads all data and statistics from the database */
    public void actualiser() {
        statutFiltre = "TOUTES";
        prixMax = null;
        appliquerFiltres();
        clients = clientRepository.trouverTous();
        locations = locationRepository.trouverToutes();

        totalVoitures = voitureRepository.compter();
        nombreVoituresDisponibles = voitureRepository.compterParStatut("DISPONIBLE");
        nombreVoituresOccupees = voitureRepository.compterParStatut("OCCUPE");
        nombreVoituresEnPanne = voitureRepository.compterParStatut("EN_PANNE");
        totalClients = clientRepository.compter();
        totalLocations = locationRepository.compter();
        chiffreAffaires = locationRepository.chiffreAffaires();
    }

    /** Applies all filters (search + max price + status) to the table */
    public void appliquerFiltres() {
        voitures = voitureRepository.trouverAvecFiltres(recherche, prixMax, statutFiltre);
    }

    /** Filters by search keyword (search bar) */
    public void rechercher() {
        appliquerFiltres();
    }

    /**
     * Shows cars matching a status in the Cars tab.
     * Called when clicking dashboard cards: resets the max
     * price so ONLY cars of that status are shown.
     *
     * @param statut status to filter; null or TOUTES = all cars
     */
    public void filtrerParStatut(String statut) {
        statutFiltre = (statut == null || statut.isBlank()) ? "TOUTES" : statut;
        prixMax = null;
        appliquerFiltres();
    }

    /** Simple navigation to the Clients tab (dashboard Clients card click) */
    public void voirClients() {
        // Tab switch handled client-side (PF('onglets').select)
    }

    /** Simple navigation to the Locations tab (Locations and Revenue cards) */
    public void voirLocations() {
        // Tab switch handled client-side (PF('onglets').select)
    }

    // =========================================================================
    //  METHODS — Car CRUD
    // =========================================================================

    public void nouvelleVoiture() {
        voiture = new Voiture();
        voiture.setStatut("DISPONIBLE");
        voiture.setPrixJour(BigDecimal.ZERO);
    }

    public void modifierVoiture(Voiture voitureSelectionnee) {
        voiture = voitureSelectionnee;
    }

    public void sauvegarderVoiture() {
        if (photoBase64 != null && !photoBase64.isBlank()) {
            sauvegarderPhotoDepuisBase64();
        }
        if (voiture.getId() == null) {
            voitureRepository.ajouter(voiture);
        } else {
            voitureRepository.modifier(voiture);
        }
        photoBase64 = null;
        photoNom = null;
        actualiser();
        nouvelleVoiture();
        ajouterMessage(FacesMessage.SEVERITY_INFO, "Voiture enregistrée", voiture.getLibelle());
    }

    public void supprimerVoiture(Integer id) {
        voitureRepository.supprimer(id);
        actualiser();
        ajouterMessage(FacesMessage.SEVERITY_INFO, "Voiture supprimée", "");
    }

    public void voirFiche(Voiture voitureSelectionnee) {
        ficheSelectionnee = voitureSelectionnee;
    }

    /**
     * Saves the photo chosen on the computer. The browser sends a
     * resized version (max side 1600px) encoded in base64, so the
     * file is always small, whatever the original photo.
     */
    private void sauvegarderPhotoDepuisBase64() {
        try {
            String donnees = photoBase64.contains(",")
                    ? photoBase64.substring(photoBase64.indexOf(',') + 1)
                    : photoBase64;
            byte[] octets = Base64.getDecoder().decode(donnees.trim());
            if (octets.length == 0) {
                return;
            }
            String nomUnique = UUID.randomUUID().toString().replace("-", "").substring(0, 12) + ".jpg";
            Path dossier = Path.of(dossierImagesVoitures());
            Files.createDirectories(dossier);
            Files.write(dossier.resolve(nomUnique), octets);
            voiture.setPhoto(nomUnique);
        } catch (IllegalArgumentException | IOException e) {
            ajouterMessage(FacesMessage.SEVERITY_ERROR,
                    "Impossible d'enregistrer la photo", "L'image choisie est illisible.");
        }
    }

    private String dossierImagesVoitures() {
        String base = System.getProperty("catalina.home", System.getProperty("java.io.tmpdir"));
        return base + File.separator + "webapps"
                + File.separator + "location-voitures"
                + File.separator + "images"
                + File.separator + "voitures";
    }

    // =========================================================================
    //  METHODS — Client CRUD (direct save, no verification)
    // =========================================================================

    public void nouveauClient() {
        client = new Client();
    }

    public void modifierClient(Client clientSelectionne) {
        client = clientSelectionne;
    }

    /**
     * Saves (add or update) the client in one step.
     */
    public void sauvegarderClient() {
        if (client.getEmail() != null && client.getEmail().isBlank()) {
            client.setEmail(null);
        }
        if (client.getNumeroPermis() != null && client.getNumeroPermis().isBlank()) {
            client.setNumeroPermis(null);
        }
        String nom = client.getNomComplet();
        boolean ajout = client.getId() == null;
        if (ajout) {
            clientRepository.ajouter(client);
        } else {
            clientRepository.modifier(client);
        }
        actualiser();
        nouveauClient();
        ajouterMessage(FacesMessage.SEVERITY_INFO,
                ajout ? "Client ajouté" : "Client modifié", nom);
    }

    public void supprimerClient(Integer id) {
        clientRepository.supprimer(id);
        actualiser();
        ajouterMessage(FacesMessage.SEVERITY_INFO, "Client supprimé", "");
    }

    private void ajouterMessage(FacesMessage.Severity severite, String resume, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severite, resume, detail));
    }

    // =========================================================================
    //  METHODS — Rental logic
    // =========================================================================

    public List<Voiture> getVoituresDisponibles() {
        return voitureRepository.trouverToutes("").stream()
                .filter(v -> "DISPONIBLE".equals(v.getStatut()))
                .toList();
    }

    public int getNombreJours() {
        if (dateDebut == null || dateFin == null || dateFin.isBefore(dateDebut)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
    }

    public BigDecimal getPrixTotal() {
        if (voitureSelectionneeId == null || getNombreJours() == 0) {
            return BigDecimal.ZERO;
        }
        return voitureRepository.trouverToutes("").stream()
                .filter(v -> v.getId().equals(voitureSelectionneeId))
                .findFirst()
                .map(v -> v.getPrixJour().multiply(BigDecimal.valueOf(getNombreJours())))
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getRemise() {
        return remise;
    }

    public void setRemise(BigDecimal remise) {
        this.remise = remise != null ? remise : BigDecimal.ZERO;
    }

    public BigDecimal getTotalRevenue() {
        BigDecimal base = getPrixTotal() != null ? getPrixTotal() : BigDecimal.ZERO;
        BigDecimal remiseValue = remise != null ? remise : BigDecimal.ZERO;
        return base.subtract(remiseValue);
    }

    public void confirmerLocation() {
        if (voitureSelectionneeId == null || location.getClientId() == null) {
            return;
        }
        location.setVoitureId(voitureSelectionneeId);
        location.setDateDebut(dateDebut);
        location.setDateFin(dateFin);
        location.setPointDepart(pointDepart);
        location.setPointRetour(pointRetour);
        location.setNombreJours(getNombreJours());
        location.setPrixTotal(getPrixTotal());
        location.setRemise(remise);

        locationRepository.enregistrer(location);

        location = new Location();
        voitureSelectionneeId = null;
        actualiser();
        ajouterMessage(FacesMessage.SEVERITY_INFO, "Location confirmée",
                "La voiture est maintenant occupée.");
    }

    /** Places the selected rental into the edit form */
    public void modifierLocation(Location locationChoisie) {
        locationSelectionnee = locationChoisie;
        editVoitureId = locationChoisie.getVoitureId();
        editClientId = locationChoisie.getClientId();
    }

    /** Number of days recalculated for the rental being edited */
    public int getJoursModification() {
        if (locationSelectionnee == null || locationSelectionnee.getDateDebut() == null
                || locationSelectionnee.getDateFin() == null
                || locationSelectionnee.getDateFin().isBefore(locationSelectionnee.getDateDebut())) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(
                locationSelectionnee.getDateDebut(), locationSelectionnee.getDateFin()) + 1;
    }

    /** Total price recalculated for the rental being edited */
    public BigDecimal getPrixTotalModification() {
        if (editVoitureId == null || getJoursModification() == 0) {
            return BigDecimal.ZERO;
        }
        return prixVoiture(editVoitureId).multiply(BigDecimal.valueOf(getJoursModification()));
    }

    /** Saves the changes to an existing rental */
    public void sauvegarderLocationModifiee() {
        if (locationSelectionnee == null || editVoitureId == null || editClientId == null) {
            ajouterMessage(FacesMessage.SEVERITY_ERROR,
                    "Champs incomplets", "Choisissez une voiture et un client.");
            return;
        }
        locationSelectionnee.setVoitureId(editVoitureId);
        locationSelectionnee.setClientId(editClientId);
        locationSelectionnee.setNombreJours(getJoursModification());
        locationSelectionnee.setPrixTotal(
                prixVoiture(editVoitureId).multiply(BigDecimal.valueOf(getJoursModification())));

        locationRepository.modifier(locationSelectionnee);
        actualiser();
        ajouterMessage(FacesMessage.SEVERITY_INFO, "Location modifiée", "");
    }

    /** Deletes a rental and frees the car */
    public void supprimerLocation(Integer id) {
        locationRepository.supprimer(id);
        actualiser();
        ajouterMessage(FacesMessage.SEVERITY_INFO, "Location supprimée",
                "La voiture est de nouveau disponible.");
    }

    /** Daily price of a car (or zero if not found) */
    private BigDecimal prixVoiture(Integer id) {
        return voitureRepository.trouverAvecFiltres("", null, "TOUTES").stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .map(Voiture::getPrixJour)
                .orElse(BigDecimal.ZERO);
    }

    // =========================================================================
    //  GETTERS / SETTERS
    // =========================================================================

    public List<String> getStatuts() {
        return List.of("DISPONIBLE", "OCCUPE", "EN_PANNE", "MAINTENANCE");
    }

    public List<String> getTransmissions() {
        return List.of("Manuelle", "Automatique");
    }

    public List<Voiture> getVoitures() { return voitures; }
    public List<Client> getClients() { return clients; }
    public List<Location> getLocations() { return locations; }

    public String getStatutFiltre() { return statutFiltre; }
    public void setStatutFiltre(String statutFiltre) {
        this.statutFiltre = statutFiltre;
    }

    public BigDecimal getPrixMax() { return prixMax; }
    public void setPrixMax(BigDecimal prixMax) { this.prixMax = prixMax; }

    public Voiture getVoiture() { return voiture; }
    public void setVoiture(Voiture voiture) { this.voiture = voiture; }

    public Voiture getFicheSelectionnee() { return ficheSelectionnee; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public Location getLocationSelectionnee() { return locationSelectionnee; }
    public void setLocationSelectionnee(Location locationSelectionnee) {
        this.locationSelectionnee = locationSelectionnee;
    }

    public Integer getEditVoitureId() { return editVoitureId; }
    public void setEditVoitureId(Integer editVoitureId) { this.editVoitureId = editVoitureId; }

    public Integer getEditClientId() { return editClientId; }
    public void setEditClientId(Integer editClientId) { this.editClientId = editClientId; }

    public String getRecherche() { return recherche; }
    public void setRecherche(String recherche) { this.recherche = recherche; }

    public Integer getVoitureSelectionneeId() { return voitureSelectionneeId; }
    public void setVoitureSelectionneeId(Integer voitureSelectionneeId) {
        this.voitureSelectionneeId = voitureSelectionneeId;
    }

    public List<Voiture> getVoituresPourEdit() {
        return voitureRepository.trouverAvecFiltres("", null, "TOUTES").stream()
                .filter(v -> "DISPONIBLE".equals(v.getStatut())
                        || v.getId().equals(editVoitureId))
                .toList();
    }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getPointDepart() { return pointDepart; }
    public void setPointDepart(String pointDepart) { this.pointDepart = pointDepart; }

    public String getPointRetour() { return pointRetour; }
    public void setPointRetour(String pointRetour) { this.pointRetour = pointRetour; }

    public String getPhotoBase64() { return photoBase64; }
    public void setPhotoBase64(String photoBase64) { this.photoBase64 = photoBase64; }

    public String getPhotoNom() { return photoNom; }
    public void setPhotoNom(String photoNom) { this.photoNom = photoNom; }

    public int getTotalVoitures() { return totalVoitures; }
    public int getNombreVoituresDisponibles() { return nombreVoituresDisponibles; }
    public int getNombreVoituresOccupees() { return nombreVoituresOccupees; }
    public int getNombreVoituresEnPanne() { return nombreVoituresEnPanne; }
    public int getTotalClients() { return totalClients; }
    public int getTotalLocations() { return totalLocations; }
    public BigDecimal getChiffreAffaires() { return chiffreAffaires; }
}