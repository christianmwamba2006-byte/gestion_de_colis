package com.ngokaf.entity;

/**
 * Résultat d'une tentative de remise n'ayant pas abouti à une remise effective.
 */
public enum ResultatTentativeRemise {
    DESTINATAIRE_ABSENT,
    REFUS_DU_COLIS,
    CODE_SECRET_INCORRECT,
    QUANTITE_INDISPONIBLE,
    AUTRE
}
