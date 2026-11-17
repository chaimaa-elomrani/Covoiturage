# Planification Jira — Plateforme de covoiturage multi-étapes

## Paramètres du projet

- **Nom suggéré :** Covoiturage multi-étapes
- **Clé Jira suggérée :** COVOIT
- **Type :** projet Scrum, sprint unique
- **Période :** du 16 au 19 novembre 2026 ; soutenance et livraison le 20 novembre
- **Objectif du sprint :** livrer une API REST démontrable couvrant les comptes, véhicules, trajets multi-étapes, recherche, réservations, règles de capacité et annulations, avec PostgreSQL, documentation et collection Postman.
- **Échelle d’estimation :** points Fibonacci (1, 2, 3, 5, 8). Pour un travail individuel de quatre jours, traiter ces points comme une estimation relative et réduire le périmètre si nécessaire.

## Ordre de priorité

1. Base technique exécutable avec PostgreSQL.
2. Modèle de données, règles métier, création de trajets et recherche par segment.
3. Réservation transactionnelle, calcul de prix et gestion des statuts.
4. Gestion centralisée des erreurs, documentation d’API, exemples de démonstration.
5. Livrables de soutenance : collection Postman, diagramme UML, README et données de démonstration.

## Épopées et tickets

| ID suggéré | Type | Résumé | Priorité | Estimation | Jour | Dépendances |
|---|---|---|---|---:|---|---|
| COVOIT-1 | Epic | Socle technique et persistance | Highest | — | 16 nov. | — |
| COVOIT-2 | Epic | Profils et véhicules | High | — | 16–17 nov. | COVOIT-1 |
| COVOIT-3 | Epic | Publication et recherche de trajets | Highest | — | 17–18 nov. | COVOIT-1, COVOIT-2 |
| COVOIT-4 | Epic | Réservations et cycle de vie | Highest | — | 18–19 nov. | COVOIT-3 |
| COVOIT-5 | Epic | Qualité, documentation et soutenance | High | — | 19 nov. | COVOIT-1 à 4 |
| COVOIT-6 | Story | Initialiser le projet, les couches et PostgreSQL | Highest | 5 | 16 nov. | — |
| COVOIT-7 | Story | Créer et consulter conducteurs et passagers | High | 3 | 16 nov. | COVOIT-6 |
| COVOIT-8 | Story | Gérer les véhicules du conducteur | High | 3 | 17 nov. | COVOIT-7 |
| COVOIT-9 | Story | Publier un trajet avec ses points d’arrêt | Highest | 5 | 17 nov. | COVOIT-8 |
| COVOIT-10 | Story | Rechercher un trajet par segment et date | Highest | 5 | 18 nov. | COVOIT-9 |
| COVOIT-11 | Story | Réserver des places et calculer le montant | Highest | 8 | 18 nov. | COVOIT-9 |
| COVOIT-12 | Story | Annuler une réservation et mettre à jour le trajet | Highest | 5 | 19 nov. | COVOIT-11 |
| COVOIT-13 | Story | Terminer ou annuler un trajet en bloc | High | 3 | 19 nov. | COVOIT-11 |
| COVOIT-14 | Story | Uniformiser les erreurs HTTP en ProblemDetail | High | 3 | 19 nov. | COVOIT-6 à 13 |
| COVOIT-15 | Story | Préparer Swagger/Postman et données de démonstration | High | 3 | 19 nov. | COVOIT-7 à 14 |
| COVOIT-16 | Story | Produire UML et README de livraison | High | 2 | 19 nov. | COVOIT-1 à 15 |

## Descriptions et critères d’acceptation

### COVOIT-6 — Initialiser le projet, les couches et PostgreSQL

**Description :** définir une architecture Controller / Service / Repository / Entity / DTO / Mapper, ajouter les dépendances nécessaires (MapStruct, documentation OpenAPI et tests), configurer PostgreSQL avec Docker Compose et les profils de configuration.

**Critères d’acceptation :**

- PostgreSQL démarre via Docker Compose et l’application se connecte à la base.
- Les packages reflètent les couches de l’application.
- Le projet utilise Java 17+ et Maven ; les secrets ne sont pas committés.
- Les DTO de l’API sont des records et MapStruct assure leur conversion.

