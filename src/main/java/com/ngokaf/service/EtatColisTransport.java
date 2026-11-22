package com.ngokaf.service;

public record EtatColisTransport(
        long quantiteEmbarquee,
        long quantiteDechargee,
        long quantiteRestanteABord,
        boolean terminee) {
}
