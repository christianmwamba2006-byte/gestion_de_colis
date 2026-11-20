package com.ngokaf.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité Expedition.
 * <p>
 * Une expédition porte la référence de facture et le montant global du transport.
 * Une expédition peut contenir plusieurs colis et est lancée par un expéditeur (Contact).
 * </p>
 */
@Entity
@Table(name = "expedition")
public class Expedition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_expedition")
    private Long id;

    @Column(name = "reference_facture", nullable = false, length = 100)
    private String referenceFacture;

    @Column(name = "date_enregistrement", nullable = false)
    private LocalDateTime dateEnregistrement;

    @Column(name = "montant_transport", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantTransport;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_contact", nullable = false)
    private Contact expediteur;

    @OneToMany(mappedBy = "expedition")
    private List<Colis> colis = new ArrayList<>();

    public Expedition() {
    }

    public Expedition(String referenceFacture, LocalDateTime dateEnregistrement,
                      BigDecimal montantTransport, String observations, Contact expediteur) {
        this.referenceFacture = referenceFacture;
        this.dateEnregistrement = dateEnregistrement;
        this.montantTransport = montantTransport;
        this.observations = observations;
        this.expediteur = expediteur;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceFacture() {
        return referenceFacture;
    }

    public void setReferenceFacture(String referenceFacture) {
        this.referenceFacture = referenceFacture;
    }

    public LocalDateTime getDateEnregistrement() {
        return dateEnregistrement;
    }

    public void setDateEnregistrement(LocalDateTime dateEnregistrement) {
        this.dateEnregistrement = dateEnregistrement;
    }

    public BigDecimal getMontantTransport() {
        return montantTransport;
    }

    public void setMontantTransport(BigDecimal montantTransport) {
        this.montantTransport = montantTransport;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Contact getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(Contact expediteur) {
        this.expediteur = expediteur;
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
        if (!(o instanceof Expedition expedition))
            return false;
        return Objects.equals(id, expedition.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Expedition{id=" + id + ", referenceFacture='" + referenceFacture + "'}";
    }
}