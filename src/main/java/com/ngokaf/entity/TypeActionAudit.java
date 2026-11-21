package com.ngokaf.entity;

/**
 * Type d'action conservée dans le journal d'audit.
 */
public enum TypeActionAudit {
    CREATION,
    MODIFICATION,
    CHANGEMENT_STATUT,
    SUPPRESSION,
    SIGNALEMENT_INCIDENT,
    PRISE_EN_CHARGE_INCIDENT,
    RESOLUTION_INCIDENT,
    CLOTURE_INCIDENT
}
