package com.ngokaf.repository;

import com.ngokaf.entity.ColisTransport;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ColisTransportRepository extends Repository<ColisTransport, Long> {

    Optional<ColisTransport> findById(Long id);

    Optional<ColisTransport> findByColis_IdAndTransport_Id(Long colisId, Long transportId);

    boolean existsByColis_IdAndTermineeFalse(Long colisId);

    boolean existsByColis_IdAndTermineeFalseAndIdNot(Long colisId, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ct from ColisTransport ct where ct.id = :id")
    Optional<ColisTransport> findByIdForUpdate(@Param("id") Long id);

    ColisTransport save(ColisTransport participation);
}
