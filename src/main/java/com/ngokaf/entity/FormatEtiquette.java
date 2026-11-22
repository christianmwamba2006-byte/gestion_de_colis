package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité FormatEtiquette.
 * <p>
 * Définit les dimensions physiques d'une étiquette (largeur, hauteur, support).
 * Plusieurs étiquettes peuvent utiliser le même format.
 * </p>
 */
@Entity
@Table(name = "format_etiquette")
public class FormatEtiquette {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_format")
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nom_format", nullable = false, length = 100)
    private String nomFormat;

    @NotNull
    @Positive
    @Column(name = "largeur", nullable = false, precision = 10, scale = 2)
    private BigDecimal largeur;

    @NotNull
    @Positive
    @Column(name = "hauteur", nullable = false, precision = 10, scale = 2)
    private BigDecimal hauteur;

    @Column(name = "support")
    private String support;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "format")
    private List<Etiquette> etiquettes = new ArrayList<>();

    public FormatEtiquette() {
    }

    public FormatEtiquette(String nomFormat, BigDecimal largeur, BigDecimal hauteur,
                           String support, String description) {
        this.nomFormat = nomFormat;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.support = support;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomFormat() {
        return nomFormat;
    }

    public void setNomFormat(String nomFormat) {
        this.nomFormat = nomFormat;
    }

    public BigDecimal getLargeur() {
        return largeur;
    }

    public void setLargeur(BigDecimal largeur) {
        this.largeur = largeur;
    }

    public BigDecimal getHauteur() {
        return hauteur;
    }

    public void setHauteur(BigDecimal hauteur) {
        this.hauteur = hauteur;
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Etiquette> getEtiquettes() {
        return etiquettes;
    }

    public void setEtiquettes(List<Etiquette> etiquettes) {
        this.etiquettes = etiquettes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof FormatEtiquette that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FormatEtiquette{id=" + id + ", nomFormat='" + nomFormat + "'}";
    }
}