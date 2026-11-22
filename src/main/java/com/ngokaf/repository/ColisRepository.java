package com.ngokaf.repository;

import com.ngokaf.entity.Colis;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ColisRepository extends JpaRepository<Colis, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Colis c where c.id = :id")
    Optional<Colis> findByIdForUpdate(@Param("id") Long id);
}
