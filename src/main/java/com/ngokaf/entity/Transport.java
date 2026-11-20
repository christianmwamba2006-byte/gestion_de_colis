package com.ngokaf.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité Transport.
 * <p>
 * Un transport correspond à un voyage réel effectué par un seul bus.
 * Il suit un itinéraire et possède ses propres agences de départ et d'arrivée,
 * initialisées à partir de l'itinéraire à la création et figées par la suite.
 * </p>
 */
@Entity
@Table(name = "transport")
public class Transport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transport")
    private Long id;

    @Column(name = "reference_transport", nullable = false, length = 100, unique = true)
    private String referenceTransport;

    @Column(name = "date_depart_prevue")
    private LocalDateTime dateDepartPrevue;

    @Column(name = "date_depart_reelle")
    private LocalDateTime dateDepartReelle;

    @Column(name = "date_arrivee_prevue")
    private LocalDateTime dateArriveePrevue;

    @Column(name = "date_arrivee_reelle")
    private LocalDateTime dateArriveeReelle;

    @Column(name = "statut", nullable = false, length = 50)
    private String statut;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_bus", nullable = false)
    private Bus bus;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_itineraire", nullable = false)
    private Itineraire itineraire;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_agence_depart", nullable = false)
    private Agence agenceDepart;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_agence_finale", nullable = false)
    private Agence agenceFinale;

    @OneToMany(mappedBy = "transport")
    private List<TransportArret> arrets = new ArrayList<>();

    public Transport() {
    }

    public Transport(String referenceTransport, LocalDateTime dateDepartPrevue,
                     LocalDateTime dateDepartReelle, LocalDateTime dateArriveePrevue,
                     LocalDateTime dateArriveeReelle, String statut, String observations,
                     Bus bus, Itineraire itineraire, Agence agenceDepart,
                     Agence agenceFinale) {
        this.referenceTransport = referenceTransport;
        this.dateDepartPrevue = dateDepartPrevue;
        this.dateDepartReelle = dateDepartReelle;
        this.dateArriveePrevue = dateArriveePrevue;
        this.dateArriveeReelle = dateArriveeReelle;
        this.statut = statut;
        this.observations = observations;
        this.bus = bus;
        this.itineraire = itineraire;
        this.agenceDepart = agenceDepart;
        this.agenceFinale = agenceFinale;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceTransport() {
        return referenceTransport;
    }

    public void setReferenceTransport(String referenceTransport) {
        this.referenceTransport = referenceTransport;
    }

    public LocalDateTime getDateDepartPrevue() {
        return dateDepartPrevue;
    }

    public void setDateDepartPrevue(LocalDateTime dateDepartPrevue) {
        this.dateDepartPrevue = dateDepartPrevue;
    }

    public LocalDateTime getDateDepartReelle() {
        return dateDepartReelle;
    }

    public void setDateDepartReelle(LocalDateTime dateDepartReelle) {
        this.dateDepartReelle = dateDepartReelle;
    }

    public LocalDateTime getDateArriveePrevue() {
        return dateArriveePrevue;
    }

    public void setDateArriveePrevue(LocalDateTime dateArriveePrevue) {
        this.dateArriveePrevue = dateArriveePrevue;
    }

    public LocalDateTime getDateArriveeReelle() {
        return dateArriveeReelle;
    }

    public void setDateArriveeReelle(LocalDateTime dateArriveeReelle) {
        this.dateArriveeReelle = dateArriveeReelle;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public Itineraire getItineraire() {
        return itineraire;
    }

    public void setItineraire(Itineraire itineraire) {
        this.itineraire = itineraire;
    }

    public Agence getAgenceDepart() {
        return agenceDepart;
    }

    public void setAgenceDepart(Agence agenceDepart) {
        this.agenceDepart = agenceDepart;
    }

    public Agence getAgenceFinale() {
        return agenceFinale;
    }

    public void setAgenceFinale(Agence agenceFinale) {
        this.agenceFinale = agenceFinale;
    }

    public List<TransportArret> getArrets() {
        return arrets;
    }

    public void setArrets(List<TransportArret> arrets) {
        this.arrets = arrets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Transport transport))
            return false;
        return Objects.equals(id, transport.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transport{id=" + id + ", referenceTransport='" + referenceTransport + "'}";
    }
}