package com.ngokaf;

import com.ngokaf.entity.*;
import com.ngokaf.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StockRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EmplacementRepository emplacementRepository;

    @Autowired
    private MouvementStockRepository mouvementStockRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void consulteLesEmplacementsParAgenceStatutEtCode() {
        Fixture fixture = creerFixture();

        assertThat(emplacementRepository.findById(fixture.emplacementUn().getId()))
                .contains(fixture.emplacementUn());
        assertThat(emplacementRepository.findByAgence_IdOrderByCodeEmplacementAscIdAsc(
                fixture.agenceUne().getId()))
                .extracting(Emplacement::getId)
                .containsExactly(
                        fixture.emplacementUn().getId(),
                        fixture.emplacementDeux().getId(),
                        fixture.emplacementInactif().getId());
        assertThat(emplacementRepository.findActifsByAgenceId(fixture.agenceUne().getId()))
                .extracting(Emplacement::getId)
                .containsExactly(
                        fixture.emplacementUn().getId(),
                        fixture.emplacementDeux().getId());
        assertThat(emplacementRepository.findByAgence_IdAndCodeEmplacement(
                fixture.agenceDeux().getId(), fixture.emplacementTrois().getCodeEmplacement()))
                .contains(fixture.emplacementTrois());
    }

    @Test
    void calculeLesSoldesParEmplacementEtParAgenceSansDoubleCompterLesColis() {
        Fixture fixture = creerFixture();
        Colis colis = creerColis(fixture, "COLIS-" + UUID.randomUUID(), null);

        enregistrerMouvement(10, TypeMouvement.ENTREE, colis, fixture,
                null, fixture.emplacementUn(), null, null);
        enregistrerMouvement(4, TypeMouvement.ENTREE, colis, fixture,
                null, fixture.emplacementUn(), null, null);
        enregistrerMouvement(3, TypeMouvement.SORTIE, colis, fixture,
                fixture.emplacementUn(), null, null, null);
        enregistrerMouvement(4, TypeMouvement.DEPLACEMENT, colis, fixture,
                fixture.emplacementUn(), fixture.emplacementDeux(), null, null);
        enregistrerMouvement(2, TypeMouvement.DEPLACEMENT, colis, fixture,
                fixture.emplacementDeux(), fixture.emplacementTrois(), null, null);
        entityManager.flush();

        assertThat(mouvementStockRepository.calculerSoldeParColisEtEmplacement(
                colis.getId(), fixture.emplacementUn().getId())).isEqualTo(7L);
        assertThat(mouvementStockRepository.calculerSoldeParColisEtEmplacement(
                colis.getId(), fixture.emplacementDeux().getId())).isEqualTo(2L);
        assertThat(mouvementStockRepository.calculerSoldeParColisEtEmplacement(
                colis.getId(), fixture.emplacementTrois().getId())).isEqualTo(2L);
        assertThat(mouvementStockRepository.findSoldesPositifsParColis(colis.getId()))
                .extracting(SoldeEmplacementProjection::getSolde)
                .containsExactly(7L, 2L, 2L);
        assertThat(mouvementStockRepository.calculerSoldeGlobalGere(colis.getId()))
                .isEqualTo(11L);

        List<SoldeColisAgenceProjection> stockAgenceUne =
                mouvementStockRepository.findSoldesPositifsParAgence(
                        fixture.agenceUne().getId());
        List<SoldeColisAgenceProjection> stockAgenceDeux =
                mouvementStockRepository.findSoldesPositifsParAgence(
                        fixture.agenceDeux().getId());
        assertThat(stockAgenceUne).hasSize(1);
        assertThat(stockAgenceUne.get(0).getColisId()).isEqualTo(colis.getId());
        assertThat(stockAgenceUne.get(0).getSolde()).isEqualTo(9L);
        assertThat(stockAgenceDeux).hasSize(1);
        assertThat(stockAgenceDeux.get(0).getColisId()).isEqualTo(colis.getId());
        assertThat(stockAgenceDeux.get(0).getSolde()).isEqualTo(2L);
    }

    @Test
    void exclutLesSoldesNulsNegatifsEtLesParentsAyantDesEnfants() {
        Fixture fixture = creerFixture();
        Colis soldeNul = creerColis(fixture, "COLIS-NUL-" + UUID.randomUUID(), null);
        Colis soldeNegatif = creerColis(fixture, "COLIS-NEG-" + UUID.randomUUID(), null);
        Colis parent = creerColis(fixture, "PARENT-" + UUID.randomUUID(), null);
        Colis enfant = creerColis(fixture, "ENFANT-" + UUID.randomUUID(), parent);

        enregistrerMouvement(5, TypeMouvement.ENTREE, soldeNul, fixture,
                null, fixture.emplacementUn(), null, null);
        enregistrerMouvement(5, TypeMouvement.SORTIE, soldeNul, fixture,
                fixture.emplacementUn(), null, null, null);
        enregistrerMouvement(3, TypeMouvement.SORTIE, soldeNegatif, fixture,
                fixture.emplacementUn(), null, null, null);
        enregistrerMouvement(8, TypeMouvement.ENTREE, parent, fixture,
                null, fixture.emplacementUn(), null, null);
        enregistrerMouvement(3, TypeMouvement.ENTREE, enfant, fixture,
                null, fixture.emplacementUn(), null, null);
        entityManager.flush();

        assertThat(mouvementStockRepository.calculerSoldeParColisEtEmplacement(
                soldeNul.getId(), fixture.emplacementUn().getId())).isZero();
        assertThat(mouvementStockRepository.findSoldesPositifsParColis(soldeNul.getId()))
                .isEmpty();
        assertThat(mouvementStockRepository.calculerSoldeGlobalGere(soldeNul.getId()))
                .isZero();
        assertThat(mouvementStockRepository.calculerSoldeParColisEtEmplacement(
                soldeNegatif.getId(), fixture.emplacementUn().getId())).isEqualTo(-3L);
        assertThat(mouvementStockRepository.findSoldesPositifsParColis(soldeNegatif.getId()))
                .isEmpty();
        assertThat(mouvementStockRepository.calculerSoldeGlobalGere(soldeNegatif.getId()))
                .isZero();
        assertThat(mouvementStockRepository.calculerSoldeParColisEtEmplacement(
                parent.getId(), fixture.emplacementUn().getId())).isZero();
        assertThat(mouvementStockRepository.findSoldesPositifsParAgence(
                fixture.agenceUne().getId()))
                .extracting(SoldeColisAgenceProjection::getColisId)
                .containsExactly(enfant.getId());
    }

    @Test
    void rechercheLesMouvementsParCleOperationRemiseEtHistoriqueDeterministe() {
        Fixture fixture = creerFixture();
        Colis colis = creerColis(fixture, "COLIS-HIST-" + UUID.randomUUID(), null);
        OperationColis operation = creerOperationTransport(fixture, colis);
        RemiseColis remise = new RemiseColis(
                LocalDateTime.now(), 1, "Receptionnaire", "0990000009",
                null, fixture.utilisateur(), colis, fixture.emplacementUn());
        entityManager.persist(remise);

        MouvementStock mouvementOperation = enregistrerMouvement(
                1, TypeMouvement.ENTREE, colis, fixture,
                null, fixture.emplacementUn(), operation, null);
        MouvementStock mouvementRemise = enregistrerMouvement(
                1, TypeMouvement.ENTREE, colis, fixture,
                null, fixture.emplacementUn(), null, remise);
        MouvementStock mouvementSimple = enregistrerMouvement(
                1, TypeMouvement.ENTREE, colis, fixture,
                null, fixture.emplacementUn(), null, null);
        entityManager.flush();

        assertThat(mouvementStockRepository.findByCleIdempotence(
                mouvementRemise.getCleIdempotence()))
                .contains(mouvementRemise);
        assertThat(mouvementStockRepository.findByOperation_IdOrderByDateMouvementAscIdAsc(
                operation.getId())).extracting(MouvementStock::getId)
                .containsExactly(mouvementOperation.getId());
        assertThat(mouvementStockRepository.findByRemise_IdOrderByDateMouvementAscIdAsc(
                remise.getId())).extracting(MouvementStock::getId)
                .containsExactly(mouvementRemise.getId());

        LocalDateTime dateFixe = LocalDateTime.of(2026, 1, 2, 3, 4, 5);
        jdbcTemplate.update(
                "update mouvement_stock set date_mouvement = ? where id_colis = ?",
                Timestamp.valueOf(dateFixe), colis.getId());
        entityManager.clear();

        List<Long> idsOrdonnes = List.of(
                mouvementOperation.getId(), mouvementRemise.getId(), mouvementSimple.getId());
        assertThat(mouvementStockRepository.findByColis_IdOrderByDateMouvementAscIdAsc(
                colis.getId())).extracting(MouvementStock::getId)
                .containsExactlyElementsOf(idsOrdonnes);
        assertThat(mouvementStockRepository.findHistoriqueParEmplacement(
                fixture.emplacementUn().getId())).extracting(MouvementStock::getId)
                .containsExactlyElementsOf(idsOrdonnes);
        assertThat(mouvementStockRepository.findHistoriqueParColisEtPeriode(
                colis.getId(), dateFixe, dateFixe)).hasSize(3);
        assertThat(mouvementStockRepository.findHistoriqueParEmplacementEtPeriode(
                fixture.emplacementUn().getId(), dateFixe, dateFixe)).hasSize(3);
    }

    private Fixture creerFixture() {
        String suffix = UUID.randomUUID().toString();
        Lieu lieuUne = new Lieu("Lieu-" + suffix, "VILLE", null);
        Lieu lieuDeux = new Lieu("LieuDeux-" + suffix, "VILLE", null);
        entityManager.persist(lieuUne);
        entityManager.persist(lieuDeux);

        Agence agenceUne = new Agence(
                "AgenceUne-" + suffix, "Adresse", "0990000011", "ACTIVE", lieuUne);
        Agence agenceDeux = new Agence(
                "AgenceDeux-" + suffix, "Adresse", "0990000012", "ACTIVE", lieuDeux);
        entityManager.persist(agenceUne);
        entityManager.persist(agenceDeux);

        Emplacement emplacementUn = new Emplacement(
                "A-01", "Rayon 1", null, StatutEmplacement.ACTIF, agenceUne);
        Emplacement emplacementDeux = new Emplacement(
                "A-02", "Rayon 2", null, StatutEmplacement.ACTIF, agenceUne);
        Emplacement emplacementInactif = new Emplacement(
                "A-03", "Rayon 3", null, StatutEmplacement.INACTIF, agenceUne);
        Emplacement emplacementTrois = new Emplacement(
                "A-01", "Rayon 1", null, StatutEmplacement.ACTIF, agenceDeux);
        entityManager.persist(emplacementUn);
        entityManager.persist(emplacementDeux);
        entityManager.persist(emplacementInactif);
        entityManager.persist(emplacementTrois);

        Contact contact = new Contact(
                "Nom", "Postnom", "0990000013", "Adresse", lieuUne);
        entityManager.persist(contact);
        Utilisateur utilisateur = new Utilisateur(
                "Nom", "Postnom", "Prenom", "0990000014",
                suffix + "@example.test", "hash-test", "ACTIVE", LocalDateTime.now());
        entityManager.persist(utilisateur);
        TypeColis typeColis = new TypeColis(
                "Type-" + suffix, null, "ACTIVE", null);
        entityManager.persist(typeColis);
        Expedition expedition = new Expedition(
                BigDecimal.ZERO, null, contact, agenceUne, utilisateur);
        entityManager.persist(expedition);
        entityManager.flush();

        return new Fixture(lieuUne, agenceUne, agenceDeux, emplacementUn,
                emplacementDeux, emplacementInactif, emplacementTrois,
                contact, utilisateur, typeColis, expedition);
    }

    private Colis creerColis(Fixture fixture, String reference, Colis parent) {
        Colis colis = new Colis(
                reference, "Carton", 10, null, "ENREGISTRE", LocalDateTime.now(),
                null, fixture.typeColis(), fixture.expedition(), fixture.lieuUne(),
                fixture.contact(), parent);
        entityManager.persist(colis);
        return colis;
    }

    private MouvementStock enregistrerMouvement(
            Integer quantite, TypeMouvement type, Colis colis, Fixture fixture,
            Emplacement depart, Emplacement arrivee,
            OperationColis operation, RemiseColis remise) {
        MouvementStock mouvement = new MouvementStock(
                quantite, type, null, colis, fixture.utilisateur(), depart,
                arrivee, operation, remise, UUID.randomUUID().toString());
        entityManager.persist(mouvement);
        return mouvement;
    }

    private OperationColis creerOperationTransport(Fixture fixture, Colis colis) {
        String suffix = UUID.randomUUID().toString();
        Bus bus = new Bus("IMM-" + suffix.substring(0, 8), "Marque", "Modele", 20, "ACTIF");
        entityManager.persist(bus);
        Itineraire itineraire = new Itineraire("Itineraire-" + suffix, null, "ACTIF");
        entityManager.persist(itineraire);
        Transport transport = new Transport(
                "TRANSPORT-" + suffix, null, null, null, null, "EN_COURS",
                null, bus, itineraire, fixture.agenceUne(), fixture.agenceDeux());
        entityManager.persist(transport);
        TransportArret arret = new TransportArret(
                transport, fixture.lieuUne(), 1, TypeArret.INTERMEDIAIRE,
                null, null, null);
        entityManager.persist(arret);
        ColisTransport participation = new ColisTransport(
                colis, transport, arret, 1, null);
        entityManager.persist(participation);
        OperationColis operation = new OperationColis(
                TypeOperation.EMBARQUEMENT, 1, null, participation,
                fixture.utilisateur(), arret, false, suffix);
        entityManager.persist(operation);
        entityManager.flush();
        return operation;
    }

    private record Fixture(
            Lieu lieuUne,
            Agence agenceUne,
            Agence agenceDeux,
            Emplacement emplacementUn,
            Emplacement emplacementDeux,
            Emplacement emplacementInactif,
            Emplacement emplacementTrois,
            Contact contact,
            Utilisateur utilisateur,
            TypeColis typeColis,
            Expedition expedition) {
    }
}
