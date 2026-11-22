package com.ngokaf.service;

import com.ngokaf.entity.*;
import com.ngokaf.repository.ColisRepository;
import com.ngokaf.repository.ColisTransportRepository;
import com.ngokaf.repository.EmplacementRepository;
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
import java.util.Optional;

@Service
public class OperationColisService {

    private static final List<TypeOperation> TYPES_EMBARQUEMENT =
            List.of(TypeOperation.EMBARQUEMENT, TypeOperation.REEMBARQUEMENT);

    private final ColisRepository colisRepository;
    private final ColisTransportRepository colisTransportRepository;
    private final EmplacementRepository emplacementRepository;
    private final TransportArretRepository transportArretRepository;
    private final TransportRepository transportRepository;
    private final OperationColisRepository operationColisRepository;
    private final CurrentUtilisateurService currentUtilisateurService;
    private final MouvementStockService mouvementStockService;

    public OperationColisService(ColisRepository colisRepository,
                                 ColisTransportRepository colisTransportRepository,
                                 EmplacementRepository emplacementRepository,
                                 TransportArretRepository transportArretRepository,
                                 TransportRepository transportRepository,
                                 OperationColisRepository operationColisRepository,
                                 CurrentUtilisateurService currentUtilisateurService,
                                 MouvementStockService mouvementStockService) {
        this.colisRepository = colisRepository;
        this.colisTransportRepository = colisTransportRepository;
        this.emplacementRepository = emplacementRepository;
        this.transportArretRepository = transportArretRepository;
        this.transportRepository = transportRepository;
        this.operationColisRepository = operationColisRepository;
        this.currentUtilisateurService = currentUtilisateurService;
        this.mouvementStockService = mouvementStockService;
    }

    @Transactional
    public OperationColis embarquer(Long participationId, Long arretId,
                                    Integer quantite, String observations,
                                    String cleIdempotence) {
        return embarquer(participationId, arretId, quantite, null,
                observations, cleIdempotence);
    }

    @Transactional
    public OperationColis embarquer(Long participationId, Long arretId,
                                    Integer quantite, Long emplacementStockId,
                                    String observations, String cleIdempotence) {
        Utilisateur acteur = currentUtilisateurService.requireAuthenticatedUtilisateur();
        validerCleIdempotence(cleIdempotence);
        OperationColis rejouee = rejouerSiExistante(cleIdempotence,
                TypeOperation.EMBARQUEMENT, participationId, arretId, quantite,
                observations, false, emplacementStockId, acteur);
        if (rejouee != null) {
            return rejouee;
        }
        ColisTransport participation = verrouillerParticipation(participationId);
        rejouee = rejouerSiExistante(cleIdempotence,
                TypeOperation.EMBARQUEMENT, participationId, arretId, quantite,
                observations, false, emplacementStockId, acteur);
        if (rejouee != null) {
            return rejouee;
        }
        validerQuantite(quantite);
        TransportArret arret = requireArretCourant(participation, arretId, false);
        validerParticipationActive(participation);
        verifierEmplacementAuLieu(emplacementStockId, arret);

        long embarquee = somme(participationId, TYPES_EMBARQUEMENT);
        long dechargee = somme(participationId, List.of(TypeOperation.DECHARGEMENT));
        if (embarquee != 0 || dechargee != 0) {
            throw new IllegalStateException(
                    "Un colis déjà embarqué doit être réembarqué après un déchargement temporaire.");
        }
        return enregistrerEmbarquement(participation, arret, acteur, quantite,
                observations, TypeOperation.EMBARQUEMENT, cleIdempotence,
                emplacementStockId);
    }

    @Transactional
    public OperationColis reembarquer(Long participationId, Long arretId,
                                      Integer quantite, String observations,
                                      String cleIdempotence) {
        return reembarquer(participationId, arretId, quantite, null,
                observations, cleIdempotence);
    }

