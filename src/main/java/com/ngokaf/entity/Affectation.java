package com.ngokaf.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entit\u00e9 associative Affectation.
 * <p>
 * Lie un Utilisateur, un Role et optionnellement une Agence.
 * L'agence est facultative pour permettre les affectations globales (administrateur).
 * </p>
 */
@Entity
@Table(name = "affectation")
public class Affectation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_affectation")
    private Long id;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(name = "statut", nullable = false, length = 50)
    private String statut;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_role", nullable = false)
    private Role role;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_agence", nullable = true)
    private Agence agence;

    public Affectation() {
    }

    public Affectation(LocalDateTime dateDebut, LocalDateTime dateFin, String statut,
                       Utilisateur utilisateur, Role role, Agence agence) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.statut = statut;
        this.utilisateur = utilisateur;
        this.role = role;
        this.agence = agence;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Agence getAgence() {
        return agence;
    }

    public void setAgence(Agence agence) {
        this.agence = agence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Affectation that))
            return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Affectation{id=" + id + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin
                + ", statut='" + statut + "'}";
    }
}