package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Association entre un incident et un colis concerné.
 */
@Entity
@Table(name = "incident_colis",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_incident", "id_colis"}))
public class IncidentColis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_incident_colis")
    private Long id;

    @NotNull
    @Min(1)
    @Column(name = "quantite_concernee", nullable = false)
    private Integer quantiteConcernee;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_incident", nullable = false)
    private Incident incident;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_colis", nullable = false)
    private Colis colis;

    public IncidentColis() {
    }

    public IncidentColis(Integer quantiteConcernee, String observations,
                         Incident incident, Colis colis) {
        this.quantiteConcernee = quantiteConcernee;
        this.observations = observations;
        this.incident = incident;
        this.colis = colis;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

    public Colis getColis() {
        return colis;
    }

    public void setColis(Colis colis) {
        this.colis = colis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof IncidentColis that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "IncidentColis{id=" + id + ", quantiteConcernee=" + quantiteConcernee + "}";
    }
}
