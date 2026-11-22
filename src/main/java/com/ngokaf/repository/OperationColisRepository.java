package com.ngokaf.repository;

import com.ngokaf.entity.OperationColis;
import com.ngokaf.entity.TypeOperation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface OperationColisRepository extends Repository<OperationColis, Long> {

    @Query("select coalesce(sum(o.quantiteConcernee), 0) from OperationColis o "
            + "where o.colisTransport.id = :participationId and o.typeOperation in :types")
    Long sommeQuantites(@Param("participationId") Long participationId,
                        @Param("types") Collection<TypeOperation> types);

    Optional<OperationColis> findFirstByColisTransport_IdOrderByDateOperationDescIdDesc(
            Long participationId);

    Optional<OperationColis> findByCleIdempotence(String cleIdempotence);

    OperationColis save(OperationColis operation);
}
