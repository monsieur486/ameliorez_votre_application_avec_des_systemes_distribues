# Journal d'utilisation de l'IA — TourGuide

> Transparence OpenClassrooms : ce journal recense **chaque usage significatif** de
> l'IA sur le projet, au fil de l'eau. Il **prouve l'appropriation** du code — en mode
> compagnon, l'IA analyse et fait corriger, elle **n'écrit pas** le code métier ; les
> lignes ci-dessous en gardent la trace honnête.
>
> **Types d'interaction** — 🔍 Revue / analyse · 💡 Explication · ⚙️ Génération assistée · 🐞 Débogage.
> Les lignes sont **chronologiques** (on ajoute en bas, on ne réécrit pas l'historique).

| Étape / Tâche | Outil IA | Prompt (résumé) | Ce que l'IA a proposé | Décision | Vérification et conclusion |
|---------------|----------|-----------------|-----------------------|----------|----------------------------|
| 🔍 Mise en place du suivi qualité | Claude Code | « passe en mode compagnon, crée les fichiers de suivi IA et le résumé des skills personnels » | Création de `utilisation-ia.md` (ce journal), `rapport.md` (support de revue vierge) et `skills-perso.md` (résumé des skills perso) ; aucun code du projet touché | Gardé | Fichiers relus ; ils vivent à la racine, hors `src/`. Prochaine étape : lancer la revue pédagogique des 5 phases |
| 🔍 Revue pédagogique globale (5 phases) | Claude Code | « analyse mon projet » | Revue classée en 5 phases → `rapport.md` : outillage `pom.xml` absent (Phase 1), 2 bugs réels (`addUserReward`, `ConcurrentModificationException`), finition Phase 5 (Javadoc/README/config), tests à durcir. Aucun code du projet modifié — l'IA a seulement pointé et expliqué | Gardé | Rapport lu et compris ; je corrige moi-même via la checklist. Constat cohérent avec le starter OC. À vérifier ensuite en lançant les outils une fois le `pom.xml` outillé |
