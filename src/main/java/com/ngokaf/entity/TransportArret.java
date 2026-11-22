package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Check;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité TransportArret.
 * <p>
 * Conserve le parcours prévu et les passages réellement observés d'un transport.
 * </p>
 */
@Entity
@Table(name = "transport_arret",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_transport", "ordre_prevu"}),
                @UniqueConstraint(columnNames = {"id_transport", "ordre_reel"}),
                @UniqueConstraint(columnNames = {"cle_idempotence"})
        })
@Check(constraints = "ordre_prevu is null or ordre_prevu > 0")
@Check(constraints = "ordre_reel is null or ordre_reel > 0")
public class TransportArret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transport_arret")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_transport", nullable = false)
    @NotNull
    private Transport transport;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_lieu", nullable = false)
    @NotNull
    private Lieu lieu;

    @Column(name = "ordre_prevu")
    @Min(1)
    private Integer ordrePrevu;

    @Column(name = "ordre_reel")
    @Min(1)
    private Integer ordreReel;

    @Column(name = "est_prevu", nullable = false)
    private boolean estPrevu;

    @Column(name = "date_prevue")
    private LocalDateTime datePrevue;

    @Column(name = "heure_indicative")
    private LocalTime heureIndicative;

    @Column(name = "date_reelle")
    private LocalDateTime dateReelle;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Column(name = "cle_idempotence", length = 100, unique = true)
    @Size(max = 100)
    private String cleIdempotence;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_arret", nullable = false, length = 20)
    @NotNull
    private TypeArret typeArret;

    public TransportArret() {
    }

    public TransportArret(Transport transport, Lieu lieu, Integer ordrePrevu,
                          TypeArret typeArret, LocalDateTime datePrevue,
                          LocalTime heureIndicative, String observations) {
        this.transport = transport;
        this.lieu = lieu;
        this.ordrePrevu = ordrePrevu;
        this.estPrevu = true;
        this.datePrevue = datePrevue;
        this.heureIndicative = heureIndicative;
        this.observations = observations;
        this.typeArret = typeArret;
    }

    public static TransportArret arretHorsItineraire(
            Transport transport, Lieu lieu, Integer ordreReel,
            LocalDateTime dateReelle, String cleIdempotence) {
        TransportArret arret = new TransportArret();
        arret.transport = transport;
        arret.lieu = lieu;
        arret.estPrevu = false;
        arret.ordreReel = ordreReel;
        arret.dateReelle = dateReelle;
        arret.cleIdempotence = cleIdempotence;
        arret.typeArret = TypeArret.INTERMEDIAIRE;
        return arret;
    }

    public Long getId() {
        return id;
    }

    public Transport getTransport() {
        return transport;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public Integer getOrdrePrevu() {
        return ordrePrevu;
    }

    public Integer getOrdreReel() {
        return ordreReel;
    }

    public boolean isEstPrevu() {
        return estPrevu;
    }

    public LocalDateTime getDatePrevue() {
        return datePrevue;
    }

    public LocalTime getHeureIndicative() {
        return heureIndicative;
    }

    public LocalDateTime getDateReelle() {
        return dateReelle;
    }

    public String getObservations() {
        return observations;
    }

    public String getCleIdempotence() {
        return cleIdempotence;
    }

    public void enregistrerPassage(int ordre, LocalDateTime date) {
        if (!estPrevu || ordrePrevu == null || ordreReel != null || dateReelle != null) {
            throw new IllegalStateException("Le passage de cet arrêt ne peut pas être modifié.");
        }
        this.ordreReel = ordre;
        this.dateReelle = Objects.requireNonNull(date);
    }

    public TypeArret getTypeArret() {
        return typeArret;
    }

    @AssertTrue(message = "Les ordres et dates de l'arrêt ne sont pas cohérents.")
    public boolean isOrdresEtDatesCoherents() {
        if (estPrevu) {
            return ordrePrevu != null
                    && ((ordreReel == null && dateReelle == null)
                    || (ordreReel != null && dateReelle != null));
        }
        return ordrePrevu == null && datePrevue == null
                && ordreReel != null && dateReelle != null
                && typeArret == TypeArret.INTERMEDIAIRE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TransportArret that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TransportArret{id=" + id + ", ordrePrevu=" + ordrePrevu
                + ", ordreReel=" + ordreReel + ", estPrevu=" + estPrevu + "}";
    }
}