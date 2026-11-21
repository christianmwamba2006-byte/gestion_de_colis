package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Notification interne destinée à un utilisateur de l'application.
 */
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_notification", nullable = false, length = 50)
    private TypeNotification typeNotification;

    @NotBlank
    @Size(max = 2000)
    @Column(name = "contenu", nullable = false, length = 2000)
    private String contenu;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur destinataire;

    @NotNull
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutNotification statut = StatutNotification.NON_LUE;

    @Column(name = "date_lecture")
    private LocalDateTime dateLecture;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "canal", nullable = false, length = 20)
    private CanalNotification canal = CanalNotification.INTERNE;

    @NotBlank
    @Size(max = 255)
    @Column(name = "cle_idempotence", nullable = false, unique = true, length = 255)
    private String cleIdempotence;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_source", length = 20)
    private TypeSourceNotification typeSource;

    @Positive
    @Column(name = "id_source")
    private Long idSource;

    public Notification() {
    }

    public Notification(TypeNotification typeNotification, String contenu,
                        Utilisateur destinataire, String cleIdempotence,
                        TypeSourceNotification typeSource, Long idSource) {
        this.typeNotification = typeNotification;
        this.contenu = contenu;
        this.destinataire = destinataire;
        this.cleIdempotence = cleIdempotence;
        this.typeSource = typeSource;
        this.idSource = idSource;
    }

    @PrePersist
    private void initialiserValeursParDefaut() {
        if (dateCreation == null)
            dateCreation = LocalDateTime.now();
        if (statut == null)
            statut = StatutNotification.NON_LUE;
        if (canal == null)
            canal = CanalNotification.INTERNE;
    }

    @AssertTrue(message = "Le statut et la date de lecture doivent être cohérents")
    public boolean isLectureValide() {
        if (statut == null)
            return false;
        return statut == StatutNotification.NON_LUE
                ? dateLecture == null
                : dateLecture != null;
    }

    @AssertTrue(message = "Le type et l'identifiant de la source doivent être renseignés ensemble")
    public boolean isSourceValide() {
        return (typeSource == null) == (idSource == null);
    }

    public void marquerCommeLue() {
        if (statut == StatutNotification.NON_LUE) {
            dateLecture = LocalDateTime.now();
            statut = StatutNotification.LUE;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeNotification getTypeNotification() {
        return typeNotification;
    }

    public void setTypeNotification(TypeNotification typeNotification) {
        this.typeNotification = typeNotification;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Utilisateur getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(Utilisateur destinataire) {
        this.destinataire = destinataire;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public StatutNotification getStatut() {
        return statut;
    }

    public void setStatut(StatutNotification statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateLecture() {
        return dateLecture;
    }

    public void setDateLecture(LocalDateTime dateLecture) {
        this.dateLecture = dateLecture;
    }

    public CanalNotification getCanal() {
        return canal;
    }

    public void setCanal(CanalNotification canal) {
        this.canal = canal;
    }

    public String getCleIdempotence() {
        return cleIdempotence;
    }

    public void setCleIdempotence(String cleIdempotence) {
        this.cleIdempotence = cleIdempotence;
    }

    public TypeSourceNotification getTypeSource() {
        return typeSource;
    }

    public void setTypeSource(TypeSourceNotification typeSource) {
        this.typeSource = typeSource;
    }

    public Long getIdSource() {
        return idSource;
    }

    public void setIdSource(Long idSource) {
        this.idSource = idSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Notification that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Notification{id=" + id + ", typeNotification=" + typeNotification
                + ", statut=" + statut + "}";
    }
}
