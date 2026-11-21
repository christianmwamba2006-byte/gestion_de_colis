package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Incident signalé dans le cadre des opérations de Ngokaf.
 */
@Entity
@Table(name = "incident")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_incident")
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "reference_incident", nullable = false, length = 100, unique = true)
    private String referenceIncident;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_incident", nullable = false, length = 50)
    private TypeIncident typeIncident;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 30)
    private StatutIncident statut = StatutIncident.SIGNALE;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "priorite", nullable = false, length = 20)
    private PrioriteIncident priorite = PrioriteIncident.NORMALE;

    @NotBlank
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "solution_apportee", columnDefinition = "TEXT")
    private String solutionApportee;

    @NotNull
    @PastOrPresent
    @Column(name = "date_signalement", nullable = false)
    private LocalDateTime dateSignalement;

    @PastOrPresent
    @Column(name = "date_prise_en_charge")
    private LocalDateTime datePriseEnCharge;

    @PastOrPresent
    @Column(name = "date_resolution")
    private LocalDateTime dateResolution;

    @PastOrPresent
    @Column(name = "date_cloture")
    private LocalDateTime dateCloture;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_utilisateur_signaleur", nullable = false)
    private Utilisateur utilisateurSignaleur;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_utilisateur_pris_en_charge")
    private Utilisateur utilisateurPrisEnCharge;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_utilisateur_resolveur")
    private Utilisateur utilisateurResolveur;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_utilisateur_cloture")
    private Utilisateur utilisateurCloture;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_agence")
    private Agence agence;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_lieu")
    private Lieu lieu;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_transport")
    private Transport transport;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_operation")
    private OperationColis operationColis;

    @OneToMany(mappedBy = "incident")
    private List<IncidentColis> colisConcernes = new ArrayList<>();

    public Incident() {
    }

    public Incident(String referenceIncident, TypeIncident typeIncident, String description,
                    LocalDateTime dateSignalement, Utilisateur utilisateurSignaleur) {
        this.referenceIncident = referenceIncident;
        this.typeIncident = typeIncident;
        this.description = description;
        this.dateSignalement = dateSignalement;
        this.utilisateurSignaleur = utilisateurSignaleur;
    }

    @PrePersist
    private void initialiserValeursParDefaut() {
        if (statut == null)
            statut = StatutIncident.SIGNALE;
        if (priorite == null)
            priorite = PrioriteIncident.NORMALE;
    }

    @AssertTrue(message = "Les dates de l'incident doivent être chronologiquement cohérentes")
    public boolean isChronologieValide() {
        if ((datePriseEnCharge != null || dateResolution != null || dateCloture != null)
                && dateSignalement == null)
            return false;
        if (datePriseEnCharge != null && datePriseEnCharge.isBefore(dateSignalement))
            return false;
        if (dateResolution != null) {
            if (dateSignalement == null || dateResolution.isBefore(dateSignalement))
                return false;
            if (datePriseEnCharge != null && dateResolution.isBefore(datePriseEnCharge))
                return false;
        }
        if (dateCloture != null) {
            if (dateResolution == null || dateCloture.isBefore(dateResolution))
                return false;
        }
        return true;
    }

    @AssertTrue(message = "Une résolution exige une date, une solution et un responsable")
    public boolean isResolutionValide() {
        boolean resolutionDeclaree = dateResolution != null
                || statut == StatutIncident.RESOLU
                || statut == StatutIncident.CLOTURE;
        if (!resolutionDeclaree)
            return true;
        return dateResolution != null
                && solutionApportee != null
                && !solutionApportee.trim().isEmpty()
                && utilisateurResolveur != null;
    }

    @AssertTrue(message = "Une prise en charge exige une date et un premier responsable")
    public boolean isPriseEnChargeValide() {
        boolean priseEnChargeDeclaree = datePriseEnCharge != null
                || statut == StatutIncident.PRIS_EN_CHARGE
                || statut == StatutIncident.EN_COURS
                || statut == StatutIncident.RESOLU
                || statut == StatutIncident.CLOTURE;
        if (!priseEnChargeDeclaree)
            return true;
        return datePriseEnCharge != null && utilisateurPrisEnCharge != null;
    }

    @AssertTrue(message = "Une clôture exige une résolution préalable et un responsable de clôture")
    public boolean isClotureValide() {
        boolean clotureDeclaree = dateCloture != null || statut == StatutIncident.CLOTURE;
        if (!clotureDeclaree)
            return true;
        return statut == StatutIncident.CLOTURE
                && dateCloture != null
                && dateResolution != null
                && solutionApportee != null
                && !solutionApportee.trim().isEmpty()
                && utilisateurResolveur != null
                && utilisateurCloture != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferenceIncident() {
        return referenceIncident;
    }

    public void setReferenceIncident(String referenceIncident) {
        this.referenceIncident = referenceIncident;
    }

    public TypeIncident getTypeIncident() {
        return typeIncident;
    }

    public void setTypeIncident(TypeIncident typeIncident) {
        this.typeIncident = typeIncident;
    }

    public StatutIncident getStatut() {
        return statut;
    }

    public void setStatut(StatutIncident statut) {
        this.statut = statut;
    }

    public PrioriteIncident getPriorite() {
        return priorite;
    }

    public void setPriorite(PrioriteIncident priorite) {
        this.priorite = priorite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSolutionApportee() {
        return solutionApportee;
    }

    public void setSolutionApportee(String solutionApportee) {
        this.solutionApportee = solutionApportee;
    }

    public LocalDateTime getDateSignalement() {
        return dateSignalement;
    }

    public void setDateSignalement(LocalDateTime dateSignalement) {
        this.dateSignalement = dateSignalement;
    }

    public LocalDateTime getDatePriseEnCharge() {
        return datePriseEnCharge;
    }

    public void setDatePriseEnCharge(LocalDateTime datePriseEnCharge) {
        this.datePriseEnCharge = datePriseEnCharge;
    }

    public LocalDateTime getDateResolution() {
        return dateResolution;
    }

    public void setDateResolution(LocalDateTime dateResolution) {
        this.dateResolution = dateResolution;
    }

    public LocalDateTime getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(LocalDateTime dateCloture) {
        this.dateCloture = dateCloture;
    }

    public Utilisateur getUtilisateurSignaleur() {
        return utilisateurSignaleur;
    }

    public void setUtilisateurSignaleur(Utilisateur utilisateurSignaleur) {
        this.utilisateurSignaleur = utilisateurSignaleur;
    }

    public Utilisateur getUtilisateurPrisEnCharge() {
        return utilisateurPrisEnCharge;
    }

    public void setUtilisateurPrisEnCharge(Utilisateur utilisateurPrisEnCharge) {
        this.utilisateurPrisEnCharge = utilisateurPrisEnCharge;
    }

    public Utilisateur getUtilisateurResolveur() {
        return utilisateurResolveur;
    }

    public void setUtilisateurResolveur(Utilisateur utilisateurResolveur) {
        this.utilisateurResolveur = utilisateurResolveur;
    }

    public Utilisateur getUtilisateurCloture() {
        return utilisateurCloture;
    }

    public void setUtilisateurCloture(Utilisateur utilisateurCloture) {
        this.utilisateurCloture = utilisateurCloture;
    }

    public Agence getAgence() {
        return agence;
    }

    public void setAgence(Agence agence) {
        this.agence = agence;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public OperationColis getOperationColis() {
        return operationColis;
    }

    public void setOperationColis(OperationColis operationColis) {
        this.operationColis = operationColis;
    }

    public List<IncidentColis> getColisConcernes() {
        return colisConcernes;
    }

    public void setColisConcernes(List<IncidentColis> colisConcernes) {
        this.colisConcernes = colisConcernes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Incident incident))
            return false;
        return Objects.equals(id, incident.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Incident{id=" + id + ", referenceIncident='" + referenceIncident
                + "', statut=" + statut + "}";
    }
}
