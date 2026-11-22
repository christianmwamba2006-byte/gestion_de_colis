package com.ngokaf.repository;

import com.ngokaf.entity.Etiquette;
import com.ngokaf.entity.StatutEtiquette;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EtiquetteRepository extends JpaRepository<Etiquette, Long> {

    List<Etiquette> findAllByColis_IdAndStatut(Long colisId, StatutEtiquette statut);

    List<Etiquette> findAllByColis_Id(Long colisId);
}
