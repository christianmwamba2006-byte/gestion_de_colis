package com.ngokaf.repository;

import com.ngokaf.entity.MouvementStock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface MouvementStockRepository extends Repository<MouvementStock, Long> {

    String SOLDE_PAR_EMPLACEMENT_SQL =
            "sum(case when m.type_mouvement in ('ENTREE', 'DEPLACEMENT') "
                    + "and m.id_emplacement_arrivee = e.id_emplacement then m.quantite else 0 end) "
                    + "- sum(case when m.type_mouvement in ('SORTIE', 'DEPLACEMENT') "
                    + "and m.id_emplacement_depart = e.id_emplacement then m.quantite else 0 end)";

    Optional<MouvementStock> findById(Long id);

    Optional<MouvementStock> findByCleIdempotence(String cleIdempotence);

    List<MouvementStock> findByColis_IdOrderByDateMouvementAscIdAsc(Long colisId);

    List<MouvementStock> findByOperation_IdOrderByDateMouvementAscIdAsc(Long operationId);

    List<MouvementStock> findByRemise_IdOrderByDateMouvementAscIdAsc(Long remiseId);

    @Query("select m from MouvementStock m "
            + "where (m.emplacementDepart.id = :emplacementId "
            + "or m.emplacementArrivee.id = :emplacementId) "
            + "order by m.dateMouvement asc, m.id asc")
    List<MouvementStock> findHistoriqueParEmplacement(
            @Param("emplacementId") Long emplacementId);

    @Query("select m from MouvementStock m where m.colis.id = :colisId "
            + "and m.dateMouvement between :debut and :fin "
            + "order by m.dateMouvement asc, m.id asc")
    List<MouvementStock> findHistoriqueParColisEtPeriode(
            @Param("colisId") Long colisId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    @Query("select m from MouvementStock m "
            + "where (m.emplacementDepart.id = :emplacementId "
            + "or m.emplacementArrivee.id = :emplacementId) "
            + "and m.dateMouvement between :debut and :fin "
            + "order by m.dateMouvement asc, m.id asc")
    List<MouvementStock> findHistoriqueParEmplacementEtPeriode(
            @Param("emplacementId") Long emplacementId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    @Query(value = "select coalesce(sum("
            + "(case when m.type_mouvement in ('ENTREE', 'DEPLACEMENT') "
            + "and m.id_emplacement_arrivee = :emplacementId then m.quantite else 0 end) "
            + "- (case when m.type_mouvement in ('SORTIE', 'DEPLACEMENT') "
            + "and m.id_emplacement_depart = :emplacementId then m.quantite else 0 end)"
            + "), 0) "
            + "from mouvement_stock m "
            + "where m.id_colis = :colisId "
            + "and not exists (select 1 from colis enfant "
            + "where enfant.id_colis_parent = m.id_colis)",
            nativeQuery = true)
    Long calculerSoldeParColisEtEmplacement(
            @Param("colisId") Long colisId,
            @Param("emplacementId") Long emplacementId);

    @Query(value = "select e.id_emplacement as emplacementId, "
            + "e.code_emplacement as codeEmplacement, e.libelle as libelle, "
            + "coalesce(" + SOLDE_PAR_EMPLACEMENT_SQL + ", 0) as solde "
            + "from mouvement_stock m "
            + "join colis c on c.id_colis = m.id_colis "
            + "join emplacement e on (e.id_emplacement = m.id_emplacement_depart "
            + "or e.id_emplacement = m.id_emplacement_arrivee) "
            + "where m.id_colis = :colisId "
            + "and not exists (select 1 from colis enfant "
            + "where enfant.id_colis_parent = c.id_colis) "
            + "group by e.id_emplacement, e.code_emplacement, e.libelle "
            + "having " + SOLDE_PAR_EMPLACEMENT_SQL + " > 0 "
            + "order by e.id_emplacement",
            nativeQuery = true)
    List<SoldeEmplacementProjection> findSoldesPositifsParColis(
            @Param("colisId") Long colisId);

    default Long calculerSoldeGlobalGere(Long colisId) {
        return findSoldesPositifsParColis(colisId).stream()
                .mapToLong(SoldeEmplacementProjection::getSolde)
                .sum();
    }

    @Query(value = "select soldes.colisId as colisId, "
            + "soldes.referenceColis as referenceColis, "
            + "soldes.agenceId as agenceId, soldes.nomAgence as nomAgence, "
            + "sum(soldes.solde) as solde "
            + "from ("
            + "select c.id_colis as colisId, c.reference_colis as referenceColis, "
            + "e.id_agence as agenceId, a.nom_agence as nomAgence, "
            + "e.id_emplacement as emplacementId, "
            + SOLDE_PAR_EMPLACEMENT_SQL + " as solde "
            + "from mouvement_stock m "
            + "join colis c on c.id_colis = m.id_colis "
            + "join emplacement e on (e.id_emplacement = m.id_emplacement_depart "
            + "or e.id_emplacement = m.id_emplacement_arrivee) "
            + "join agence a on a.id_agence = e.id_agence "
            + "where e.id_agence = :agenceId "
            + "and not exists (select 1 from colis enfant "
            + "where enfant.id_colis_parent = c.id_colis) "
            + "group by c.id_colis, c.reference_colis, e.id_agence, "
            + "a.nom_agence, e.id_emplacement "
            + "having " + SOLDE_PAR_EMPLACEMENT_SQL + " > 0"
            + ") soldes "
            + "group by soldes.colisId, soldes.referenceColis, "
            + "soldes.agenceId, soldes.nomAgence "
            + "order by soldes.colisId",
            nativeQuery = true)
    List<SoldeColisAgenceProjection> findSoldesPositifsParAgence(
            @Param("agenceId") Long agenceId);
}
