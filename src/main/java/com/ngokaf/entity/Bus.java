package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité Bus.
 * <p>
 * Un bus peut effectuer zéro, un ou plusieurs transports.
 * Un transport est effectué par un seul bus.
 * </p>
 */
@Entity
@Table(name = "bus")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bus")
    private Long id;

    @Column(name = "immatriculation", nullable = false, length = 20, unique = true)
    private String immatriculation;

    @Column(name = "marque", nullable = false, length = 50)
    private String marque;

    @Column(name = "modele", nullable = false, length = 50)
    private String modele;

    @Column(name = "capacite", nullable = false)
    @Min(1)
    private Integer capacite;

    @Column(name = "statut", nullable = false, length = 50)
    private String statut;

    @OneToMany(mappedBy = "bus")
    private List<Transport> transports = new ArrayList<>();

    public Bus() {
    }

    public Bus(String immatriculation, String marque, String modele,
               Integer capacite, String statut) {
        this.immatriculation = immatriculation;
        this.marque = marque;
        this.modele = modele;
        this.capacite = capacite;
        this.statut = statut;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
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
        if (!(o instanceof Bus bus))
            return false;
        return Objects.equals(id, bus.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Bus{id=" + id + ", immatriculation='" + immatriculation + "'}";
    }
}