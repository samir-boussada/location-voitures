CREATE DATABASE IF NOT EXISTS location_voitures;
USE location_voitures;

CREATE TABLE voitures (
    id INT AUTO_INCREMENT PRIMARY KEY,
    marque VARCHAR(50) NOT NULL,
    modele VARCHAR(50) NOT NULL,
    prix_jour DECIMAL(10,2) NOT NULL,
    statut ENUM('DISPONIBLE','OCCUPE','EN_PANNE','MAINTENANCE') NOT NULL DEFAULT 'DISPONIBLE',
    photo VARCHAR(255),
    moteur VARCHAR(100),
    transmission VARCHAR(50)
);

CREATE TABLE clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom_complet VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telephone VARCHAR(20),
    numero_permis VARCHAR(50),
    UNIQUE KEY email (email),
    UNIQUE KEY numero_permis (numero_permis)
);

CREATE TABLE locations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    voiture_id INT NOT NULL,
    client_id INT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    point_depart VARCHAR(255),
    point_retour VARCHAR(255),
    remise DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    nombre_jours INT NOT NULL,
    prix_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (voiture_id) REFERENCES voitures(id),
    FOREIGN KEY (client_id) REFERENCES clients(id)
);

-- Application user accounts (table fed by application code: admin auto-created on startup)
CREATE TABLE utilisateurs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(100) NOT NULL,
    role VARCHAR(30) DEFAULT 'CLIENT',
    cree_le TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO voitures (marque, modele, prix_jour, statut, photo, moteur, transmission) VALUES
('Renault', 'Symbole', 4500.00, 'DISPONIBLE', NULL, '1.4L essence 75 ch', 'Manuelle'),
('Skoda', 'Fabia', 5500.00, 'DISPONIBLE', NULL, '1.2L essence 90 ch', 'Manuelle'),
('Renault', 'Megane', 7000.00, 'EN_PANNE', NULL, '1.5L diesel 110 ch', 'Manuelle'),
('Dacia', 'Stepway', 6000.00, 'MAINTENANCE', NULL, '1.6L essence 102 ch', 'Manuelle'),
('Range Rover', 'Velar', 25000.00, 'DISPONIBLE', 'ae70e5a750c1.jpg', '2.0L diesel 180 ch', 'Automatique'),
('Renault', 'Clio 4', 5000.00, 'DISPONIBLE', NULL, '1.2L essence 75 ch', 'Manuelle'),
('Hyundai', 'i10', 4000.00, 'DISPONIBLE', NULL, '1.0L essence 67 ch', 'Manuelle'),
('Audi', 'A3', 12000.00, 'DISPONIBLE', NULL, '1.4L essence 150 ch', 'Automatique'),
('Volkswagen', 'Golf 8', 9500.00, 'DISPONIBLE', NULL, '1.4L essence 150 ch', 'Automatique'),
('Volkswagen', 'Polo', 6500.00, 'MAINTENANCE', NULL, '1.4L essence 90 ch', 'Manuelle'),
('Hyundai', 'i20', 5000.00, 'DISPONIBLE', 'c5f089f74122.jpg', '1.2L essence 87 ch', 'Manuelle'),
('Fiat', 'Tipo', 5500.00, 'EN_PANNE', NULL, '1.6L diesel 120 ch', 'Manuelle');

INSERT INTO clients (nom_complet, email, telephone, numero_permis) VALUES
('Sara Meziane', 'sara@mail.com', '0661-789012', 'DZ-4455'),
('Youcef Khediri', 'youcef@mail.com', '0770-345678', 'DZ-6677');