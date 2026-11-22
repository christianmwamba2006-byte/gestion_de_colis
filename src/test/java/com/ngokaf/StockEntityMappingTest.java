package com.ngokaf;

import com.ngokaf.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StockEntityMappingTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private Validator validator;

    @Test
    void enregistreLesEntitesStockEtConserveLesEnums() {
        Set<String> entityNames = entityManagerFactory.getMetamodel().getEntities()
                .stream()
                .map(jakarta.persistence.metamodel.EntityType::getName)
                .collect(Collectors.toSet());

        assertThat(entityNames).contains("Emplacement", "MouvementStock");
        assertThat(StatutEmplacement.values())
                .containsExactly(StatutEmplacement.ACTIF, StatutEmplacement.INACTIF);
        assertThat(TypeMouvement.values()).containsExactly(
                TypeMouvement.ENTREE, TypeMouvement.SORTIE, TypeMouvement.DEPLACEMENT);
    }

    @Test
    void valideLesChampsEtLUniciteAgenceCodeEmplacement() throws NoSuchFieldException {
        Emplacement invalide = new Emplacement(
                "C".repeat(51), "", null, null, null);
        Set<String> invalidProperties = validator.validate(invalide).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertThat(invalidProperties)
                .contains("codeEmplacement", "libelle", "statut", "agence");
        assertThat(column(Emplacement.class, "codeEmplacement").length()).isEqualTo(50);
        assertThat(column(Emplacement.class, "libelle").length()).isEqualTo(100);
        assertThat(tableHasUniqueConstraint(
                Emplacement.class, "id_agence", "code_emplacement")).isTrue();

        Agence agenceUne = creerAgence();
        Agence agenceDeux = creerAgence();
        entityManager.persist(new Emplacement(
                "R-01", "Rayon", null, StatutEmplacement.ACTIF, agenceUne));
        entityManager.persist(new Emplacement(
                "R-01", "Rayon", null, StatutEmplacement.ACTIF, agenceDeux));
        entityManager.flush();
    }

    @Test
    void rejetteLeCodeDupliqueDansUneMemeAgence() {
        Agence agence = creerAgence();
        entityManager.persist(new Emplacement(
                "R-01", "Premier", null, StatutEmplacement.ACTIF, agence));
        entityManager.flush();

        assertThatThrownBy(() -> {
            entityManager.persist(new Emplacement(
                    "R-01", "Second", null, StatutEmplacement.INACTIF, agence));
            entityManager.flush();
        }).isInstanceOf(PersistenceException.class);
    }

    @Test
    void imposeLesRelationsEtLesEmplacementsSelonLeTypeDeMouvement() throws Exception {
        Field utilisateurField = MouvementStock.class.getDeclaredField("utilisateur");
        assertThat(utilisateurField.getAnnotation(jakarta.persistence.ManyToOne.class).optional())
                .isFalse();
        assertThat(utilisateurField.getAnnotation(jakarta.persistence.JoinColumn.class).nullable())
                .isFalse();

        assertOptionalRelation("emplacementDepart", "id_emplacement_depart");
        assertOptionalRelation("emplacementArrivee", "id_emplacement_arrivee");
        assertOptionalRelation("operation", "id_operation");
        assertOptionalRelation("remise", "id_remise");

        assertThat(column(MouvementStock.class, "cleIdempotence").nullable()).isFalse();
        assertThat(column(MouvementStock.class, "cleIdempotence").length()).isEqualTo(100);
        assertThat(tableHasUniqueConstraint(MouvementStock.class, "cle_idempotence")).isTrue();
        assertThat(Arrays.stream(MouvementStock.class.getMethods())
                .noneMatch(method -> method.getName().startsWith("set"))).isTrue();
        assertThat(violations(movement(
                0, TypeMouvement.ENTREE, null, null, null,
                null, null, null, null, " ")))
                .contains("quantite", "utilisateur", "cleIdempotence");

        Emplacement emplacement = new Emplacement();
        MouvementStock entreeInvalide = movement(
                TypeMouvement.ENTREE, emplacement, null);
        MouvementStock sortieInvalide = movement(
                TypeMouvement.SORTIE, null, emplacement);
        MouvementStock deplacementInvalide = movement(
                TypeMouvement.DEPLACEMENT, null, emplacement);

        assertThat(violations(entreeInvalide)).contains("emplacementsCoherentsAvecType");
        assertThat(violations(sortieInvalide)).contains("emplacementsCoherentsAvecType");
        assertThat(violations(deplacementInvalide)).contains("emplacementsCoherentsAvecType");
        assertThat(violations(movement(TypeMouvement.ENTREE, null, emplacement)))
                .doesNotContain("emplacementsCoherentsAvecType");
        assertThat(violations(movement(TypeMouvement.SORTIE, emplacement, null)))
                .doesNotContain("emplacementsCoherentsAvecType");
        assertThat(violations(movement(TypeMouvement.DEPLACEMENT, emplacement, emplacement)))
                .doesNotContain("emplacementsCoherentsAvecType");
    }

    @Test
    void dateServeurEtCleIdempotenceUniqueSontMappees() {
        String suffix = UUID.randomUUID().toString();
        Lieu lieu = new Lieu("Lieu-" + suffix, "VILLE", null);
        entityManager.persist(lieu);
        Agence agence = new Agence(
                "Agence-" + suffix, "Adresse", "0990000001", "ACTIVE", lieu);
        entityManager.persist(agence);
        Emplacement emplacement = new Emplacement(
                "R-01", "Rayon", null, StatutEmplacement.ACTIF, agence);
        entityManager.persist(emplacement);

        Contact contact = new Contact("Nom", "Postnom", "0990000002", "Adresse", lieu);
        entityManager.persist(contact);
        Utilisateur utilisateur = new Utilisateur(
                "Nom", "Postnom", "Prenom", "0990000003",
                suffix + "@example.test", "hash-test", "ACTIVE", LocalDateTime.now());
        entityManager.persist(utilisateur);
        TypeColis typeColis = new TypeColis("Carton-" + suffix, null, "ACTIVE", null);
        entityManager.persist(typeColis);
        Expedition expedition = new Expedition(
                BigDecimal.ZERO, null, contact, agence, utilisateur);
        entityManager.persist(expedition);
        Colis colis = new Colis(
                "COLIS-" + suffix, "Carton", 2, null, "ENREGISTRE",
                LocalDateTime.now(), null, typeColis, expedition, lieu, contact, null);
        entityManager.persist(colis);
        entityManager.flush();

        String cle = UUID.randomUUID().toString();
        MouvementStock mouvement = movement(
                1, TypeMouvement.ENTREE, null, colis, utilisateur,
                null, emplacement, null, null, cle);
        entityManager.persist(mouvement);
        entityManager.flush();

        assertThat(mouvement.getDateMouvement()).isNotNull();
        assertThat(mouvement.getDateMouvement()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(mouvement.getUtilisateur()).isEqualTo(utilisateur);
        assertThat(mouvement.getOperation()).isNull();
        assertThat(mouvement.getRemise()).isNull();

        assertThatThrownBy(() -> {
            entityManager.persist(movement(
                    1, TypeMouvement.ENTREE, null, colis, utilisateur,
                    null, emplacement, null, null, cle));
            entityManager.flush();
        }).isInstanceOf(PersistenceException.class);
    }

    private Agence creerAgence() {
        String suffix = UUID.randomUUID().toString();
        Lieu lieu = new Lieu("Lieu-" + suffix, "VILLE", null);
        entityManager.persist(lieu);
        Agence agence = new Agence(
                "Agence-" + suffix, "Adresse", "0990000001", "ACTIVE", lieu);
        entityManager.persist(agence);
        return agence;
    }

    private Set<String> violations(MouvementStock mouvement) {
        return validator.validate(mouvement).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    private MouvementStock movement(
            TypeMouvement type, Emplacement depart, Emplacement arrivee) {
        return movement(1, type, null, null, null, depart, arrivee, null, null, "test-key");
    }

    private MouvementStock movement(
            Integer quantite, TypeMouvement type, String observations, Colis colis,
            Utilisateur utilisateur, Emplacement depart, Emplacement arrivee,
            OperationColis operation, RemiseColis remise, String cleIdempotence) {
        return new MouvementStock(quantite, type, observations, colis, utilisateur,
                depart, arrivee, operation, remise, cleIdempotence);
    }

    private static jakarta.persistence.Column column(Class<?> type, String fieldName)
            throws NoSuchFieldException {
        return type.getDeclaredField(fieldName).getAnnotation(jakarta.persistence.Column.class);
    }

    private static void assertOptionalRelation(String fieldName, String joinColumnName)
            throws NoSuchFieldException {
        Field field = MouvementStock.class.getDeclaredField(fieldName);
        assertThat(field.getAnnotation(jakarta.persistence.ManyToOne.class).optional()).isTrue();
        assertThat(field.getAnnotation(jakarta.persistence.JoinColumn.class).name())
                .isEqualTo(joinColumnName);
        assertThat(field.getAnnotation(jakarta.persistence.JoinColumn.class).nullable()).isTrue();
    }

    private static boolean tableHasUniqueConstraint(Class<?> type, String... columns) {
        return Arrays.stream(type.getAnnotation(Table.class).uniqueConstraints())
                .map(UniqueConstraint::columnNames)
                .anyMatch(names -> Arrays.equals(names, columns));
    }
}
