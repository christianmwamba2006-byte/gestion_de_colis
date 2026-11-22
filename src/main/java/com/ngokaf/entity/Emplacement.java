package com.ngokaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;

/**
 * Entité Emplacement.
 * <p>
 * Un emplacement appartient obligatoirement à une seule agence.
 * Le couple (id_agence, code_emplacement) est unique.
 * </p>
 */
@Entity
@Table(name = "emplacement",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_agence", "code_emplacement"})
)
public class Emplacement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_emplacement")
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "code_emplacement", nullable = false, length = 50)
    private String codeEmplacement;

    @NotBlank
    @Size(max = 100)
    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "statut", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StatutEmplacement statut;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_agence", nullable = false)
    private Agence agence;

    public Emplacement() {
    }

    public Emplacement(String codeEmplacement, String libelle, String description,
                       StatutEmplacement statut, Agence agence) {
        this.codeEmplacement = codeEmplacement;
        this.libelle = libelle;
        this.description = description;
        this.statut = statut;
        this.agence = agence;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeEmplacement() {
        return codeEmplacement;
    }

    public void setCodeEmplacement(String codeEmplacement) {
        this.codeEmplacement = codeEmplacement;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatutEmplacement getStatut() {
        return statut;
    }

    public void setStatut(StatutEmplacement statut) {
        this.statut = statut;
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
        if (!(o instanceof Emplacement emplacement))
            return false;
        return Objects.equals(id, emplacement.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Emplacement{id=" + id + ", codeEmplacement='" + codeEmplacement + "', libelle='" + libelle + "', statut=" + statut + "}";
    }
}
