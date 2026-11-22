package com.ngokaf.repository;

import com.ngokaf.entity.Utilisateur;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UtilisateurRepository extends Repository<Utilisateur, Long> {

    Optional<Utilisateur> findById(Long id);

    Optional<Utilisateur> findByEmail(String email);
}
