package com.ngokaf.service;

import com.ngokaf.entity.*;
import com.ngokaf.repository.ArretItineraireRepository;
import com.ngokaf.repository.JournalAuditRepository;
import com.ngokaf.repository.TransportArretRepository;
import com.ngokaf.repository.TransportRepository;
import com.ngokaf.security.CurrentUtilisateurService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TransportService {

    @PersistenceContext
    private EntityManager entityManager;

    private final TransportRepository transportRepository;
    private final ArretItineraireRepository arretItineraireRepository;
    private final TransportArretRepository transportArretRepository;
    private final JournalAuditRepository journalAuditRepository;
    private final CurrentUtilisateurService currentUtilisateurService;

    public TransportService(TransportRepository transportRepository,
                            ArretItineraireRepository arretItineraireRepository,
                            TransportArretRepository transportArretRepository,
                            JournalAuditRepository journalAuditRepository,
                            CurrentUtilisateurService currentUtilisateurService) {
        this.transportRepository = transportRepository;
        this.arretItineraireRepository = arretItineraireRepository;
        this.transportArretRepository = transportArretRepository;
        this.journalAuditRepository = journalAuditRepository;
        this.currentUtilisateurService = currentUtilisateurService;
    }

    @Transactional
    public Transport creerTransport(CreerTransportRequest demande) {
        currentUtilisateurService.requireAuthenticatedUtilisateur();
        Objects.requireNonNull(demande, "La demande de transport est obligatoire.");
        if (demande.referenceTransport() == null || demande.referenceTransport().isBlank()
                || demande.referenceTransport().length() > 100) {
            throw new IllegalArgumentException("La référence du transport est invalide.");
        }
        if (demande.statut() == null || demande.statut().isBlank()
                || demande.statut().length() > 50) {
            throw new IllegalArgumentException("Le statut du transport est obligatoire.");
        }
        if (demande.dateDepartPrevue() != null && demande.dateArriveePrevue() != null
                && demande.dateArriveePrevue().isBefore(demande.dateDepartPrevue())) {
            throw new IllegalArgumentException(
                    "La date d'arrivée prévue ne peut précéder le départ prévu.");
        }

        Bus bus = requireEntity(Bus.class, demande.busId(), "Bus");
        Itineraire itineraire = requireEntity(
                Itineraire.class, demande.itineraireId(), "Itinéraire");
        Agence agenceDepart = requireEntity(
                Agence.class, demande.agenceDepartId(), "Agence de départ");
        Agence agenceFinale = requireEntity(
                Agence.class, demande.agenceFinaleId(), "Agence d'arrivée");

        List<ArretItineraire> arretsPlanifies =
                arretItineraireRepository.findByItineraire_IdOrderByOrdreArretAsc(
                        itineraire.getId());
        validerParcours(arretsPlanifies, agenceDepart, agenceFinale);

        Transport transport = new Transport(
                demande.referenceTransport(),
                demande.dateDepartPrevue(),
                null,
                demande.dateArriveePrevue(),
                null,
                demande.statut(),
                demande.observations(),
                bus,
                itineraire,
                agenceDepart,
                agenceFinale);
        transport = transportRepository.save(transport);
        entityManager.flush();

        List<TransportArret> copies = new ArrayList<>();
        for (ArretItineraire arret : arretsPlanifies) {
            LocalDateTime datePrevue = switch (arret.getTypeArret()) {
                case DEPART -> demande.dateDepartPrevue();
                case ARRIVEE -> demande.dateArriveePrevue();
                case INTERMEDIAIRE -> null;
            };
            TransportArret copie = new TransportArret(
                    transport,
                    arret.getLieu(),
                    arret.getOrdreArret(),
                    arret.getTypeArret(),
                    datePrevue,
                    arret.getHeureIndicative(),
                    arret.getObservations());
            transportArretRepository.save(copie);
            copies.add(copie);
        }
        transport.setArrets(copies);
        return transport;
    }

    @Transactional
    public TransportArret enregistrerPassageArret(Long arretId) {
        currentUtilisateurService.requireAuthenticatedUtilisateur();
        TransportArret reference = transportArretRepository.findById(arretId)
                .orElseThrow(() -> new EntityNotFoundException("Arrêt de transport introuvable."));
        Transport transport = transportRepository.findByIdForUpdate(
                        reference.getTransport().getId())
                .orElseThrow(() -> new EntityNotFoundException("Transport introuvable."));
        TransportArret arret = transportArretRepository.findByIdForUpdate(arretId)
                .orElseThrow(() -> new EntityNotFoundException("Arrêt de transport introuvable."));

        if (!arret.isEstPrevu()) {
            throw new IllegalArgumentException(
                    "Un arrêt hors itinéraire est déjà enregistré comme passage réel.");
        }
        if (arret.getOrdreReel() != null || arret.getDateReelle() != null) {
            throw new IllegalStateException("Le passage de cet arrêt est déjà enregistré.");
        }
        if (transport.getDateArriveeReelle() != null) {
            throw new IllegalStateException("Le transport est déjà arrivé.");
        }

        int dernierOrdre = transportArretRepository.maxOrdreReel(transport.getId());
        if (arret.getTypeArret() == TypeArret.DEPART) {
            if (dernierOrdre != 0 || transport.getDateDepartReelle() != null) {
                throw new IllegalStateException("Le départ réel du transport est déjà enregistré.");
            }
        } else if (transport.getDateDepartReelle() == null) {
            throw new IllegalStateException(
                    "Le départ doit être enregistré avant les autres passages.");
        }

        LocalDateTime maintenant;
        if (arret.getTypeArret() == TypeArret.DEPART) {
            maintenant = transport.enregistrerDepartReel();
        } else if (arret.getTypeArret() == TypeArret.ARRIVEE) {
            maintenant = transport.enregistrerArriveeReelle();
        } else {
            maintenant = LocalDateTime.now();
        }
        arret.enregistrerPassage(dernierOrdre + 1, maintenant);
        return arret;
    }

    @Transactional
    public TransportArret ajouterArretHorsItineraire(
            Long transportId, Long lieuId, String cleIdempotence) {
        Utilisateur acteur = currentUtilisateurService.requireAuthenticatedUtilisateur();
        validerCleIdempotence(cleIdempotence);
        Transport transport = transportRepository.findByIdForUpdate(transportId)
                .orElseThrow(() -> new EntityNotFoundException("Transport introuvable."));

        TransportArret existant = transportArretRepository.findByCleIdempotence(cleIdempotence)
                .orElse(null);
        if (existant != null) {
            if (Objects.equals(existant.getTransport().getId(), transportId)
                    && Objects.equals(existant.getLieu().getId(), lieuId)) {
                return existant;
            }
            throw new IllegalArgumentException(
                    "La clé d'idempotence est déjà utilisée pour une autre commande.");
        }

        if (transport.getDateDepartReelle() == null || transport.getDateArriveeReelle() != null) {
            throw new IllegalStateException(
                    "Un arrêt hors itinéraire ne peut être ajouté que pendant le transport.");
        }
        Lieu lieu = requireEntity(Lieu.class, lieuId, "Lieu");
        int ordreReel = transportArretRepository.maxOrdreReel(transportId) + 1;
        LocalDateTime maintenant = LocalDateTime.now();
        TransportArret arret = TransportArret.arretHorsItineraire(
                transport, lieu, ordreReel, maintenant, cleIdempotence);
        arret = transportArretRepository.save(arret);
        entityManager.flush();

        journalAuditRepository.save(new JournalAudit(
                TypeActionAudit.CREATION,
                "TransportArret",
                arret.getId(),
                null,
                "transportId=" + transportId + ", lieuId=" + lieuId
                        + ", ordreReel=" + ordreReel,
                acteur));
        return arret;
    }

    private void validerCleIdempotence(String cleIdempotence) {
        if (cleIdempotence == null || cleIdempotence.isBlank()
                || cleIdempotence.length() > 100) {
            throw new IllegalArgumentException(
                    "La clé d'idempotence doit contenir entre 1 et 100 caractères.");
        }
    }

    private void validerParcours(List<ArretItineraire> arrets,
                                 Agence agenceDepart, Agence agenceFinale) {
        if (arrets.size() < 2) {
            throw new IllegalArgumentException(
                    "L'itinéraire doit comporter au moins un départ et une arrivée.");
        }
        if (arrets.get(0).getTypeArret() != TypeArret.DEPART
                || arrets.get(arrets.size() - 1).getTypeArret() != TypeArret.ARRIVEE) {
            throw new IllegalArgumentException(
                    "Le premier arrêt doit être DEPART et le dernier ARRIVEE.");
        }
        for (int i = 1; i < arrets.size() - 1; i++) {
            if (arrets.get(i).getTypeArret() != TypeArret.INTERMEDIAIRE) {
                throw new IllegalArgumentException(
                        "Les arrêts situés entre le départ et l'arrivée doivent être INTERMEDIAIRE.");
            }
        }
        for (ArretItineraire arret : arrets) {
            if (arret.getOrdreArret() == null || arret.getOrdreArret() <= 0
                    || arret.getLieu() == null || arret.getTypeArret() == null) {
                throw new IllegalArgumentException(
                        "Chaque arrêt d'itinéraire doit avoir un ordre positif, un type et un lieu.");
            }
        }
        if (!Objects.equals(arrets.get(0).getLieu().getId(), agenceDepart.getLieu().getId())) {
            throw new IllegalArgumentException(
                    "Le lieu du premier arrêt ne correspond pas à l'agence de départ.");
        }
        if (!Objects.equals(arrets.get(arrets.size() - 1).getLieu().getId(),
                agenceFinale.getLieu().getId())) {
            throw new IllegalArgumentException(
                    "Le lieu du dernier arrêt ne correspond pas à l'agence d'arrivée.");
        }
    }

    private <T> T requireEntity(Class<T> type, Long id, String label) {
        if (id == null) {
            throw new IllegalArgumentException(label + " obligatoire.");
        }
        T entity = entityManager.find(type, id);
        if (entity == null) {
            throw new EntityNotFoundException(label + " introuvable.");
        }
        return entity;
    }
}
