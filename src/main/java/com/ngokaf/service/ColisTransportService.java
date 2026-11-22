package com.ngokaf.service;

import com.ngokaf.entity.Colis;
import com.ngokaf.entity.ColisTransport;
import com.ngokaf.entity.Transport;
import com.ngokaf.entity.TransportArret;
import com.ngokaf.entity.TypeArret;
import com.ngokaf.repository.ColisTransportRepository;
import com.ngokaf.repository.TransportArretRepository;
import com.ngokaf.repository.TransportRepository;
import com.ngokaf.repository.ColisRepository;
import com.ngokaf.security.CurrentUtilisateurService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ColisTransportService {

    private final ColisRepository colisRepository;
    private final TransportRepository transportRepository;
    private final TransportArretRepository transportArretRepository;
    private final ColisTransportRepository colisTransportRepository;
    private final CurrentUtilisateurService currentUtilisateurService;

    public ColisTransportService(ColisRepository colisRepository,
                                 TransportRepository transportRepository,
                                 TransportArretRepository transportArretRepository,
                                 ColisTransportRepository colisTransportRepository,
                                 CurrentUtilisateurService currentUtilisateurService) {
        this.colisRepository = colisRepository;
        this.transportRepository = transportRepository;
        this.transportArretRepository = transportArretRepository;
        this.colisTransportRepository = colisTransportRepository;
        this.currentUtilisateurService = currentUtilisateurService;
    }

    @Transactional
    public ColisTransport creerParticipation(Long colisId, Long transportId,
                                             Long arretPrevuId, Integer quantitePrevue,
                                             String observations) {
        currentUtilisateurService.requireAuthenticatedUtilisateur();
        if (quantitePrevue == null || quantitePrevue <= 0) {
            throw new IllegalArgumentException("La quantité prévue doit être strictement positive.");
        }

        Colis colis = colisRepository.findByIdForUpdate(colisId)
                .orElseThrow(() -> new EntityNotFoundException("Colis introuvable."));
        Transport transport = transportRepository.findByIdForUpdate(transportId)
                .orElseThrow(() -> new EntityNotFoundException("Transport introuvable."));
        if (transport.getDateArriveeReelle() != null) {
            throw new IllegalStateException(
                    "Une participation ne peut pas être créée sur un transport arrivé.");
        }
        TransportArret arretPrevu = transportArretRepository.findById(arretPrevuId)
                .orElseThrow(() -> new EntityNotFoundException("Arrêt prévu introuvable."));

        if (quantitePrevue > colis.getQuantite()) {
            throw new IllegalArgumentException(
                    "La quantité prévue ne peut dépasser la quantité du colis.");
        }
        if (!arretPrevu.isEstPrevu() || arretPrevu.getTypeArret() != TypeArret.ARRIVEE
                || !arretPrevu.getTransport().getId().equals(transportId)) {
            throw new IllegalArgumentException(
                    "L'arrêt prévu doit être l'arrivée planifiée du même transport.");
        }
        if (colisTransportRepository.findByColis_IdAndTransport_Id(colisId, transportId)
                .isPresent()) {
            throw new IllegalStateException(
                    "Une participation pour ce colis et ce transport existe déjà.");
        }
        if (colisTransportRepository.existsByColis_IdAndTermineeFalse(colisId)) {
            throw new IllegalStateException(
                    "Le colis possède déjà une participation active à un transport.");
        }

        return colisTransportRepository.save(new ColisTransport(
                colis, transport, arretPrevu, quantitePrevue, observations));
    }
}
