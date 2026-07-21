# Résumé des skills personnels — démarche qualité Java/Spring

Deux skills maison encadrent le projet. Ils partagent **la même grille de qualité**
(les 5 phases) mais jouent des rôles **opposés et complémentaires** : l'un *analyse*,
l'autre *applique*.

## Les deux skills

### 🧭 `compagnon` — le mentor (il analyse, il ne génère pas)
Rôle : **lire** le code existant, **repérer** manques et erreurs, les **classer** par
phase, et **expliquer** — sans réécrire le projet à ma place. Objectif : que je
**progresse par moi-même** (montée en compétence, pas vibe-coding).

- Se déclenche sur : « analyse mon projet », « où sont mes manques / erreurs ? »,
  « revue pédagogique », « prépare-moi pour la soutenance ».
- Produit deux artefacts de suivi (jamais du code métier) : `rapport.md` (revue +
  checklist cochable) et `utilisation-ia.md` (journal de transparence OC).
- Distingue toujours **« il manque X »** de **« X est là mais comporte l'erreur Y »**.
- Chaque point suit : **constat → pourquoi → exemple minimal → à toi de jouer**.

### 🛠️ `java-spring-conventions` — l'exécutant (il applique et génère)
Rôle : **installer l'outillage**, **échafauder**, **mettre le code en conformité**,
selon le style maison. C'est vers lui qu'on bascule quand on veut *faire* le travail.

- Se déclenche sur : échafauder un projet, outiller le `pom.xml`
  (Checkstyle/JaCoCo/PMD/SpotBugs), concevoir/revoir l'architecture, `.env`/Docker,
  `application.yml`/README, vérifier avant commit/push/PR.
- Fait respecter : **TDD d'abord**, Maven + Lombok, Javadoc française, logs SLF4J,
  suppression du code mort, commits FR (contributeur unique `monsieur486`, sans
  `Co-Authored-By` ; branche + PR pour les gros chantiers), YAML commenté, secrets
  hors du code, SOLID, site Maven.

> **Règle d'or :** le compagnon **montre la direction**, `java-spring-conventions`
> **fait la route**. Toute génération de code (bascule vers le second) doit être
> journalisée honnêtement dans `utilisation-ia.md`, étiquetée ⚙️ « génération ».

## La grille commune — les 5 phases qualité

| Phase | Objet | Vérifier |
|-------|-------|----------|
| **1 — Outillage `pom.xml`** | Plugins de rapports : compilateur+Lombok, JaCoCo 80 %, Checkstyle, SpotBugs+FindSecBugs, PMD+CPD, `maven-site-plugin`+`<reporting>`, (reco) ArchUnit | `./mvnw clean verify site` |
| **2 — Checkstyle** | Style : nommage, pas d'import wildcard, ligne ≤ 120, indentation, **nombres magiques** → constante nommée | `./mvnw checkstyle:check` |
| **3 — PMD** | Conception : complexité, longueurs, code mort/inutilisé, duplication (CPD), **chaînes répétées** → constante, SRP/couplage | `./mvnw pmd:check pmd:cpd-check` |
| **4 — SpotBugs** | Bugs (bytecode) + **sécurité** (FindSecBugs : injection, crypto faible, secrets en dur) | `./mvnw spotbugs:check` |
| **5 — Logs, Javadoc, config, README** | Finition : SLF4J (jamais `System.out`), Javadoc FR sur le public, `application.yml` commenté sans valeur en dur, README racine | lecture + rapports `target/site/` |

> Rapports **non bloquants** (build vert), mais la cible est **0 violation** et
> **JaCoCo ≥ 80 %** : « vert » ≠ « propre », il faut **lire les rapports**.

## Les 3 axes d'apprentissage (ce que le jury évalue)

- **Présentation** — indentation, largeur, blancs, ordre des membres, Javadoc lisible.
  *Le code se lit.*
- **Code propre et structuré** — nommage parlant, méthodes courtes à responsabilité
  unique, pas de code mort ni de duplication, pas de valeur « magique », erreurs nettes.
  *Le code se comprend.*
- **Architecture solide** — couches `controller → service → repository`, DTO aux
  frontières, pas de cycle, et **SOLID** :
  - **DIP** — collaborateurs en `private final` injectés par constructeur
    (`@RequiredArgsConstructor`), **jamais `@Autowired`** sur champ ; dépendre d'interfaces.
  - **SRP** — méthodes par classe : limite douce **6**, limite dure **10**.
  - **Couplage** — collaborateurs injectés : au-delà de **~5**, découper (façade,
    stratégies, événements).
  *Le code tient dans le temps.*

> **Sécurité** : préoccupation **transverse** (surtout Phase 4 + secrets hors code),
> pas un 4e axe — elle peut étiqueter un point `(sécurité)`.

## Les tests — dimension transverse (maison TDD d'abord)

Au-delà du % JaCoCo : **présence** (chaque comportement métier testé), **qualité**
des assertions (given/when/then, cas nominal **et** cas d'erreur), indices de
**démarche TDD**, **intégration** via Testcontainers (pas H2, qui ment sur les
dialectes) et tests **déterministes**.

## Où vivent les fichiers de suivi

À la **racine** du projet, **jamais dans `src/`** :

- `rapport.md` — revue pédagogique + checklist cochable (photo à jour, remise à jour à chaque revue).
- `utilisation-ia.md` — journal de transparence OpenClassrooms (chronologique, on ajoute en bas).
- `skills-perso.md` — cette fiche.
