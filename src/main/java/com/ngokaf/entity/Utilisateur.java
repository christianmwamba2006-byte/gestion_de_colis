package com.ngokaf.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité Utilisateur.
 * <p>
 * Le champ {@code motDePasse} doit stocker exclusivement un hachage BCrypt.
 * Aucun stockage en clair n'est autoris\u00e9 (r\u00e8gle de s\u00e9curit\u00e9 AGENTS.md §5).
 * </p>
 */
@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Long id;

    @Column(name = "nom", nullable = false, length = 50)
    private String nom;

    @Column(name = "postnom", nullable = false, length = 50)
    private String postnom;

    @Column(name = "prenom", nullable = false, length = 50)
    private String prenom;

    @Column(name = "telephone", nullable = false, length = 20)
    private String telephone;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "mot_de_passe", nullable = false, length = 255)
    private String motDePasse;

    @Column(name = "statut", nullable = false, length = 50)
    private String statut;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "utilisateur")
    private List<Affectation> affectations = new ArrayList<>();

    public Utilisateur() {
    }

    public Utilisateur(String nom, String postnom, String prenom, String telephone,
                       String email, String motDePasse, String statut, LocalDateTime dateCreation) {
        this.nom = nom;
        this.postnom = postnom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.motDePasse = motDePasse;
        this.statut = statut;
        this.dateCreation = dateCreation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPostnom() {
        return postnom;
    }

    public void setPostnom(String postnom) {
        this.postnom = postnom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<Affectation> getAffectations() {
        return affectations;
    }

    public void setAffectations(List<Affectation> affectations) {
        this.affectations = affectations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Utilisateur utilisateur))
            return false;
        return Objects.equals(id, utilisateur.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Utilisateur{id=" + id + ", nom='" + nom + "', postnom='" + postnom + "', prenom='"
                + prenom + "', email='" + email + "'}";
    }
}