    @Transactional
    public OperationColis reembarquer(Long participationId, Long arretId,
                                      Integer quantite, Long emplacementStockId,
                                      String observations, String cleIdempotence) {
        Utilisateur acteur = currentUtilisateurService.requireAuthenticatedUtilisateur();
        validerCleIdempotence(cleIdempotence);
        OperationColis rejouee = rejouerSiExistante(cleIdempotence,
                TypeOperation.REEMBARQUEMENT, participationId, arretId, quantite,
                observations, false, emplacementStockId, acteur);
        if (rejouee != null) {
            return rejouee;
        }
        ColisTransport participation = verrouillerParticipation(participationId);
        rejouee = rejouerSiExistante(cleIdempotence,
                TypeOperation.REEMBARQUEMENT, participationId, arretId, quantite,
                observations, false, emplacementStockId, acteur);
        if (rejouee != null) {
            return rejouee;
        }
        validerQuantite(quantite);
        TransportArret arret = requireArretCourant(participation, arretId, false);
        validerParticipationActive(participation);
        verifierEmplacementAuLieu(emplacementStockId, arret);

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
                observations, TypeOperation.REEMBARQUEMENT, cleIdempotence,
                emplacementStockId);
    }

    @Transactional
    public OperationColis decharger(Long participationId, Long arretId,
                                    Integer quantite, boolean definitif,
                                    String observations, String cleIdempotence) {
        return decharger(participationId, arretId, quantite, definitif, null,
                observations, cleIdempotence);
    }

    @Transactional
    public OperationColis decharger(Long participationId, Long arretId,
                                    Integer quantite, boolean definitif,
                                    Long emplacementStockId,
                                    String observations, String cleIdempotence) {
        Utilisateur acteur = currentUtilisateurService.requireAuthenticatedUtilisateur();
        validerCleIdempotence(cleIdempotence);
        OperationColis rejouee = rejouerSiExistante(cleIdempotence,
                TypeOperation.DECHARGEMENT, participationId, arretId, quantite,
                observations, definitif, emplacementStockId, acteur);
        if (rejouee != null) {
            return rejouee;
        }
        ColisTransport participation = verrouillerParticipation(participationId);
        rejouee = rejouerSiExistante(cleIdempotence,
                TypeOperation.DECHARGEMENT, participationId, arretId, quantite,
                observations, definitif, emplacementStockId, acteur);
        if (rejouee != null) {
            return rejouee;
        }
        validerQuantite(quantite);
        TransportArret arret = requireArretCourant(participation, arretId, true);
        validerParticipationActive(participation);
        verifierEmplacementAuLieu(emplacementStockId, arret);

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
        enregistrerMouvementStock(operation, emplacementStockId);
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
            String cleIdempotence, Long emplacementStockId) {
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
        enregistrerMouvementStock(operation, emplacementStockId);
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
            boolean clotureParticipation, Long emplacementStockId,
            Utilisateur acteur) {
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
        verifierMouvementStockAssocie(existante, emplacementStockId);
        return existante;
    }

    private void verifierEmplacementAuLieu(Long emplacementStockId, TransportArret arret) {
        if (emplacementStockId == null) {
            return;
        }
        Emplacement emplacement = emplacementRepository.findById(emplacementStockId)
                .orElseThrow(() -> new EntityNotFoundException("Emplacement introuvable."));
        if (!Objects.equals(
                emplacement.getAgence().getLieu().getId(), arret.getLieu().getId())) {
            throw new IllegalArgumentException(
                    "L'emplacement Stock ne correspond pas au lieu de l'arrêt réel.");
        }
    }

    private void enregistrerMouvementStock(
            OperationColis operation, Long emplacementStockId) {
        if (emplacementStockId == null) {
            return;
        }
        boolean entree = operation.getTypeOperation() == TypeOperation.DECHARGEMENT;
        mouvementStockService.creer(
                entree ? TypeMouvement.ENTREE : TypeMouvement.SORTIE,
                operation.getColisTransport().getColis().getId(),
                operation.getQuantiteConcernee(),
                entree ? null : emplacementStockId,
                entree ? emplacementStockId : null,
                operation.getId(),
                null,
                cleIdempotenceMouvement(operation));
    }

    private void verifierMouvementStockAssocie(
            OperationColis operation, Long emplacementStockId) {
        Optional<MouvementStock> mouvement = mouvementStockService
                .rechercherParCleIdempotence(cleIdempotenceMouvement(operation));
        if (emplacementStockId == null) {
            if (mouvement.isPresent()) {
                throw new IllegalArgumentException(
                        "La clé d'idempotence est déjà liée à une opération avec Stock.");
            }
            return;
        }

        boolean entree = operation.getTypeOperation() == TypeOperation.DECHARGEMENT;
        Long departId = entree ? null : emplacementStockId;
        Long arriveeId = entree ? emplacementStockId : null;
        if (mouvement.isEmpty()
                || !MouvementStockService.correspondAuContenu(
                mouvement.get(), entree ? TypeMouvement.ENTREE : TypeMouvement.SORTIE,
                operation.getColisTransport().getColis().getId(),
                operation.getQuantiteConcernee(), departId, arriveeId,
                operation.getId(), null)) {
            throw new IllegalArgumentException(
                    "La clé d'idempotence est déjà utilisée pour une commande Stock différente.");
        }
    }

    private String cleIdempotenceMouvement(OperationColis operation) {
        return "OPERATION_COLIS_" + operation.getId();
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
