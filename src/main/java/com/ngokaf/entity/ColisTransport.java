package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité ColisTransport.
 * <p>
 * Association entre un colis et un transport précis. Un colis peut participer
 * à plusieurs transports successifs.
 * </p>
 */
@Entity
@Table(name = "colis_transport",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_colis", "id_transport"}))
public class ColisTransport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_colis_transport")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_colis", nullable = false)
    private Colis colis;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_transport", nullable = false)
    private Transport transport;

    @Column(name = "quantite_prevue", nullable = false)
    @Min(1)
    private Integer quantitePrevue;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @OneToMany(mappedBy = "colisTransport")
    private List<OperationColis> operations = new ArrayList<>();

    public ColisTransport() {
    }

    public ColisTransport(Colis colis, Transport transport, Integer quantitePrevue,
                          String observations) {
        this.colis = colis;
        this.transport = transport;
        this.quantitePrevue = quantitePrevue;
        this.observations = observations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Colis getColis() {
        return colis;
    }

    public void setColis(Colis colis) {
        this.colis = colis;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public Integer getQuantitePrevue() {
        return quantitePrevue;
    }

    public void setQuantitePrevue(Integer quantitePrevue) {
        this.quantitePrevue = quantitePrevue;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public List<OperationColis> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationColis> operations) {
        this.operations = operations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ColisTransport that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ColisTransport{id=" + id + ", colis=" + colis.getId()
                + ", transport=" + transport.getId() + "}";
    }
}