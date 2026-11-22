package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.Check;
import java.time.LocalDateTime;
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
@Check(constraints = "quantite_prevue > 0")
public class ColisTransport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_colis_transport")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_colis", nullable = false)
    @NotNull
    private Colis colis;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_transport", nullable = false)
    @NotNull
    private Transport transport;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_arret_prevu", nullable = false)
    @NotNull
    private TransportArret arretPrevu;

    @ManyToOne
    @JoinColumn(name = "id_arret_reel")
    private TransportArret arretReel;

    @Column(name = "quantite_prevue", nullable = false)
    @Positive
    @NotNull
    private Integer quantitePrevue;

    @Column(name = "date_embarquement")
    private LocalDateTime dateEmbarquement;

    @Column(name = "date_dechargement")
    private LocalDateTime dateDechargement;

    @Column(name = "est_terminee", nullable = false)
    private boolean terminee;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @OneToMany(mappedBy = "colisTransport")
    private List<OperationColis> operations = new ArrayList<>();

    public ColisTransport() {
    }

    public ColisTransport(Colis colis, Transport transport, TransportArret arretPrevu,
                          Integer quantitePrevue, String observations) {
        this.colis = colis;
        this.transport = transport;
        this.arretPrevu = arretPrevu;
        this.quantitePrevue = quantitePrevue;
        this.observations = observations;
    }

    public Long getId() {
        return id;
    }

    public Colis getColis() {
        return colis;
    }

    public Transport getTransport() {
        return transport;
    }

    public TransportArret getArretPrevu() {
        return arretPrevu;
    }

    public TransportArret getArretReel() {
        return arretReel;
    }

    public Integer getQuantitePrevue() {
        return quantitePrevue;
    }

    public LocalDateTime getDateEmbarquement() {
        return dateEmbarquement;
    }

    public LocalDateTime getDateDechargement() {
        return dateDechargement;
    }

    public boolean isTerminee() {
        return terminee;
    }

    public void enregistrerEmbarquement(LocalDateTime date) {
        if (terminee) {
            throw new IllegalStateException("Une participation terminée ne peut pas être rouverte.");
        }
        this.dateEmbarquement = date;
    }

    public void enregistrerDechargement(LocalDateTime date, boolean definitif,
                                        TransportArret arret) {
        if (terminee) {
            throw new IllegalStateException("Une participation terminée ne peut pas être modifiée.");
        }
        this.dateDechargement = date;
        if (definitif) {
            this.terminee = true;
            this.arretReel = arret;
        }
    }

    public String getObservations() {
        return observations;
    }

    public List<OperationColis> getOperations() {
        return operations;
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