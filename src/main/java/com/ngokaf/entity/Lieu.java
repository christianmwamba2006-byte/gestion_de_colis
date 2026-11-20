package com.ngokaf.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité Lieu.
 * <p>
 * Un lieu peut être associé à zéro, une ou plusieurs agences (cardinalité 0,N).
 * </p>
 */
@Entity
@Table(name = "lieu")
public class Lieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lieu")
    private Long id;

    @Column(name = "nom_lieu", nullable = false, length = 100)
    private String nomLieu;

    @Column(name = "type_lieu", nullable = false, length = 50)
    private String typeLieu;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "lieu")
    private List<Agence> agences = new ArrayList<>();

    public Lieu() {
    }

    public Lieu(String nomLieu, String typeLieu, String description) {
        this.nomLieu = nomLieu;
        this.typeLieu = typeLieu;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomLieu() {
        return nomLieu;
    }

    public void setNomLieu(String nomLieu) {
        this.nomLieu = nomLieu;
    }

    public String getTypeLieu() {
        return typeLieu;
    }

    public void setTypeLieu(String typeLieu) {
        this.typeLieu = typeLieu;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Agence> getAgences() {
        return agences;
    }

    public void setAgences(List<Agence> agences) {
        this.agences = agences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Lieu lieu))
            return false;
        return Objects.equals(id, lieu.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Lieu{id=" + id + ", nomLieu='" + nomLieu + "', typeLieu='" + typeLieu + "'}";
    }
}