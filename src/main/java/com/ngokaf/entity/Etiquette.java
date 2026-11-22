package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité Etiquette.
 * <p>
 * Chaque étiquette conserve une référence QR et son format. Son statut est
 * l'unique source de vérité pour son cycle de vie.
 * </p>
 */
@Entity
@Table(name = "etiquette")
public class Etiquette {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_etiquette")
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(name = "reference_qr", nullable = false, length = 255, unique = true)
    private String referenceQr;

    @NotNull
    @Column(name = "date_generation", nullable = false)
    private LocalDateTime dateGeneration;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 50)
    private StatutEtiquette statut;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_colis", nullable = false)
    private Colis colis;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_format", nullable = false)
    private FormatEtiquette format;

    public Etiquette() {
    }

    public Etiquette(String referenceQr, Colis colis, FormatEtiquette format) {
        this.referenceQr = referenceQr;
        this.statut = StatutEtiquette.ACTIVE;
        this.colis = colis;
        this.format = format;
    }

    @PrePersist
    private void definirDateGeneration() {
        dateGeneration = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceQr() {
        return referenceQr;
    }

    public LocalDateTime getDateGeneration() {
        return dateGeneration;
    }

    public StatutEtiquette getStatut() {
        return statut;
    }

    public void remplacer() {
        if (statut != StatutEtiquette.ACTIVE) {
            throw new IllegalStateException("Seule une étiquette active peut être remplacée.");
        }
        statut = StatutEtiquette.REMPLACEE;
    }

    public void desactiver() {
        if (statut != StatutEtiquette.ACTIVE) {
            throw new IllegalStateException("Seule une étiquette active peut être désactivée.");
        }
        statut = StatutEtiquette.DESACTIVEE;
    }

    public Colis getColis() {
        return colis;
    }

    public void setColis(Colis colis) {
        this.colis = colis;
    }

    public FormatEtiquette getFormat() {
        return format;
    }

    public void setFormat(FormatEtiquette format) {
        this.format = format;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Etiquette etiquette))
            return false;
        return Objects.equals(id, etiquette.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Etiquette{id=" + id + ", referenceQr='" + referenceQr + "', statut=" + statut + "}";
    }
}