### COVOIT-7 — Créer et consulter conducteurs et passagers

**Description :** exposer les opérations nécessaires à la création, consultation, modification et recherche des profils conducteur et passager.

**Critères d’acceptation :**

- Les champs obligatoires sont validés ; email unique pour les deux types de profil et numéro de permis unique pour un conducteur.
- La recherche conducteur accepte nom ou email ; la recherche passager accepte email.
- Les réponses ne divulguent pas d’informations qui ne sont pas nécessaires à l’API.

### COVOIT-8 — Gérer les véhicules du conducteur

**Description :** permettre l’ajout, la consultation, la modification et la suppression d’un véhicule appartenant au conducteur.

**Critères d’acceptation :**

- Une immatriculation ne peut être enregistrée qu’une fois.
- La capacité passagers est comprise entre 1 et 8.
- Un conducteur ne peut modifier ou supprimer que ses propres véhicules.
- La suppression est refusée si le véhicule est déjà associé à un trajet.

### COVOIT-9 — Publier un trajet avec ses points d’arrêt

**Description :** permettre à un conducteur de publier un trajet associé à l’un de ses véhicules, avec au moins deux points d’arrêt ordonnés.

**Critères d’acceptation :**

- Le premier point est le départ (ordre 0), le dernier l’arrivée ; les villes ne se répètent pas.
- Les heures sont strictement croissantes et le départ est futur.
- Le prix du premier point vaut 0 ; les prix suivants sont strictement croissants et stockés en `BigDecimal` à deux décimales.
- Le nombre de places proposé est compris entre 1 et la capacité du véhicule.
- Un conducteur ne peut publier qu’avec son propre véhicule.
- Les trajets commencent au statut `PUBLIE`.

### COVOIT-10 — Rechercher un trajet par segment et date

**Description :** rechercher les trajets disponibles entre deux villes présentes dans le parcours, dans le bon ordre, avec date, prix maximal et pagination.

**Critères d’acceptation :**

- Le départ recherché précède l’arrivée recherchée dans le même trajet.
- Seuls les trajets `PUBLIE` avec suffisamment de places sont renvoyés.
- Le prix comparé au filtre maximal est la différence des prix des deux points recherchés.
- Les résultats sont paginés et triés par heure de départ.
- La requête de recherche récupère les deux points et les places restantes sans charger une collection par résultat.

### COVOIT-11 — Réserver des places et calculer le montant

**Description :** réserver une ou plusieurs places entre deux points d’un trajet et calculer le tarif automatiquement.

**Critères d’acceptation :**

- Les points appartiennent au trajet et l’ordre de montée précède celui de descente.
- Le trajet doit être `PUBLIE` et le nombre de places disponibles doit suffire.
- Un passager ne peut avoir qu’une réservation `CONFIRMEE` par trajet.
- Le montant est `(prixDescente - prixMontée) × nombrePlaces`, en `BigDecimal` à deux décimales.
- La réservation et la mise à jour du statut sont atomiques ; le trajet devient `COMPLET` à zéro place disponible.
- Un conducteur ne peut pas réserver ; les places restantes sont calculées à partir des réservations confirmées.

### COVOIT-12 — Annuler une réservation et mettre à jour le trajet

**Description :** permettre à un passager d’annuler sa réservation confirmée avant la limite des 24 heures précédant le départ.

**Critères d’acceptation :**

- Un passager ne peut annuler que sa propre réservation.
- L’annulation est refusée si le départ est dans moins de 24 heures ou si la réservation n’est pas confirmée.
- La réservation passe à `ANNULEE` et les places sont libérées dans la même transaction.
- Un trajet `COMPLET` repasse à `PUBLIE` dès qu’une place est libérée.

### COVOIT-13 — Terminer ou annuler un trajet en bloc

**Description :** permettre au conducteur propriétaire de terminer ou annuler son trajet selon son statut et son heure de départ.

**Critères d’acceptation :**

