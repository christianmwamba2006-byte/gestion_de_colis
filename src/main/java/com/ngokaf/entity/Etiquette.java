package com.ngokaf.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité Etiquette.
 * <p>
 * Chaque étiquette possède sa propre référence QR. Une seule étiquette peut
 * être active par colis (champ estActive). Lorsqu'une nouvelle étiquette
 * remplace une ancienne, l'ancienne devient inactive. L'historique des
 * anciennes étiquettes est conservé.
 * </p>
 */
@Entity
@Table(name = "etiquette")
public class Etiquette {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_etiquette")
    private Long id;

    @Column(name = "reference_qr", nullable = false, length = 255, unique = true)
    private String referenceQr;

    @Column(name = "date_generation", nullable = false)
    private LocalDateTime dateGeneration;

    @Column(name = "statut", nullable = false, length = 50)
    private String statut;

    @Column(name = "est_active", nullable = false)
    private Boolean estActive;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_colis", nullable = false)
    private Colis colis;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_format", nullable = false)
    private FormatEtiquette format;

    public Etiquette() {
    }

    public Etiquette(String referenceQr, LocalDateTime dateGeneration, String statut,
                     Boolean estActive, Colis colis, FormatEtiquette format) {
        this.referenceQr = referenceQr;
        this.dateGeneration = dateGeneration;
        this.statut = statut;
        this.estActive = estActive;
        this.colis = colis;
        this.format = format;
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

    public void setReferenceQr(String referenceQr) {
        this.referenceQr = referenceQr;
    }

    public LocalDateTime getDateGeneration() {
        return dateGeneration;
    }

    public void setDateGeneration(LocalDateTime dateGeneration) {
        this.dateGeneration = dateGeneration;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Boolean getEstActive() {
        return estActive;
    }

    public void setEstActive(Boolean estActive) {
        this.estActive = estActive;
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
        return "Etiquette{id=" + id + ", referenceQr='" + referenceQr + "', estActive=" + estActive + "}";
    }
}