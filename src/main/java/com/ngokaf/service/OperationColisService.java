package com.ngokaf.service;

import com.ngokaf.entity.*;
import com.ngokaf.repository.ColisRepository;
import com.ngokaf.repository.ColisTransportRepository;
import com.ngokaf.repository.OperationColisRepository;
import com.ngokaf.repository.TransportArretRepository;
import com.ngokaf.repository.TransportRepository;
import com.ngokaf.security.CurrentUtilisateurService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class OperationColisService {

    private static final List<TypeOperation> TYPES_EMBARQUEMENT =
            List.of(TypeOperation.EMBARQUEMENT, TypeOperation.REEMBARQUEMENT);

    private final ColisRepository colisRepository;
    private final ColisTransportRepository colisTransportRepository;
    private final TransportArretRepository transportArretRepository;
    private final TransportRepository transportRepository;
    private final OperationColisRepository operationColisRepository;
    private final CurrentUtilisateurService currentUtilisateurService;

    public OperationColisService(ColisRepository colisRepository,
                                 ColisTransportRepository colisTransportRepository,
                                 TransportArretRepository transportArretRepository,
                                 TransportRepository transportRepository,
                                 OperationColisRepository operationColisRepository,
                                 CurrentUtilisateurService currentUtilisateurService) {
        this.colisRepository = colisRepository;
        this.colisTransportRepository = colisTransportRepository;
        this.transportArretRepository = transportArretRepository;
        this.transportRepository = transportRepository;
        this.operationColisRepository = operationColisRepository;
        this.currentUtilisateurService = currentUtilisateurService;
    }

    @Transactional
    public OperationColis embarquer(Long participationId, Long arretId,
                                    Integer quantite, String observations,
                                    String cleIdempotence) {
        Utilisateur acteur = currentUtilisateurService.requireAuthenticatedUtilisateur();
        validerCleIdempotence(cleIdempotence);
        OperationColis rejouee = rejouerSiExistante(cleIdempotence,
                TypeOperation.EMBARQUEMENT, participationId, arretId, quantite,
                observations, false, acteur);
        if (rejouee != null) {
            return rejouee;
        }
        ColisTransport participation = verrouillerParticipation(participationId);
        rejouee = rejouerSiExistante(cleIdempotence,
                TypeOperation.EMBARQUEMENT, participationId, arretId, quantite,
                observations, false, acteur);
        if (rejouee != null) {
            return rejouee;
        }
        validerQuantite(quantite);
        TransportArret arret = requireArretCourant(participation, arretId, false);
        validerParticipationActive(participation);

        long embarquee = somme(participationId, TYPES_EMBARQUEMENT);
        long dechargee = somme(participationId, List.of(TypeOperation.DECHARGEMENT));
        if (embarquee != 0 || dechargee != 0) {
            throw new IllegalStateException(
                    "Un colis déjà embarqué doit être réembarqué après un déchargement temporaire.");
        }
        return enregistrerEmbarquement(participation, arret, acteur, quantite,
                observations, TypeOperation.EMBARQUEMENT, cleIdempotence);
    }

    @Transactional
    public OperationColis reembarquer(Long participationId, Long arretId,
                                      Integer quantite, String observations,
                                      String cleIdempotence) {
        Utilisateur acteur = currentUtilisateurService.requireAuthenticatedUtilisateur();
        validerCleIdempotence(cleIdempotence);
        OperationColis rejouee = rejouerSiExistante(cleIdempotence,
                TypeOperation.REEMBARQUEMENT, participationId, arretId, quantite,
                observations, false, acteur);
        if (rejouee != null) {
            return rejouee;
        }
        ColisTransport participation = verrouillerParticipation(participationId);
        rejouee = rejouerSiExistante(cleIdempotence,
                TypeOperation.REEMBARQUEMENT, participationId, arretId, quantite,
                observations, false, acteur);
        if (rejouee != null) {
            return rejouee;
        }
        validerQuantite(quantite);
        TransportArret arret = requireArretCourant(participation, arretId, false);
        validerParticipationActive(participation);

        long embarquee = somme(participationId, TYPES_EMBARQUEMENT);
        long dechargee = somme(participationId, List.of(TypeOperation.DECHARGEMENT));
        OperationColis derniere = operationColisRepository
                .findFirstByColisTransport_IdOrderByDateOperationDescIdDesc(participationId)
                .orElseThrow(() -> new IllegalStateException(
                        "Aucun embarquement antérieur ne permet un réembarquement."));
        if (embarquee == 0 || dechargee == 0
                || derniere.getTypeOperation() != TypeOperation.DECHARGEMENT
                || derniere.isClotureParticipation()) {
            throw new IllegalStateException(
                    "Le réembarquement exige un déchargement temporaire précédent.");
        }
        return enregistrerEmbarquement(participation, arret, acteur, quantite,
                observations, TypeOperation.REEMBARQUEMENT, cleIdempotence);
    }

    @Transactional
    public OperationColis decharger(Long participationId, Long arretId,
                                    Integer quantite, boolean definitif,
                                    String observations, String cleIdempotence) {
        Utilisateur acteur = currentUtilisateurService.requireAuthenticatedUtilisateur();
        validerCleIdempotence(cleIdempotence);
        OperationColis rejouee = rejouerSiExistante(cleIdempotence,
                TypeOperation.DECHARGEMENT, participationId, arretId, quantite,
                observations, definitif, acteur);
        if (rejouee != null) {
            return rejouee;
        }
        ColisTransport participation = verrouillerParticipation(participationId);
        rejouee = rejouerSiExistante(cleIdempotence,
                TypeOperation.DECHARGEMENT, participationId, arretId, quantite,
                observations, definitif, acteur);
        if (rejouee != null) {
            return rejouee;
        }
        validerQuantite(quantite);
        TransportArret arret = requireArretCourant(participation, arretId, true);
        validerParticipationActive(participation);

        long embarquee = somme(participationId, TYPES_EMBARQUEMENT);
        long dechargee = somme(participationId, List.of(TypeOperation.DECHARGEMENT));
        long solde = embarquee - dechargee;
        if (solde < 0) {
            throw new IllegalStateException("Le cumul des opérations produit un solde négatif.");
        }
        if (quantite > solde) {
            throw new IllegalArgumentException("Le déchargement dépasse le solde à bord.");
        }
        if (definitif && quantite != solde) {
            throw new IllegalArgumentException(
                    "Un déchargement définitif doit ramener le solde à bord à zéro.");
        }

        OperationColis operation = new OperationColis(
                TypeOperation.DECHARGEMENT,
                quantite,
                observations,
                participation,
                acteur,
                arret,
                definitif,
                cleIdempotence);
        LocalDateTime maintenant = LocalDateTime.now();
        participation.enregistrerDechargement(maintenant, definitif, arret);
        operationColisRepository.save(operation);
        return operation;
    }

    @Transactional(readOnly = true)
    public EtatColisTransport consulterEtat(Long participationId) {
        currentUtilisateurService.requireAuthenticatedUtilisateur();
        ColisTransport participation = colisTransportRepository.findById(participationId)
                .orElseThrow(() -> new EntityNotFoundException("Participation introuvable."));
        long embarquee = somme(participationId, TYPES_EMBARQUEMENT);
        long dechargee = somme(participationId, List.of(TypeOperation.DECHARGEMENT));
        long solde = embarquee - dechargee;
        if (solde < 0) {
            throw new IllegalStateException("Le cumul des opérations produit un solde négatif.");
        }
        return new EtatColisTransport(embarquee, dechargee, solde, participation.isTerminee());
    }

    private OperationColis enregistrerEmbarquement(
            ColisTransport participation, TransportArret arret, Utilisateur acteur,
            int quantite, String observations, TypeOperation type,
            String cleIdempotence) {
        long embarquee = somme(participation.getId(), TYPES_EMBARQUEMENT);
        long dechargee = somme(participation.getId(), List.of(TypeOperation.DECHARGEMENT));
        long solde = embarquee - dechargee;
        if (solde < 0 || solde + quantite > participation.getQuantitePrevue()) {
            throw new IllegalArgumentException(
                    "L'embarquement dépasserait la quantité maximale prévue à bord.");
        }
        LocalDateTime maintenant = LocalDateTime.now();
        OperationColis operation = new OperationColis(
                type, quantite, observations, participation, acteur, arret, false,
                cleIdempotence);
        participation.enregistrerEmbarquement(maintenant);
        operationColisRepository.save(operation);
        return operation;
    }

    private ColisTransport verrouillerParticipation(Long participationId) {
        ColisTransport reference = colisTransportRepository.findById(participationId)
                .orElseThrow(() -> new EntityNotFoundException("Participation introuvable."));
        colisRepository.findByIdForUpdate(reference.getColis().getId())
                .orElseThrow(() -> new EntityNotFoundException("Colis introuvable."));
        return colisTransportRepository.findByIdForUpdate(participationId)
                .orElseThrow(() -> new EntityNotFoundException("Participation introuvable."));
    }

    private TransportArret requireArretCourant(
            ColisTransport participation, Long arretId, boolean dechargement) {
        Transport transport = transportRepository.findByIdForUpdate(
                        participation.getTransport().getId())
                .orElseThrow(() -> new EntityNotFoundException("Transport introuvable."));
        TransportArret arret = transportArretRepository.findByIdForUpdate(arretId)
                .orElseThrow(() -> new EntityNotFoundException("Arrêt de transport introuvable."));
        if (!arret.getTransport().getId().equals(participation.getTransport().getId())) {
            throw new IllegalArgumentException(
                    "L'arrêt n'appartient pas au transport de la participation.");
        }
        if (arret.getOrdreReel() == null || arret.getDateReelle() == null) {
            throw new IllegalStateException(
                    "Une opération de colis exige un arrêt dont le passage réel est enregistré.");
        }
        Integer ordreCourant = transportArretRepository.maxOrdreReel(transport.getId());
        if (!Objects.equals(ordreCourant, arret.getOrdreReel())) {
            throw new IllegalStateException(
                    "Une opération ne peut pas être enregistrée sur un arrêt déjà dépassé.");
        }
        if (transport.getDateArriveeReelle() != null
                && (!dechargement || arret.getTypeArret() != TypeArret.ARRIVEE)) {
            throw new IllegalStateException(
                    "Seul un déchargement est permis à l'arrivée du transport.");
        }
        if (arret.getTypeArret() == TypeArret.ARRIVEE
                && transport.getDateArriveeReelle() == null) {
            throw new IllegalStateException(
                    "L'arrivée du transport doit être enregistrée avant les opérations à destination.");
        }
        return arret;
    }

    private OperationColis rejouerSiExistante(
            String cleIdempotence, TypeOperation type, Long participationId,
            Long arretId, Integer quantite, String observations,
            boolean clotureParticipation, Utilisateur acteur) {
        OperationColis existante = operationColisRepository
                .findByCleIdempotence(cleIdempotence)
                .orElse(null);
        if (existante == null) {
            return null;
        }

        boolean memeCommande = existante.getTypeOperation() == type
                && Objects.equals(existante.getColisTransport().getId(), participationId)
                && Objects.equals(existante.getTransportArret().getId(), arretId)
                && Objects.equals(existante.getQuantiteConcernee(), quantite)
                && Objects.equals(existante.getObservations(), observations)
                && existante.isClotureParticipation() == clotureParticipation
                && Objects.equals(existante.getUtilisateur().getId(), acteur.getId());
        if (!memeCommande) {
            throw new IllegalArgumentException(
                    "La clé d'idempotence est déjà utilisée pour une commande différente.");
        }
        return existante;
    }

    private void validerCleIdempotence(String cleIdempotence) {
        if (cleIdempotence == null || cleIdempotence.isBlank()
                || cleIdempotence.length() > 100) {
            throw new IllegalArgumentException(
                    "La clé d'idempotence doit contenir entre 1 et 100 caractères.");
        }
    }

    private void validerParticipationActive(ColisTransport participation) {
        if (participation.isTerminee()) {
            throw new IllegalStateException("Une participation terminée ne peut pas être rouverte.");
        }
        if (colisTransportRepository.existsByColis_IdAndTermineeFalseAndIdNot(
                participation.getColis().getId(), participation.getId())) {
            throw new IllegalStateException(
                    "Le colis possède une autre participation active à un transport.");
        }
    }

    private void validerQuantite(Integer quantite) {
        if (quantite == null || quantite <= 0) {
            throw new IllegalArgumentException("La quantité de l'opération doit être positive.");
        }
    }

    private long somme(Long participationId, List<TypeOperation> types) {
        Long somme = operationColisRepository.sommeQuantites(participationId, types);
        return somme == null ? 0 : somme;
    }
}
