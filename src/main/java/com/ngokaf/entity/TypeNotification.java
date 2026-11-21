package com.ngokaf.entity;

/**
 * Catégorie d'événement communiquée par une notification.
 */
public enum TypeNotification {
    COLIS_ARRIVE_AGENCE,
    COLIS_DISPONIBLE_RETRAIT,
    INCIDENT_SIGNALE,
    INCIDENT_PRIS_EN_CHARGE,
    INCIDENT_RESOLU,
    INCIDENT_CLOTURE,
    RETARD_TRANSPORT_ACTION_REQUISE,
    SUIVI_COLIS_IMPORTANT
}
