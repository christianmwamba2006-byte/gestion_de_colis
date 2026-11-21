package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité représentant une remise effective d'un colis à un réceptionnaire.
 */
@Entity
@Table(name = "remise_colis")
public class RemiseColis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_remise")
    private Long id;

    @NotNull
    @PastOrPresent
    @Column(name = "date_remise", nullable = false)
    private LocalDateTime dateRemise;

    @NotNull
    @Min(1)
    @Column(name = "quantite_remise", nullable = false)
    private Integer quantiteRemise;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nom_receptionnaire", nullable = false, length = 100)
    private String nomReceptionnaire;

    @NotBlank
    @Size(max = 20)
    @Column(name = "telephone_receptionnaire", nullable = false, length = 20)
    private String telephoneReceptionnaire;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_colis", nullable = false)
    private Colis colis;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_emplacement", nullable = true)
    private Emplacement emplacement;

    @OneToMany(mappedBy = "remise")
    private List<TentativeRemise> tentatives = new ArrayList<>();

    public RemiseColis() {
    }

    public RemiseColis(LocalDateTime dateRemise, Integer quantiteRemise,
                       String nomReceptionnaire, String telephoneReceptionnaire,
                       String observations, Utilisateur utilisateur, Colis colis,
                       Emplacement emplacement) {
        this.dateRemise = dateRemise;
        this.quantiteRemise = quantiteRemise;
        this.nomReceptionnaire = nomReceptionnaire;
        this.telephoneReceptionnaire = telephoneReceptionnaire;
        this.observations = observations;
        this.utilisateur = utilisateur;
        this.colis = colis;
        this.emplacement = emplacement;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateRemise() {
        return dateRemise;
    }

    public void setDateRemise(LocalDateTime dateRemise) {
        this.dateRemise = dateRemise;
    }

    public Integer getQuantiteRemise() {
        return quantiteRemise;
    }

    public void setQuantiteRemise(Integer quantiteRemise) {
        this.quantiteRemise = quantiteRemise;
    }

    public String getNomReceptionnaire() {
        return nomReceptionnaire;
    }

    public void setNomReceptionnaire(String nomReceptionnaire) {
        this.nomReceptionnaire = nomReceptionnaire;
    }

    public String getTelephoneReceptionnaire() {
        return telephoneReceptionnaire;
    }

    public void setTelephoneReceptionnaire(String telephoneReceptionnaire) {
        this.telephoneReceptionnaire = telephoneReceptionnaire;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Colis getColis() {
        return colis;
    }

    public void setColis(Colis colis) {
        this.colis = colis;
    }

    public Emplacement getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(Emplacement emplacement) {
        this.emplacement = emplacement;
    }

    public List<TentativeRemise> getTentatives() {
        return tentatives;
    }

    public void setTentatives(List<TentativeRemise> tentatives) {
        this.tentatives = tentatives;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RemiseColis remise))
            return false;
        return Objects.equals(id, remise.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RemiseColis{id=" + id + ", dateRemise=" + dateRemise
                + ", quantiteRemise=" + quantiteRemise + "}";
    }
}
