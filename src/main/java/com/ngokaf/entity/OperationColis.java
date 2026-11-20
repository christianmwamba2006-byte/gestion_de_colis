package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité OperationColis.
 * <p>
 * Historique des opérations réellement effectuées sur un colis pendant son
 * parcours. Les opérations sont conservées sans écrasement ni suppression.
 * </p>
 */
@Entity
@Table(name = "operation_colis")
public class OperationColis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_operation")
    private Long id;

    @Column(name = "type_operation", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TypeOperation typeOperation;

    @Column(name = "date_operation", nullable = false)
    private LocalDateTime dateOperation;

    @Column(name = "quantite_concernee", nullable = false)
    @Min(1)
    private Integer quantiteConcernee;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_colis", nullable = false)
    private Colis colis;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_colis_transport", nullable = true)
    private ColisTransport colisTransport;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_lieu", nullable = false)
    private Lieu lieu;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_transport_arret", nullable = true)
    private TransportArret transportArret;

    public OperationColis() {
    }

    public OperationColis(TypeOperation typeOperation, LocalDateTime dateOperation,
                          Integer quantiteConcernee, String observations, Colis colis,
                          ColisTransport colisTransport, Utilisateur utilisateur,
                          Lieu lieu, TransportArret transportArret) {
        this.typeOperation = typeOperation;
        this.dateOperation = dateOperation;
        this.quantiteConcernee = quantiteConcernee;
        this.observations = observations;
        this.colis = colis;
        this.colisTransport = colisTransport;
        this.utilisateur = utilisateur;
        this.lieu = lieu;
        this.transportArret = transportArret;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeOperation getTypeOperation() {
        return typeOperation;
    }

    public void setTypeOperation(TypeOperation typeOperation) {
        this.typeOperation = typeOperation;
    }

    public LocalDateTime getDateOperation() {
        return dateOperation;
    }

    public void setDateOperation(LocalDateTime dateOperation) {
        this.dateOperation = dateOperation;
    }

    public Integer getQuantiteConcernee() {
        return quantiteConcernee;
    }

    public void setQuantiteConcernee(Integer quantiteConcernee) {
        this.quantiteConcernee = quantiteConcernee;
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

    public ColisTransport getColisTransport() {
        return colisTransport;
    }

    public void setColisTransport(ColisTransport colisTransport) {
        this.colisTransport = colisTransport;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public TransportArret getTransportArret() {
        return transportArret;
    }

    public void setTransportArret(TransportArret transportArret) {
        this.transportArret = transportArret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof OperationColis that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OperationColis{id=" + id + ", typeOperation=" + typeOperation
                + ", dateOperation=" + dateOperation + "}";
    }
}