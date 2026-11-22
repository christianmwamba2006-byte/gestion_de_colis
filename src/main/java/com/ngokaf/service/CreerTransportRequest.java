package com.ngokaf.service;

import java.time.LocalDateTime;

public record CreerTransportRequest(
        String referenceTransport,
        LocalDateTime dateDepartPrevue,
        LocalDateTime dateArriveePrevue,
        String statut,
        String observations,
        Long busId,
        Long itineraireId,
        Long agenceDepartId,
        Long agenceFinaleId) {
}
