package com.ngokaf;

import com.ngokaf.entity.ArretItineraire;
import com.ngokaf.entity.Bus;
import com.ngokaf.entity.Colis;
import com.ngokaf.entity.Contact;
import com.ngokaf.entity.Etiquette;
import com.ngokaf.entity.Expedition;
import com.ngokaf.entity.FormatEtiquette;
import com.ngokaf.entity.Itineraire;
import com.ngokaf.entity.Lieu;
import com.ngokaf.entity.Role;
import com.ngokaf.entity.Transport;
import com.ngokaf.entity.TransportArret;
import com.ngokaf.entity.TypeArret;
import com.ngokaf.entity.TypeColis;
import com.ngokaf.entity.Utilisateur;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Vérifie le démarrage du contexte Spring avec le profil {@code test}
 * et le chargement des entités JPA sur la base H2 en mémoire.
 * Aucune connexion MySQL n'est tentée dans ce profil.
 */
@SpringBootTest
@ActiveProfiles("test")
class NgokafApplicationTests {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void contextLoadsWithTestProfile() {
        assertThat(entityManagerFactory).isNotNull();
    }

    @Test
    void domainEntitiesAreRegistered() {
        var entityNames = entityManagerFactory.getMetamodel().getEntities()
                .stream()
                .map(jakarta.persistence.metamodel.EntityType::getName)
                .toList();

        assertThat(entityNames)
                .contains("Lieu", "Agence", "Role", "Utilisateur", "Affectation",
                        "Contact", "Expedition", "TypeColis", "FormatEtiquette", "Colis", "Etiquette",
                        "Bus", "Itineraire", "ArretItineraire", "Transport", "TransportArret",
                        "ColisTransport", "OperationColis");
    }
}