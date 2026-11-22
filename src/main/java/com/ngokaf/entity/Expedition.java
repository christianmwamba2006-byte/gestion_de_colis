package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité Expedition.
 * <p>
 * Une expédition est enregistrée par un utilisateur pour une agence d'origine
 * et un expéditeur (Contact).
 * </p>
 */
@Entity
@Table(name = "expedition")
public class Expedition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_expedition")
    private Long id;

    @NotNull
    @Column(name = "date_enregistrement", nullable = false)
    private LocalDateTime dateEnregistrement;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "montant_transport", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantTransport;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_expediteur", nullable = false)
    private Contact expediteur;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_agence", nullable = false)
    private Agence agenceOrigine;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateurEnregistreur;

    @OneToMany(mappedBy = "expedition")
    private List<Colis> colis = new ArrayList<>();

    public Expedition() {
    }

    public Expedition(BigDecimal montantTransport, String observations, Contact expediteur,
                      Agence agenceOrigine, Utilisateur utilisateurEnregistreur) {
        this.montantTransport = montantTransport;
        this.observations = observations;
        this.expediteur = expediteur;
        this.agenceOrigine = agenceOrigine;
        this.utilisateurEnregistreur = utilisateurEnregistreur;
    }

    @PrePersist
    private void definirDateEnregistrement() {
        dateEnregistrement = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateEnregistrement() {
        return dateEnregistrement;
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

    public Agence getAgenceOrigine() {
        return agenceOrigine;
    }

    public void setAgenceOrigine(Agence agenceOrigine) {
        this.agenceOrigine = agenceOrigine;
    }

    public Utilisateur getUtilisateurEnregistreur() {
        return utilisateurEnregistreur;
    }

    public void setUtilisateurEnregistreur(Utilisateur utilisateurEnregistreur) {
        this.utilisateurEnregistreur = utilisateurEnregistreur;
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
        return "Expedition{id=" + id + "}";
    }
}