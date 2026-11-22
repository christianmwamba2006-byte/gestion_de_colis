package com.ngokaf.repository;

import com.ngokaf.entity.Transport;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TransportRepository extends Repository<Transport, Long> {

    Optional<Transport> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Transport t where t.id = :id")
    Optional<Transport> findByIdForUpdate(@Param("id") Long id);

    Transport save(Transport transport);
}
