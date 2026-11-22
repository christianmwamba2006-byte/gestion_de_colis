package com.ngokaf.service;

import com.ngokaf.entity.Colis;
import com.ngokaf.entity.Etiquette;
import com.ngokaf.entity.FormatEtiquette;
import com.ngokaf.entity.StatutEtiquette;
import com.ngokaf.repository.ColisRepository;
import com.ngokaf.repository.EtiquetteRepository;
import com.ngokaf.repository.FormatEtiquetteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class EtiquetteService {

    private final ColisRepository colisRepository;
    private final FormatEtiquetteRepository formatRepository;
    private final EtiquetteRepository etiquetteRepository;

    public EtiquetteService(ColisRepository colisRepository,
                            FormatEtiquetteRepository formatRepository,
                            EtiquetteRepository etiquetteRepository) {
        this.colisRepository = colisRepository;
        this.formatRepository = formatRepository;
        this.etiquetteRepository = etiquetteRepository;
    }

    @Transactional
    public Etiquette creerOuRemplacer(Long colisId, Long formatId) {
        if (colisId == null || formatId == null) {
            throw new IllegalArgumentException("Le colis et le format sont obligatoires.");
        }

        Colis colis = colisRepository.findByIdForUpdate(colisId)
                .orElseThrow(() -> new EntityNotFoundException("Colis introuvable."));
        FormatEtiquette format = formatRepository.findById(formatId)
                .orElseThrow(() -> new EntityNotFoundException("Format d'étiquette introuvable."));

        List<Etiquette> etiquettesActives = etiquetteRepository
                .findAllByColis_IdAndStatut(colisId, StatutEtiquette.ACTIVE);
        if (etiquettesActives.size() > 1) {
            throw new IllegalStateException("Le colis possède déjà plusieurs étiquettes actives.");
        }
        if (!etiquettesActives.isEmpty()) {
            etiquettesActives.get(0).remplacer();
        }

        Etiquette nouvelleEtiquette = new Etiquette(UUID.randomUUID().toString(), colis, format);
        return etiquetteRepository.save(nouvelleEtiquette);
    }
}
