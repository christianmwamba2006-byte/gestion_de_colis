package com.ngokaf.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

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

    private static final Pattern VERIFICATEUR_HMAC_PATTERN =
            Pattern.compile("hmac-sha256\\$[0-9a-f]{64}");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_colis")
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "reference_colis", nullable = false, length = 100, unique = true)
    private String referenceColis;

    @Column(name = "designation", columnDefinition = "TEXT")
    private String designation;

    @NotNull
    @Positive
    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotBlank
    @Size(max = 50)
    @Column(name = "statut", nullable = false, length = 50)
    private String statut;

    @NotNull
    @PastOrPresent
    @Column(name = "date_enregistrement", nullable = false)
    private LocalDateTime dateEnregistrement;

    // Keep the legacy column unchanged until an approved data migration is performed.
    @JsonIgnore
    @Size(max = 255)
    @Column(name = "code_secret_retrait", length = 255)
    private String verificateurCodeRetrait;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_type_colis", nullable = false)
    private TypeColis typeColis;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_expedition", nullable = false)
    private Expedition expedition;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_destination", nullable = false)
    private Lieu destination;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_destinataire", nullable = false)
    private Contact destinataire;

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
                 String verificateurCodeRetrait, TypeColis typeColis, Expedition expedition,
                 Lieu destination, Contact destinataire, Colis parent) {
        this.referenceColis = referenceColis;
        this.designation = designation;
        this.quantite = quantite;
        this.description = description;
        this.statut = statut;
        this.dateEnregistrement = dateEnregistrement;
        setVerificateurCodeRetrait(verificateurCodeRetrait);
        this.typeColis = typeColis;
        this.expedition = expedition;
        this.destination = destination;
        this.destinataire = destinataire;
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

    @JsonIgnore
    public String getVerificateurCodeRetrait() {
        return verificateurCodeRetrait;
    }

    @JsonIgnore
    public void setVerificateurCodeRetrait(String verificateurCodeRetrait) {
        if (verificateurCodeRetrait != null
                && !VERIFICATEUR_HMAC_PATTERN.matcher(verificateurCodeRetrait).matches()) {
            throw new IllegalArgumentException("Le code de retrait doit être un vérificateur HMAC-SHA-256.");
        }
        this.verificateurCodeRetrait = verificateurCodeRetrait;
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

    public Contact getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(Contact destinataire) {
        this.destinataire = destinataire;
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