package com.ngokaf.repository;

import com.ngokaf.entity.TransportArret;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransportArretRepository extends Repository<TransportArret, Long> {

    Optional<TransportArret> findById(Long id);

    Optional<TransportArret> findByCleIdempotence(String cleIdempotence);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from TransportArret a where a.id = :id")
    Optional<TransportArret> findByIdForUpdate(@Param("id") Long id);

    List<TransportArret> findByTransport_IdOrderByOrdrePrevuAsc(Long transportId);

    @Query("select coalesce(max(a.ordreReel), 0) from TransportArret a "
            + "where a.transport.id = :transportId")
    Integer maxOrdreReel(@Param("transportId") Long transportId);

    TransportArret save(TransportArret arret);
}
