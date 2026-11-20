# Plateforme de covoiturage multi-étapes

API REST Spring Boot permettant de publier des trajets avec plusieurs arrêts, de rechercher un segment et de réserver des places.

## Fonctionnalités

- Création, consultation, recherche et modification des profils conducteurs et passagers.
- Gestion des véhicules associés à un conducteur.
- Publication de trajets décrits par une liste ordonnée de points d’arrêt.
- Recherche d’un trajet entre deux villes du parcours, dans le bon sens, avec date, prix maximal et pagination.
- Réservation d’une ou plusieurs places entre deux arrêts, avec calcul du prix du segment.
- Annulation des réservations et des trajets, et gestion des statuts `PUBLIE`, `COMPLET`, `TERMINE` et `ANNULE`.

## Technologies

- Java 17 et Spring Boot
- Spring MVC, Spring Data JPA et Hibernate
- PostgreSQL avec Docker Compose
- MapStruct et Bean Validation
- Springdoc OpenAPI / Swagger UI
- Maven

## Architecture

Le code est organisé en couches sous `src/main/java/com/coisee/covoiturage` :

- `controller` : endpoints REST et conversion des requêtes HTTP.
- `service` : interfaces des services métier.
- `service/impl` : implémentations des services.
- `repository` : accès aux données avec Spring Data JPA.
- `entity` : entités persistées en base.
- `dto` : objets d’entrée et de sortie de l’API.
- `mapper` : conversion DTO/entités avec MapStruct.
- `exception` : exceptions métier et gestion globale des erreurs.

## Prérequis

- JDK 17 ou supérieur
- Docker avec Docker Compose
- Le port PostgreSQL `5432` disponible (ou définir `DB_PORT`)

## Démarrage en local

1. Démarrer PostgreSQL :

   ```bash
   docker compose up -d
   ```

2. Vérifier que le conteneur est prêt :

   ```bash
   docker compose ps
   ```

3. Démarrer l’application avec le profil local par défaut :

   ```bash
   ./mvnw spring-boot:run
   ```

Sous Windows, utiliser `mvnw.cmd spring-boot:run`.

La configuration locale se trouve dans `src/main/resources/application-local.properties`. Les valeurs locales par défaut de la base sont `covoiturage` pour la base, l’utilisateur et le mot de passe. Elles peuvent être remplacées avec `DB_URL`, `DB_USERNAME` et `DB_PASSWORD`. Pour changer le port publié par Docker Compose, définir `DB_PORT`.

Pour arrêter PostgreSQL sans supprimer les données :

```bash
docker compose down
```

Les données sont conservées dans le volume Docker `postgres_data`. Ne pas utiliser `docker compose down -v` sauf si la suppression de ces données est voulue.

## Documentation interactive

Quand l’application est démarrée :

- Swagger UI : <http://localhost:8080/swagger-ui.html>
- Document OpenAPI JSON : <http://localhost:8080/api-docs>

## Endpoints

Tous les endpoints utilisent le préfixe `/api`.

### Conducteurs

| Méthode | Chemin | Fonction |
|---|---|---|
| `POST` | `/conducteurs` | Créer un conducteur |
| `GET` | `/conducteurs` | Lister les conducteurs |
| `GET` | `/conducteurs/{id}` | Consulter un conducteur |
| `GET` | `/conducteurs/search?searchTerm=...` | Rechercher par nom ou email |
| `PUT` | `/conducteurs/{id}` | Modifier un conducteur |

### Passagers

| Méthode | Chemin | Fonction |
|---|---|---|
| `POST` | `/passagers` | Créer un passager |
| `GET` | `/passagers` | Lister les passagers |
| `GET` | `/passagers/{id}` | Consulter un passager |
| `GET` | `/passagers/search?email=...` | Rechercher par email |
| `PUT` | `/passagers/{id}` | Modifier un passager |

### Véhicules

