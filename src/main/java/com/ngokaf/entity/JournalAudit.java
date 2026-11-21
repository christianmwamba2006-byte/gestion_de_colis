package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Trace append-only d'une action effectuée dans l'application.
 */
@Entity
@Table(name = "journal_audit")
public class JournalAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_journal")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_action", nullable = false, length = 50)
    private TypeActionAudit typeAction;

    @NotBlank
    @Size(max = 100)
    @Column(name = "entite_cible", nullable = false, length = 100)
    private String entiteCible;

    @NotNull
    @Positive
    @Column(name = "identifiant_cible", nullable = false)
    private Long identifiantCible;

    @Column(name = "ancienne_valeur", columnDefinition = "TEXT")
    private String ancienneValeur;

    @Column(name = "nouvelle_valeur", columnDefinition = "TEXT")
    private String nouvelleValeur;

    @NotNull
    @PastOrPresent
    @Column(name = "date_action", nullable = false)
    private LocalDateTime dateAction;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    public JournalAudit() {
    }

    public JournalAudit(TypeActionAudit typeAction, String entiteCible,
                        Long identifiantCible, String ancienneValeur,
                        String nouvelleValeur, Utilisateur utilisateur) {
        this.typeAction = typeAction;
        this.entiteCible = entiteCible;
        this.identifiantCible = identifiantCible;
        this.ancienneValeur = ancienneValeur;
        this.nouvelleValeur = nouvelleValeur;
        this.utilisateur = utilisateur;
    }

    @PrePersist
    private void definirDateAction() {
        if (dateAction == null)
            dateAction = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public TypeActionAudit getTypeAction() {
        return typeAction;
    }

    public String getEntiteCible() {
        return entiteCible;
    }

    public Long getIdentifiantCible() {
        return identifiantCible;
    }

    public String getAncienneValeur() {
        return ancienneValeur;
    }

    public String getNouvelleValeur() {
        return nouvelleValeur;
    }

    public LocalDateTime getDateAction() {
        return dateAction;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof JournalAudit that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "JournalAudit{id=" + id + ", typeAction=" + typeAction
                + ", entiteCible='" + entiteCible + "', identifiantCible="
                + identifiantCible + "}";
    }
}
