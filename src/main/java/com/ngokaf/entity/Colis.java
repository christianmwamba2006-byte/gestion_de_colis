package com.ngokaf.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité Colis.
 * <p>
 * Chaque colis possède sa propre destination (un lieu, qui peut être une agence
 * ou un arrêt intermédiaire). Les lots et sous-lots sont représentés par une
 * relation hiérarchique auto-référencée (id_colis_parent). Un colis peut avoir
 * plusieurs étiquettes successives, mais une seule active à la fois.
 * </p>
 */
@Entity
@Table(name = "colis")
public class Colis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_colis")
    private Long id;

    @Column(name = "reference_colis", nullable = false, length = 100, unique = true)
    private String referenceColis;

    @Column(name = "designation", columnDefinition = "TEXT")
    private String designation;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "statut", nullable = false, length = 50)
    private String statut;

    @Column(name = "date_enregistrement", nullable = false)
    private LocalDateTime dateEnregistrement;

    @Column(name = "code_secret_retrait", length = 255)
    private String codeSecretRetrait;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_type_colis", nullable = false)
    private TypeColis typeColis;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_expedition", nullable = false)
    private Expedition expedition;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_lieu_destination", nullable = false)
    private Lieu destination;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_colis_parent", nullable = true)
    private Colis parent;

    @OneToMany(mappedBy = "parent")
    private List<Colis> enfants = new ArrayList<>();

    @OneToMany(mappedBy = "colis")
    private List<Etiquette> etiquettes = new ArrayList<>();

    public Colis() {
    }

    public Colis(String referenceColis, String designation, Integer quantite,
                 String description, String statut, LocalDateTime dateEnregistrement,
                 String codeSecretRetrait, TypeColis typeColis, Expedition expedition,
                 Lieu destination, Colis parent) {
        this.referenceColis = referenceColis;
        this.designation = designation;
        this.quantite = quantite;
        this.description = description;
        this.statut = statut;
        this.dateEnregistrement = dateEnregistrement;
        this.codeSecretRetrait = codeSecretRetrait;
        this.typeColis = typeColis;
        this.expedition = expedition;
        this.destination = destination;
        this.parent = parent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceColis() {
        return referenceColis;
    }

    public void setReferenceColis(String referenceColis) {
        this.referenceColis = referenceColis;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
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

    public LocalDateTime getDateEnregistrement() {
        return dateEnregistrement;
    }

    public void setDateEnregistrement(LocalDateTime dateEnregistrement) {
        this.dateEnregistrement = dateEnregistrement;
    }

    public String getCodeSecretRetrait() {
        return codeSecretRetrait;
    }

    public void setCodeSecretRetrait(String codeSecretRetrait) {
        this.codeSecretRetrait = codeSecretRetrait;
    }

    public TypeColis getTypeColis() {
        return typeColis;
    }

    public void setTypeColis(TypeColis typeColis) {
        this.typeColis = typeColis;
    }

    public Expedition getExpedition() {
        return expedition;
    }

    public void setExpedition(Expedition expedition) {
        this.expedition = expedition;
    }

    public Lieu getDestination() {
        return destination;
    }

    public void setDestination(Lieu destination) {
        this.destination = destination;
    }

    public Colis getParent() {
        return parent;
    }

    public void setParent(Colis parent) {
        this.parent = parent;
    }

    public List<Colis> getEnfants() {
        return enfants;
    }

    public void setEnfants(List<Colis> enfants) {
        this.enfants = enfants;
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
        if (!(o instanceof Colis colis))
            return false;
        return Objects.equals(id, colis.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Colis{id=" + id + ", referenceColis='" + referenceColis + "'}";
    }
}