package com.ngokaf.entity;

/**
 * Catégorie d'un incident signalé dans l'agence.
 */
public enum TypeIncident {
    COLIS_ENDOMMAGE,
    COLIS_MANQUANT,
    COLIS_PERDU,
    ERREUR_DESTINATION,
    ERREUR_EMBARQUEMENT,
    ERREUR_DECHARGEMENT,
    RETARD_TRANSPORT,
    ACCIDENT_TRANSPORT,
    PANNE_BUS,
    INCOHERENCE_STOCK,
    ETIQUETTE_OU_QR_INVALIDE,
    INCIDENT_REMISE,
    AUTRE
}
