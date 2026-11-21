package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité représentant une tentative de remise qui n'a pas nécessairement abouti.
 */
@Entity
@Table(name = "tentative_remise")
public class TentativeRemise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tentative")
    private Long id;

    @NotNull
    @PastOrPresent
    @Column(name = "date_tentative", nullable = false)
    private LocalDateTime dateTentative;

    @NotNull
    @Min(1)
    @Column(name = "quantite_concernee", nullable = false)
    private Integer quantiteConcernee;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "resultat", nullable = false, length = 50)
    private ResultatTentativeRemise resultat;

    @Column(name = "motif_echec", columnDefinition = "TEXT")
    private String motifEchec;

    @Size(max = 100)
    @Column(name = "nom_receptionnaire", length = 100)
    private String nomReceptionnaire;

    @Size(max = 20)
    @Column(name = "telephone_receptionnaire", length = 20)
    private String telephoneReceptionnaire;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_colis", nullable = false)
    private Colis colis;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_remise", nullable = true)
    private RemiseColis remise;

    public TentativeRemise() {
    }

    public TentativeRemise(LocalDateTime dateTentative, Integer quantiteConcernee,
                           ResultatTentativeRemise resultat, String motifEchec,
                           String nomReceptionnaire, String telephoneReceptionnaire,
                           Colis colis, Utilisateur utilisateur, RemiseColis remise) {
        this.dateTentative = dateTentative;
        this.quantiteConcernee = quantiteConcernee;
        this.resultat = resultat;
        this.motifEchec = motifEchec;
        this.nomReceptionnaire = nomReceptionnaire;
        this.telephoneReceptionnaire = telephoneReceptionnaire;
        this.colis = colis;
        this.utilisateur = utilisateur;
        this.remise = remise;
    }

    @AssertTrue(message = "Le motif d'échec est obligatoire lorsque le résultat est AUTRE")
    public boolean isMotifEchecValidePourAutre() {
        return resultat != ResultatTentativeRemise.AUTRE
                || (motifEchec != null && !motifEchec.trim().isEmpty());
    }

    @AssertTrue(message = "La remise associée doit concerner le même colis que la tentative")
    public boolean isRemiseCompatibleAvecColis() {
        if (remise == null)
            return true;
        if (colis == null || remise.getColis() == null)
            return false;
        if (colis.getId() != null && remise.getColis().getId() != null)
            return Objects.equals(colis.getId(), remise.getColis().getId());
        return colis == remise.getColis();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateTentative() {
        return dateTentative;
    }

    public void setDateTentative(LocalDateTime dateTentative) {
        this.dateTentative = dateTentative;
    }

    public Integer getQuantiteConcernee() {
        return quantiteConcernee;
    }

    public void setQuantiteConcernee(Integer quantiteConcernee) {
        this.quantiteConcernee = quantiteConcernee;
    }

    public ResultatTentativeRemise getResultat() {
        return resultat;
    }

    public void setResultat(ResultatTentativeRemise resultat) {
        this.resultat = resultat;
    }

    public String getMotifEchec() {
        return motifEchec;
    }

    public void setMotifEchec(String motifEchec) {
        this.motifEchec = motifEchec;
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

    public Colis getColis() {
        return colis;
    }

    public void setColis(Colis colis) {
        this.colis = colis;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public RemiseColis getRemise() {
        return remise;
    }

    public void setRemise(RemiseColis remise) {
        this.remise = remise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TentativeRemise tentative))
            return false;
        return Objects.equals(id, tentative.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TentativeRemise{id=" + id + ", dateTentative=" + dateTentative
                + ", quantiteConcernee=" + quantiteConcernee
                + ", resultat=" + resultat + "}";
    }
}
