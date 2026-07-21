# TourGuide

Application de recommandation touristique : elle suit la position des utilisateurs,
calcule leurs récompenses selon les attractions proches, propose les cinq attractions
les plus proches et des offres de voyage adaptées.

Stack : **Java 17**, **Spring Boot 3.1**, **JUnit 5**. API REST, sans base de données
(utilisateurs de test générés en mémoire).

---

## 🎓 Note pour l'examinateur (soutenance)

Ce projet a été mené dans une démarche de **montée en compétence** : le code est écrit
par l'apprenant, l'IA servant de **mentor** (analyse, revue, cadrage) plutôt que de
générateur. Deux documents éclairent cette démarche :

- 📓 **[Journal d'utilisation de l'IA](utilisation-ia.md)** — trace **honnête et
  chronologique** de chaque recours à l'IA (revue, explication, génération assistée), au
  format du template OpenClassrooms. Les rares générations par l'IA y sont nommées
  explicitement.
- 🧭 **[Résumé des skills personnels](skills-perso.md)** — la démarche qualité maison
  (les 5 phases, les 3 axes d'apprentissage) et les deux outils d'accompagnement
  (le mentor qui analyse, le générateur qui applique).

> Choix de périmètre assumé : TourGuide étant un projet de **modification d'un existant**,
> l'outillage qualité lourd (Checkstyle, JaCoCo, PMD, SpotBugs…) n'a **volontairement pas**
> été ajouté. Ces pistes figurent dans la partie « évolutions et améliorations futures ».

---

## Prérequis

- **Java 17** (JDK)
- **Maven** via le wrapper `./mvnw` (rien à installer)
- Les **trois bibliothèques locales** (`gpsUtil`, `RewardCentral`, `TripPricer`) fournies
  dans `libs/`, à installer dans le dépôt Maven local (voir ci-dessous).

## Installation des bibliothèques locales

Ces dépendances ne sont pas publiées sur Maven Central ; on les installe une fois dans
le dépôt local depuis les JAR de `libs/` :

```bash
./mvnw install:install-file -Dfile=libs/gpsUtil.jar       -DgroupId=gpsUtil       -DartifactId=gpsUtil       -Dversion=1.0.0 -Dpackaging=jar
./mvnw install:install-file -Dfile=libs/RewardCentral.jar -DgroupId=rewardCentral -DartifactId=rewardCentral -Dversion=1.0.0 -Dpackaging=jar
./mvnw install:install-file -Dfile=libs/TripPricer.jar    -DgroupId=tripPricer    -DartifactId=tripPricer    -Dversion=1.0.0 -Dpackaging=jar
```

## Démarrage

```bash
./mvnw spring-boot:run   # lance l'application sur http://localhost:8080
```

## Build & tests

```bash
./mvnw clean verify      # compile, exécute les tests (hors charge) et construit le JAR
```

Le JAR produit se trouve dans `target/tourguide-<version>.jar` et se lance avec
`java -jar target/tourguide-<version>.jar`.

### Tests de performance

Les tests `highVolumeTrackLocation` et `highVolumeGetRewards` valident le passage à
l'échelle (cibles : 100 000 utilisateurs en moins de 15 et 20 minutes). Ils sont taggés
`@Tag("performance")` et **exclus du build par défaut** (pour garder `verify` rapide et la
CI en quelques secondes). Pour les lancer :

```bash
./mvnw test -Pperformance
```

Ils tournent sur **100 utilisateurs** par défaut ; pour reproduire la mesure à grande
échelle, porter la constante `NUMBER_OF_USERS` à `100000` dans
`src/test/java/com/openclassrooms/tourguide/TestPerformance.java`.

## API

Base : `http://localhost:8080`. Tous les endpoints attendent un paramètre `userName`
(les utilisateurs de test sont nommés `internalUser0`, `internalUser1`, …).

| Méthode | URL                                | Description                                   |
|---------|------------------------------------|-----------------------------------------------|
| GET     | `/getLocation?userName=…`          | Dernière localisation connue de l'utilisateur |
| GET     | `/getNearbyAttractions?userName=…` | Les cinq attractions les plus proches         |
| GET     | `/getRewards?userName=…`           | Récompenses accumulées                        |
| GET     | `/getTripDeals?userName=…`         | Offres de voyage des fournisseurs             |

Exemple — les cinq attractions les plus proches :

```bash
curl "http://localhost:8080/getNearbyAttractions?userName=internalUser0"
```

Réponse `200 OK` (extrait) :

```json
[
  {
    "attractionName": "Disneyland",
    "attractionLatitude": 33.817595,
    "attractionLongitude": -117.922008,
    "userLatitude": 45.2,
    "userLongitude": 3.1,
    "distance": 2340.5,
    "rewardPoints": 428
  }
]
```

### Erreurs

Les erreurs sont renvoyées dans un format uniforme (`ApiError`) :

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Utilisateur introuvable : inconnu",
  "path": "/getLocation"
}
```

| Statut | Cas                                                               |
|--------|-------------------------------------------------------------------|
| `404`  | Utilisateur introuvable                                           |
| `400`  | Paramètre `userName` manquant ou invalide (vide)                  |
| `500`  | Erreur interne inattendue (message neutre, sans détail technique) |

## Contribution & livraison

`master` est **protégé** : aucun push direct. Chaque changement passe par une branche,
une pull request et l'**intégration continue** (build + tests) qui doit être verte avant
le merge.

```bash
git switch -c feat/ma-modif
# … commits …
git push -u origin feat/ma-modif
gh pr create --base master
```

Pour **publier une version**, pousser un tag `vX.Y.Z` : la GitHub Action construit le JAR
et publie une **release téléchargeable**.

```bash
git tag v1.0.0
git push origin v1.0.0
```

## Version

**1.0.1**
