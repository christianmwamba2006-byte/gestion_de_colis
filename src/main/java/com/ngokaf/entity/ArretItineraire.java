package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Entité ArretItineraire.
 * <p>
 * Représente un arrêt dans un itinéraire. L'arrêt est un lieu (pas nécessairement
 * une agence). L'ordre des arrêts doit être unique dans un même itinéraire.
 * </p>
 */
@Entity
@Table(name = "arret_itineraire",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_itineraire", "ordre_arret"}))
public class ArretItineraire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_arret")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_itineraire", nullable = false)
    private Itineraire itineraire;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_lieu", nullable = false)
    private Lieu lieu;

    @Column(name = "ordre_arret", nullable = false)
    @Min(1)
    private Integer ordreArret;

    @Column(name = "heure_indicative")
    private LocalTime heureIndicative;

    @Column(name = "type_arret", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TypeArret typeArret;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    public ArretItineraire() {
    }

    public ArretItineraire(Itineraire itineraire, Lieu lieu, Integer ordreArret,
                           LocalTime heureIndicative, TypeArret typeArret,
                           String observations) {
        this.itineraire = itineraire;
        this.lieu = lieu;
        this.ordreArret = ordreArret;
        this.heureIndicative = heureIndicative;
        this.typeArret = typeArret;
        this.observations = observations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Itineraire getItineraire() {
        return itineraire;
    }

    public void setItineraire(Itineraire itineraire) {
        this.itineraire = itineraire;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public Integer getOrdreArret() {
        return ordreArret;
    }

    public void setOrdreArret(Integer ordreArret) {
        this.ordreArret = ordreArret;
    }

    public LocalTime getHeureIndicative() {
        return heureIndicative;
    }

    public void setHeureIndicative(LocalTime heureIndicative) {
        this.heureIndicative = heureIndicative;
    }

    public TypeArret getTypeArret() {
        return typeArret;
    }

    public void setTypeArret(TypeArret typeArret) {
        this.typeArret = typeArret;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ArretItineraire that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ArretItineraire{id=" + id + ", ordreArret=" + ordreArret
                + ", typeArret=" + typeArret + "}";
    }
}