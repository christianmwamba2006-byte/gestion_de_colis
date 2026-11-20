package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entité TransportArret.
 * <p>
 * Conserve une copie des arrêts de l'itinéraire au moment de la planification
 * du transport. Les modifications ultérieures de l'itinéraire ne modifient pas
 * les parcours déjà enregistrés dans cette entité.
 * </p>
 */
@Entity
@Table(name = "transport_arret",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_transport", "ordre_passage"}))
public class TransportArret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transport_arret")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_transport", nullable = false)
    private Transport transport;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_lieu", nullable = false)
    private Lieu lieu;

    @Column(name = "ordre_passage", nullable = false)
    @Min(1)
    private Integer ordrePassage;

    @Column(name = "heure_passage_prevue", nullable = false)
    private LocalDateTime heurePassagePrevue;

    @Column(name = "type_arret", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TypeArret typeArret;

    public TransportArret() {
    }

    public TransportArret(Transport transport, Lieu lieu, Integer ordrePassage,
                          LocalDateTime heurePassagePrevue, TypeArret typeArret) {
        this.transport = transport;
        this.lieu = lieu;
        this.ordrePassage = ordrePassage;
        this.heurePassagePrevue = heurePassagePrevue;
        this.typeArret = typeArret;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public Integer getOrdrePassage() {
        return ordrePassage;
    }

    public void setOrdrePassage(Integer ordrePassage) {
        this.ordrePassage = ordrePassage;
    }

    public LocalDateTime getHeurePassagePrevue() {
        return heurePassagePrevue;
    }

    public void setHeurePassagePrevue(LocalDateTime heurePassagePrevue) {
        this.heurePassagePrevue = heurePassagePrevue;
    }

    public TypeArret getTypeArret() {
        return typeArret;
    }

    public void setTypeArret(TypeArret typeArret) {
        this.typeArret = typeArret;
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
        return "TransportArret{id=" + id + ", ordrePassage=" + ordrePassage
                + ", heurePassagePrevue=" + heurePassagePrevue + "}";
    }
}