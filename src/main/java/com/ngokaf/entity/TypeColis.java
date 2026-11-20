package com.ngokaf.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité TypeColis.
 * <p>
 * Caractérise un colis par son type physique. Le format d'étiquette proposé
 * dépend du type physique du colis (règle de gestion validée).
 * </p>
 */
@Entity
@Table(name = "type_colis")
public class TypeColis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_colis")
    private Long id;

    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "statut", nullable = false, length = 50)
    private String statut;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_format_default", nullable = true)
    private FormatEtiquette formatDefault;

    @OneToMany(mappedBy = "typeColis")
    private List<Colis> colis = new ArrayList<>();

    public TypeColis() {
    }

    public TypeColis(String libelle, String description, String statut,
                     FormatEtiquette formatDefault) {
        this.libelle = libelle;
        this.description = description;
        this.statut = statut;
        this.formatDefault = formatDefault;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
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

    public FormatEtiquette getFormatDefault() {
        return formatDefault;
    }

    public void setFormatDefault(FormatEtiquette formatDefault) {
        this.formatDefault = formatDefault;
    }

    public List<Colis> getColis() {
        return colis;
    }

    public void setColis(List<Colis> colis) {
        this.colis = colis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TypeColis that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TypeColis{id=" + id + ", libelle='" + libelle + "'}";
    }
}