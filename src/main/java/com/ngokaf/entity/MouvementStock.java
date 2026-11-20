package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
@Table(name = "mouvement_stock")
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mouvement")
    private Long id;

    @Column(name = "quantite", nullable = false)
    @Min(1)
    private Integer quantite;

    @Column(name = "date_mouvement", nullable = false)
    private LocalDateTime dateMouvement;

    @Column(name = "type_mouvement", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TypeMouvement typeMouvement;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_colis", nullable = false)
    private Colis colis;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_emplacement_depart", nullable = true)
    private Emplacement emplacementDepart;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_emplacement_arrivee", nullable = true)
    private Emplacement emplacementArrivee;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_operation", nullable = true)
    private OperationColis operation;

    public MouvementStock() {
    }

    public MouvementStock(Integer quantite, LocalDateTime dateMouvement,
                          TypeMouvement typeMouvement, String observations,
                          Colis colis, Emplacement emplacementDepart,
                          Emplacement emplacementArrivee, OperationColis operation) {
        this.quantite = quantite;
        this.dateMouvement = dateMouvement;
        this.typeMouvement = typeMouvement;
        this.observations = observations;
        this.colis = colis;
        this.emplacementDepart = emplacementDepart;
        this.emplacementArrivee = emplacementArrivee;
        this.operation = operation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public LocalDateTime getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(LocalDateTime dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public TypeMouvement getTypeMouvement() {
        return typeMouvement;
    }

    public void setTypeMouvement(TypeMouvement typeMouvement) {
        this.typeMouvement = typeMouvement;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Colis getColis() {
        return colis;
    }

    public void setColis(Colis colis) {
        this.colis = colis;
    }

    public Emplacement getEmplacementDepart() {
        return emplacementDepart;
    }

    public void setEmplacementDepart(Emplacement emplacementDepart) {
        this.emplacementDepart = emplacementDepart;
    }

    public Emplacement getEmplacementArrivee() {
        return emplacementArrivee;
    }

    public void setEmplacementArrivee(Emplacement emplacementArrivee) {
        this.emplacementArrivee = emplacementArrivee;
    }

    public OperationColis getOperation() {
        return operation;
    }

    public void setOperation(OperationColis operation) {
        this.operation = operation;
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