- La terminaison est permise uniquement après l’heure de départ ; le statut devient `TERMINE`.
- L’annulation est permise pour un trajet `PUBLIE` ou `COMPLET`.
- L’annulation du trajet et le passage de toutes ses réservations confirmées à `ANNULEE` forment une seule transaction.
- Les statuts finaux `TERMINE` et `ANNULE` ne permettent plus de modification.
- Un conducteur ne peut agir que sur ses propres trajets.

### COVOIT-14 — Uniformiser les erreurs HTTP en ProblemDetail

**Description :** centraliser les erreurs de validation, d’accès et de règles métier dans un `@RestControllerAdvice`.

**Critères d’acceptation :**

- Les réponses d’erreur sont des `ProblemDetail` JSON et incluent timestamp, code HTTP, type, message clair et chemin.
- Les codes suivent le brief : 400 validation, 403 accès interdit, 404 ressource absente, 409 règle métier, 500 erreur interne.
- Les refus métier sont journalisés au niveau WARN et les opérations métier au niveau INFO.

### COVOIT-15 — Préparer Swagger/Postman et données de démonstration

**Description :** fournir une façon simple d’explorer l’API et de reproduire les scénarios de validation à la soutenance.

**Critères d’acceptation :**

- Tous les endpoints principaux sont décrits dans Swagger ou couverts par une collection Postman importable.
- La collection inclut publication multi-étapes, recherche dans le bon et mauvais sens, réservation, trajet complet, annulation et erreurs attendues.
- Un jeu de données local/documenté permet de lancer la démonstration sans données manuelles cachées.

### COVOIT-16 — Produire UML et README de livraison

**Description :** préparer les livrables de soutenance et les instructions d’exécution.

**Critères d’acceptation :**

- Le diagramme UML présente entités, cardinalités et enums.
- Le README documente prérequis, démarrage Docker, lancement Spring, configuration, endpoints et scénario de démonstration.
- Les limites connues sont indiquées clairement.

## Découpage journalier

| Date | Objectif de fin de journée |
|---|---|
| **Lun. 16 nov.** | Projet démarre, PostgreSQL fonctionne, structure de couches et modèle initial définis ; création des profils commencée. |
| **Mar. 17 nov.** | Gestion conducteur/passager/véhicule terminée ; publication avec validations des points d’arrêt opérationnelle. |
| **Mer. 18 nov.** | Recherche paginée par segment opérationnelle ; réservation avec calcul de prix, capacité et statut complet opérationnelle. |
| **Jeu. 19 nov.** | Annulations, fin de trajet, erreurs centralisées, vérifications ciblées, Swagger/Postman, données, UML et README prêts. |
| **Ven. 20 nov.** | Répétition et soutenance ; aucune fonctionnalité majeure ne devrait être commencée ce jour-là. |

## Risques et arbitrages

- **Périmètre dense pour quatre jours :** livrer d’abord le parcours démontrable conducteur → véhicule → trajet → recherche → réservation → annulation. Repousser les recherches avancées et modifications de profil si elles menacent ce flux.
- **Concurrence sur les places :** verrouiller le trajet pendant la réservation (par exemple verrou pessimiste) et exécuter réservation/statut dans une transaction afin d’éviter le dépassement de capacité.
- **Identité/authentification absente du brief technique :** représenter le propriétaire courant de façon explicitement documentée pour la démonstration ; ne pas prétendre fournir une authentification sécurisée si elle n’est pas implémentée.
- **Sémantique d’annulation :** appliquer la limite de 24 h à l’annulation par le passager ; l’annulation par le conducteur suit le cycle de vie décrit dans le brief.

## Configuration Jira conseillée

- Créer les épopées COVOIT-1 à COVOIT-5 et lier chaque story à son épopée correspondante.
- Utiliser les statuts `À faire`, `En cours`, `À vérifier`, `Terminé`.
- Ajouter les labels `backend`, `api`, `database`, `demo` selon le ticket.
- Garder le sprint unique court ; mettre chaque ticket en sous-tâches techniques si Jira exige un suivi plus fin.
- L’ordre de réalisation suit les dépendances du tableau ; la démonstration de bout en bout est le critère de sortie du sprint.
