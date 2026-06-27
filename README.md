# TourGuide

Application Spring Boot de tourisme : elle suit la localisation des utilisateurs,
leur propose les attractions les plus proches, calcule des récompenses et des
offres de voyage. Le suivi de localisation est parallélisé pour tenir la charge
(jusqu'à 100 000 utilisateurs).

Stack : **Java 17**, **Spring Boot 3.1**, **Maven**, **JUnit 5**.

## Prérequis

- **Java 17**
- **Maven 3.9+** (ou le `mvn` du système)
- Les bibliothèques fournies dans `libs/` (gpsUtil, RewardCentral, TripPricer)
  doivent être installées dans le dépôt Maven local :

```bash
mvn install:install-file -Dfile=libs/gpsUtil.jar -DgroupId=gpsUtil -DartifactId=gpsUtil -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=libs/RewardCentral.jar -DgroupId=rewardCentral -DartifactId=rewardCentral -Dversion=1.0.0 -Dpackaging=jar
mvn install:install-file -Dfile=libs/TripPricer.jar -DgroupId=tripPricer -DartifactId=tripPricer -Dversion=1.0.0 -Dpackaging=jar
```

Le nombre d'utilisateurs générés en mémoire et les seuils se règlent dans
`TourGuideConfiguration.java` (les valeurs par défaut conviennent en dev).

## Commandes

| Commande | Rôle |
| --- | --- |
| `mvn verify` | Build complet : Checkstyle (bloquant), tests, **JaCoCo ≥ 90 %**, SpotBugs (+ FindSecBugs), PMD (+ CPD), règles d'architecture ArchUnit. |
| `mvn clean verify site` | Génère le site dans `target/site/` : informations projet, Javadoc (FR), couverture JaCoCo, rapport Surefire. |
| `mvn spring-boot:run` | Démarre l'application sur `http://localhost:8080`. |
| `mvn package` puis `java -jar target/tourguide-0.0.1-SNAPSHOT.jar` | Construit puis lance le jar exécutable. |

Les **tests de charge** (`TestPerformance`, 100 000 utilisateurs) sont exclus du
build standard ; les lancer explicitement avec
`mvn test -Dtest=TestPerformance -Dsurefire.failIfNoSpecifiedTests=false`.

## API REST

Toutes les routes sont en **GET** et prennent le paramètre `userName`
(sauf l'accueil). En mode test, des utilisateurs `internalUser0`, `internalUser1`…
sont disponibles.

| Méthode & chemin | Rôle |
| --- | --- |
| `GET /` | Message d'accueil. |
| `GET /getLocation?userName=…` | Dernière localisation connue de l'utilisateur. |
| `GET /getNearbyAttractions?userName=…` | Les cinq attractions les plus proches. |
| `GET /getRewards?userName=…` | Récompenses gagnées par l'utilisateur. |
| `GET /getTripDeals?userName=…` | Dix offres de voyage. |

### Exemple

Requête :

```
GET /getNearbyAttractions?userName=internalUser0
```

Réponse (extrait) :

```json
[
  {
    "attractionName": "Disneyland",
    "attractionLatitude": 33.817595,
    "attractionLongitude": -117.922008,
    "userLatitude": 33.817595,
    "userLongitude": -117.922008,
    "distance": 0.0,
    "rewardPoints": 124
  }
]
```
