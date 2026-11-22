package com.ngokaf.security;

import com.ngokaf.entity.Utilisateur;
import com.ngokaf.repository.UtilisateurRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public CurrentUtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public Utilisateur requireAuthenticatedUtilisateur() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Une authentification est requise.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Utilisateur utilisateur && utilisateur.getId() != null) {
            return utilisateurRepository.findById(utilisateur.getId())
                    .orElseThrow(() -> new AccessDeniedException(
                            "L'utilisateur authentifié n'existe plus."));
        }

        return utilisateurRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException(
                        "L'identité authentifiée ne correspond à aucun utilisateur."));
    }
}