| Méthode | Chemin | Fonction |
|---|---|---|
| `POST` | `/vehicules` | Ajouter un véhicule |
| `GET` | `/vehicules/{vehiculeId}?conducteurId=...` | Consulter un véhicule (propriétaire requis) |
| `GET` | `/vehicules/conducteur/{conducteurId}` | Lister les véhicules d’un conducteur |
| `PUT` | `/vehicules/{vehiculeId}?conducteurId=...` | Modifier un véhicule |
| `DELETE` | `/vehicules/{vehiculeId}?conducteurId=...` | Supprimer un véhicule |

### Trajets

| Méthode | Chemin | Fonction |
|---|---|---|
| `POST` | `/trajets` | Publier un trajet multi-étapes |
| `GET` | `/trajets/{trajetId}` | Consulter le détail d’un trajet |
| `GET` | `/trajets/search` | Rechercher avec pagination |
| `PATCH` | `/trajets/{trajetId}/finish?conducteurId=...` | Terminer un trajet |
| `PATCH` | `/trajets/{trajetId}/cancel?conducteurId=...` | Annuler un trajet |

La recherche accepte les paramètres `villeDepart`, `villeArrivee`, `dateDepart` (format `AAAA-MM-JJ`), `nombrePlaces`, et facultativement `prixMax`. La pagination utilise les paramètres Spring `page` et `size`, par exemple `?page=0&size=10`.

Exemple :

```text
GET /api/trajets/search?villeDepart=Casablanca&villeArrivee=Settat&dateDepart=2026-11-25&nombrePlaces=1&prixMax=50&page=0&size=10
```

### Réservations

| Méthode | Chemin | Fonction |
|---|---|---|
| `POST` | `/reservations` | Réserver des places |
| `GET` | `/reservations/{reservationId}?passagerId=...` | Consulter une réservation du passager |
| `GET` | `/reservations/passager/{passagerId}` | Lister les réservations d’un passager |
| `GET` | `/reservations/trajet/{trajetId}?conducteurId=...` | Voir les réservations d’un trajet (conducteur propriétaire) |
| `PATCH` | `/reservations/{reservationId}/cancel?passagerId=...` | Annuler une réservation |

## Règles métier principales

- Un parcours comprend au moins deux points d’arrêt, ordonnés à partir de zéro.
- Le départ est dans le futur ; les heures de passage et les prix depuis le départ sont strictement croissants.
- Le prix du premier point vaut zéro et une ville ne peut apparaître qu’une fois dans le parcours.
- Un véhicule propose entre 1 et 8 places passagers ; le trajet ne peut pas en proposer plus que sa capacité.
- Le prix d’un segment est `prix au point de descente - prix au point de montée`.
- Les places restantes sont calculées à partir des réservations confirmées.
- Un passager peut annuler sa réservation jusqu’à 24 heures avant le départ.
- Un trajet passe de `PUBLIE` à `COMPLET` quand aucune place ne reste disponible et revient à `PUBLIE` après une annulation qui libère des places.

## Tests

Exécuter les tests avec :

```bash
./mvnw test
```

Les tests unitaires des services Trajet et Réservation sont en cours de préparation ; compléter cette couverture reste une étape de livraison.

## Livrables de démonstration

- Planification Jira : `docs/jira-planning.md`.
- Diagramme UML : [version PNG](docs/diagramme-classes.png) / [version SVG](docs/diagramme-classes.svg) ; source Graphviz : `docs/diagramme-classes.dot`.
- Collection Postman importable : `postman/Covoiturage.postman_collection.json`.
- Préparer des données de démonstration dans PostgreSQL avant la soutenance.

![Diagramme de classes UML du projet](docs/diagramme-classes.png)

## Configuration de production

Le profil `prod` utilise `src/main/resources/application-prod.properties` et exige les variables `DB_URL`, `DB_USERNAME` et `DB_PASSWORD`. Ne pas committer de vrais identifiants ou secrets.
