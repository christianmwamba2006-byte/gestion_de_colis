package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Check;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité MouvementStock.
 * <p>
 * Retrace les entrées, sorties et déplacements physiques de colis
 * dans le stock d'une agence. Les mouvements sont conservés sans
 * écrasement ni suppression.
 * </p>
 */
@Entity
@Table(name = "mouvement_stock",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cle_idempotence"}))
@Check(constraints = "quantite > 0")
@Check(constraints = "(type_mouvement = 'ENTREE' and id_emplacement_depart is null "
        + "and id_emplacement_arrivee is not null) "
        + "or (type_mouvement = 'SORTIE' and id_emplacement_depart is not null "
        + "and id_emplacement_arrivee is null) "
        + "or (type_mouvement = 'DEPLACEMENT' and id_emplacement_depart is not null "
        + "and id_emplacement_arrivee is not null)")
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mouvement")
    private Long id;

    @NotNull
    @Positive
    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @NotNull
    @PastOrPresent
    @Column(name = "date_mouvement", nullable = false)
    private LocalDateTime dateMouvement;

    @NotNull
    @Column(name = "type_mouvement", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TypeMouvement typeMouvement;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_colis", nullable = false)
    private Colis colis;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    @NotNull
    private Utilisateur utilisateur;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_emplacement_depart", nullable = true)
    private Emplacement emplacementDepart;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_emplacement_arrivee", nullable = true)
    private Emplacement emplacementArrivee;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_operation", nullable = true)
    private OperationColis operation;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_remise", nullable = true)
    private RemiseColis remise;

    @NotBlank
    @Size(max = 100)
    @Column(name = "cle_idempotence", nullable = false, length = 100)
    private String cleIdempotence;

    public MouvementStock() {
    }

    public MouvementStock(Integer quantite, TypeMouvement typeMouvement,
                          String observations, Colis colis, Utilisateur utilisateur,
                          Emplacement emplacementDepart, Emplacement emplacementArrivee,
                          OperationColis operation, RemiseColis remise,
                          String cleIdempotence) {
        this.quantite = quantite;
        this.typeMouvement = typeMouvement;
        this.observations = observations;
        this.colis = colis;
        this.utilisateur = utilisateur;
        this.emplacementDepart = emplacementDepart;
        this.emplacementArrivee = emplacementArrivee;
        this.operation = operation;
        this.remise = remise;
        this.cleIdempotence = cleIdempotence;
    }

    @PrePersist
    private void definirDateMouvement() {
        dateMouvement = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public LocalDateTime getDateMouvement() {
        return dateMouvement;
    }

    public TypeMouvement getTypeMouvement() {
        return typeMouvement;
    }

    public String getObservations() {
        return observations;
    }

    public Colis getColis() {
        return colis;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public Emplacement getEmplacementDepart() {
        return emplacementDepart;
    }

    public Emplacement getEmplacementArrivee() {
        return emplacementArrivee;
    }

    public OperationColis getOperation() {
        return operation;
    }

    public RemiseColis getRemise() {
        return remise;
    }

    public String getCleIdempotence() {
        return cleIdempotence;
    }

    @AssertTrue(message = "Les emplacements doivent correspondre au type de mouvement.")
    public boolean isEmplacementsCoherentsAvecType() {
        if (typeMouvement == null) {
            return true;
        }
        return switch (typeMouvement) {
            case ENTREE -> emplacementDepart == null && emplacementArrivee != null;
            case SORTIE -> emplacementDepart != null && emplacementArrivee == null;
            case DEPLACEMENT -> emplacementDepart != null && emplacementArrivee != null;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof MouvementStock that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MouvementStock{id=" + id + ", quantite=" + quantite + ", dateMouvement=" + dateMouvement + ", typeMouvement=" + typeMouvement + "}";
    }
}
