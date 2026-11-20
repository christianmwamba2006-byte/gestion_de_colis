package com.ngokaf.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité Agence.
 * <p>
 * Une agence appartient obligatoirement à un seul lieu (cardinalité 1,1 vers Lieu).
 * Un lieu peut être associé à zéro, une ou plusieurs agences (cardinalité 0,N).
 * </p>
 */
@Entity
@Table(name = "agence")
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agence")
    private Long id;

    @Column(name = "nom_agence", nullable = false, length = 100)
    private String nomAgence;

    @Column(name = "adresse", nullable = false, length = 255)
    private String adresse;

    @Column(name = "telephone", nullable = false, length = 20)
    private String telephone;

    @Column(name = "statut", nullable = false, length = 50)
    private String statut;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_lieu", nullable = false)
    private Lieu lieu;

    @OneToMany(mappedBy = "agence")
    private List<Affectation> affectations = new ArrayList<>();

    public Agence() {
    }

    public Agence(String nomAgence, String adresse, String telephone, String statut, Lieu lieu) {
        this.nomAgence = nomAgence;
        this.adresse = adresse;
        this.telephone = telephone;
        this.statut = statut;
        this.lieu = lieu;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomAgence() {
        return nomAgence;
    }

    public void setNomAgence(String nomAgence) {
        this.nomAgence = nomAgence;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public List<Affectation> getAffectations() {
        return affectations;
    }

    public void setAffectations(List<Affectation> affectations) {
        this.affectations = affectations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Agence agence))
            return false;
        return Objects.equals(id, agence.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Agence{id=" + id + ", nomAgence='" + nomAgence + "', statut='" + statut + "'}";
    }
}