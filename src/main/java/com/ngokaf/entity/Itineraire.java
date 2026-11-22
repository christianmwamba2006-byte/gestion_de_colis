package com.ngokaf.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité Itineraire.
 * <p>
 * Un itinéraire est un parcours réutilisable par plusieurs transports.
 * </p>
 */
@Entity
@Table(name = "itineraire")
public class Itineraire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_itineraire")
    private Long id;

    @Column(name = "nom_itineraire", nullable = false, length = 100)
    private String nomItineraire;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "statut", nullable = false, length = 50)
    private String statut;

    @OneToMany(mappedBy = "itineraire")
    private List<ArretItineraire> arrets = new ArrayList<>();

    @OneToMany(mappedBy = "itineraire")
    private List<Transport> transports = new ArrayList<>();

    public Itineraire() {
    }

    public Itineraire(String nomItineraire, String description, String statut) {
        this.nomItineraire = nomItineraire;
        this.description = description;
        this.statut = statut;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomItineraire() {
        return nomItineraire;
    }

    public void setNomItineraire(String nomItineraire) {
        this.nomItineraire = nomItineraire;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public List<ArretItineraire> getArrets() {
        return arrets;
    }

    public void setArrets(List<ArretItineraire> arrets) {
        this.arrets = arrets;
    }

    public List<Transport> getTransports() {
        return transports;
    }

    public void setTransports(List<Transport> transports) {
        this.transports = transports;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Itineraire itineraire))
            return false;
        return Objects.equals(id, itineraire.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Itineraire{id=" + id + ", nomItineraire='" + nomItineraire + "'}";
    }
}