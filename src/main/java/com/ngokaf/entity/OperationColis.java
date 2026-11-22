package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.Check;
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
@Table(name = "operation_colis",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cle_idempotence"}))
@Check(constraints = "quantite_concernee > 0")
@Check(constraints = "cloture_participation = false or type_operation = 'DECHARGEMENT'")
public class OperationColis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_operation")
    private Long id;

    @Column(name = "type_operation", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @NotNull
    private TypeOperation typeOperation;

    @Column(name = "date_operation", nullable = false)
    @NotNull
    private LocalDateTime dateOperation;

    @Column(name = "quantite_concernee", nullable = false)
    @Positive
    @NotNull
    private Integer quantiteConcernee;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_colis_transport", nullable = false)
    @NotNull
    private ColisTransport colisTransport;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    @NotNull
    private Utilisateur utilisateur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_transport_arret", nullable = false)
    @NotNull
    private TransportArret transportArret;

    @Column(name = "cloture_participation", nullable = false)
    private boolean clotureParticipation;

    @Column(name = "cle_idempotence", length = 100, unique = true)
    private String cleIdempotence;

    public OperationColis() {
    }

    public OperationColis(TypeOperation typeOperation, Integer quantiteConcernee,
                          String observations, ColisTransport colisTransport,
                          Utilisateur utilisateur, TransportArret transportArret,
                          boolean clotureParticipation, String cleIdempotence) {
        this.typeOperation = typeOperation;
        this.quantiteConcernee = quantiteConcernee;
        this.observations = observations;
        this.colisTransport = colisTransport;
        this.utilisateur = utilisateur;
        this.transportArret = transportArret;
        this.clotureParticipation = clotureParticipation;
        this.cleIdempotence = cleIdempotence;
    }

    @PrePersist
    private void definirDateOperation() {
        dateOperation = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public TypeOperation getTypeOperation() {
        return typeOperation;
    }

    public LocalDateTime getDateOperation() {
        return dateOperation;
    }

    public Integer getQuantiteConcernee() {
        return quantiteConcernee;
    }

    public String getObservations() {
        return observations;
    }

    public ColisTransport getColisTransport() {
        return colisTransport;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public TransportArret getTransportArret() {
        return transportArret;
    }

    public boolean isClotureParticipation() {
        return clotureParticipation;
    }

    public String getCleIdempotence() {
        return cleIdempotence;
    }

    @AssertTrue(message = "Seul un déchargement peut clôturer une participation.")
    public boolean isClotureCoherente() {
        return !clotureParticipation || typeOperation == TypeOperation.DECHARGEMENT;
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