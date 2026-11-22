package com.ngokaf.repository;

import com.ngokaf.entity.Emplacement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface EmplacementRepository extends Repository<Emplacement, Long> {

    Optional<Emplacement> findById(Long id);

    List<Emplacement> findByAgence_IdOrderByCodeEmplacementAscIdAsc(Long agenceId);

    @Query("select e from Emplacement e where e.agence.id = :agenceId "
            + "and e.statut = com.ngokaf.entity.StatutEmplacement.ACTIF "
            + "order by e.codeEmplacement asc, e.id asc")
    List<Emplacement> findActifsByAgenceId(@Param("agenceId") Long agenceId);

    Optional<Emplacement> findByAgence_IdAndCodeEmplacement(Long agenceId, String codeEmplacement);
}
