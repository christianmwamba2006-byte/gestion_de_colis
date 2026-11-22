package com.ngokaf.repository;

import com.ngokaf.entity.ArretItineraire;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface ArretItineraireRepository extends Repository<ArretItineraire, Long> {

    List<ArretItineraire> findByItineraire_IdOrderByOrdreArretAsc(Long itineraireId);
}
