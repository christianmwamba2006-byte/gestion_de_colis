
# Instructions de développement — Ngokaf

## 1. Présentation du projet

Ngokaf est une agence de transport de colis entre plusieurs villes et agences, notamment Lubumbashi et Kolwezi.

Le projet consiste à développer une application web de gestion, de suivi et de livraison des colis avec système d'identification par QR code.

L'application doit permettre de gérer :
- Les utilisateurs, les rôles et les affectations.
- Les agences.
- Les expéditions et les colis.
- Les lots et sous-colis.
- Les types de colis et formats d'étiquettes.
- La génération et l'impression des QR codes.
- Les bus.
- Les itinéraires et leurs arrêts.
- Les transports.
- Les embarquements et déchargements.
- Les stocks et emplacements.
- Les remises de colis.
- Les tentatives de remise.
- Les incidents.
- Les notifications.
- Le suivi des colis.
- Les statistiques et tableaux de bord.
- Le journal d'audit.

## 2. Technologies

Stack technique prévue :
- Backend : Java 21 et Spring Boot.
- Frontend : HTML, CSS et JavaScript.
- Framework CSS : Bootstrap.
- Base de données : MySQL.
- Architecture : API REST.
- QR code : ZXing.
- Environnement de développement : Visual Studio Code.

Ne change pas ces technologies sans autorisation.

## 3. Règles de travail

- Lire les fichiers existants avant de les modifier.
- Comprendre l'architecture du projet avant d'ajouter du code.
- Ne pas créer plusieurs fichiers remplissant la même fonction.
- Respecter les conventions de nommage déjà adoptées.
- Séparer les responsabilités entre contrôleurs, services, repositories, entités et interfaces.
- Éviter les duplications de code.
- Privilégier un code simple, lisible, maintenable et sécurisé.
- Ne pas anticiper inutilement les fonctionnalités des modules futurs.
- Ne pas modifier les modules déjà fonctionnels sans nécessité.
- Signaler toute incohérence détectée avant de prendre une décision qui affecte le fonctionnement général.
- Ne jamais inventer de règles métier.

## 4. Respect des règles métier

Les règles de gestion définies pour Ngokaf sont prioritaires.

Avant de développer une fonctionnalité :
1. Identifier le cas d'utilisation concerné.
2. Identifier les acteurs et leurs permissions.
3. Vérifier les règles métier concernées.
4. Identifier les entités et relations impliquées.
5. Implémenter la fonctionnalité.
6. Vérifier son fonctionnement et les conséquences sur les autres modules.

Si une règle métier est absente ou contradictoire, demander une clarification plutôt que d'inventer un comportement.

## 5. Sécurité

- Ne jamais stocker les mots de passe en clair.
- Utiliser une méthode sécurisée de hachage des mots de passe.
- Vérifier les permissions côté backend.
- Ne pas considérer les restrictions du frontend comme une protection suffisante.
- Valider les données reçues par l'API.
- Protéger les routes et les opérations sensibles.
- Ne pas exposer les secrets, mots de passe, clés ou informations sensibles dans le code source.
- Ne jamais supprimer ou écraser des données sans autorisation.

## 6. Méthode d'exécution

Travailler étape par étape.

Pour chaque tâche :
1. Examiner les fichiers concernés.
2. Présenter brièvement ce qui doit être réalisé.
3. Effectuer uniquement les modifications nécessaires.
4. Vérifier les erreurs de compilation et les tests disponibles.
5. Expliquer les fichiers créés ou modifiés.
6. Indiquer ce qui fonctionne et ce qui reste à faire.

Ne pas commencer une autre fonctionnalité tant que la tâche en cours n'est pas terminée ou que ses limites ne sont pas clairement identifiées.

## 7. Gestion des modifications

- Ne pas supprimer des fichiers sans justification.
- Ne pas réécrire entièrement un fichier lorsqu'une modification ciblée suffit.
- Préserver les fonctionnalités existantes.
- Ne pas installer de dépendance inutile.
- Justifier toute nouvelle bibliothèque ajoutée.
- Signaler les modifications importantes de la structure du projet.
- Ne pas effectuer de commandes destructives sans autorisation.

## 8. Communication

Répondre en français.

Les explications doivent être claires, directes et pratiques.

Éviter les explications théoriques excessives.

En cas d'erreur :
- Identifier la cause probable.
- Examiner les fichiers et les messages d'erreur.
- Proposer une correction adaptée.
- Vérifier que la correction ne crée pas de nouvelle régression.

L'objectif est de construire une application Ngokaf fonctionnelle, cohérente, sécurisée et maintenable.

## 9. Enregistrement systématique des analyses

Lorsqu'une analyse est demandée, tu dois obligatoirement enregistrer le résultat complet dans un fichier Markdown (`.md`) à la racine du projet.

### Règles à respecter

- À la fin de chaque analyse, créer un fichier Markdown contenant l'intégralité de l'analyse réalisée.
- Le fichier doit contenir les éléments examinés, les constats, les décisions ou propositions, les incohérences détectées, les corrections recommandées et la conclusion, selon la nature de l'analyse.
- Le document doit être structuré avec des titres et sous-titres clairs, et suffisamment détaillé pour servir de référence lors des prochaines étapes du développement.
- Le nom du fichier doit être explicite, en majuscules, avec des mots séparés par des underscores, et se terminer par `.md`.
- Ne jamais écraser un rapport d'analyse existant. Si un fichier portant le même nom existe, le compléter uniquement si l'analyse concerne le même sujet et la même étape ; sinon, créer un fichier distinct.
- Ne pas se contenter d'afficher le rapport dans la conversation : il faut réellement créer ou mettre à jour le fichier dans le projet.
- Ne modifier aucun autre fichier pendant une tâche d'analyse, sauf si l'utilisateur l'autorise explicitement.

### Fin de chaque analyse

Après chaque analyse, confirmer :
1. Le nom et l'emplacement du fichier créé.
2. Les principaux éléments contenus dans le rapport.
3. Les éventuelles anomalies ou points bloquants identifiés.
4. Un résumé concis dans la conversation.

Cette règle s'applique à toutes les analyses du projet, qu'elles concernent la conception, les entités, les relations, les règles métier, la sécurité, le code, les tests, l'architecture ou toute autre partie du projet.
