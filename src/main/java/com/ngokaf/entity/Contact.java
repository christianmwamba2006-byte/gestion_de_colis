package com.ngokaf.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entité Contact.
 * <p>
 * Représente un expéditeur de colis. Un contact peut être l'expéditeur de
 * zéro, une ou plusieurs expéditions.
 * </p>
 */
@Entity
@Table(name = "contact")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contact")
    private Long id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "postnom", nullable = false, length = 100)
    private String postnom;

    @Column(name = "telephone", nullable = false, length = 20)
    private String telephone;

    @Column(name = "adresse", nullable = false, length = 255)
    private String adresse;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_lieu", nullable = true)
    private Lieu lieu;

    @OneToMany(mappedBy = "expediteur")
    private List<Expedition> expeditions = new ArrayList<>();

    public Contact() {
    }

    public Contact(String nom, String postnom, String telephone, String adresse,
                   Lieu lieu) {
        this.nom = nom;
        this.postnom = postnom;
        this.telephone = telephone;
        this.adresse = adresse;
        this.lieu = lieu;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public List<Expedition> getExpeditions() {
        return expeditions;
    }

    public void setExpeditions(List<Expedition> expeditions) {
        this.expeditions = expeditions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Contact contact))
            return false;
        return Objects.equals(id, contact.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Contact{id=" + id + ", nom='" + nom + "', postnom='" + postnom + "'}";
    }
}