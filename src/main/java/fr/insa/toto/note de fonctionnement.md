# Rapport Explicatif du Projet "Base-de-donnee-S5"

## 1. Logique Globale et Haut Niveau

Ce projet est une application web de **gestion de tournois sportifs** (type pétanque ou e-sport, vu la gestion d'équipes et de scores). Il repose sur une architecture classique **Java** utilisant le framework **Vaadin** pour l'interface utilisateur (package `webui`) et **JDBC** pour la persistance des données (package `model`).

### Architecture
L'application est divisée en deux couches principales :
*   **Modèle (`fr.insa.toto.model`)** : Contient la logique métier et l'accès aux données (DAO pattern simplifié). Les classes comme `Tournoi`, `Joueur`, `Match` mappent directement les tables de la base de données.
*   **Interface (`fr.insa.toto.webui`)** : Gère l'affichage et les interactions utilisateurs via des composants Vaadin (`VueDetailsTournoi`, `VueInscription`, etc.).

### Flux de Fonctionnement
1.  **Gestion des Joueurs** : Les utilisateurs peuvent s'inscrire, renseigner leurs profils (Nom, Prénom, Sexe, Taille).
2.  **Cycle de Vie d'un Tournoi** :
    *   **Création** : Un administrateur configure un tournoi (nombre de joueurs par équipe, nombre de rondes, terrains).
    *   **Inscription** : Les joueurs s'inscrivent au tournoi tant qu'il est "Ouvert".
    *   **Lancement** : Le tournoi passe en statut "En cours". Les inscriptions sont closes.
    *   **Déroulement** : Le système génère des **Rondes**. Pour chaque ronde, des **Matchs** sont créés (souvent par tirage aléatoire ou selon un classement).
    *   **Saisie des Scores** : Les résultats des matchs sont rentrés, ce qui met à jour les scores des joueurs/équipes.
    *   **Clôture** : Une fois toutes les rondes terminées, le tournoi est fini et un classement final est établi.

## 2. Liste des Fonctions et Classes

Voici un inventaire des principales fonctions par classe.

### Classes du Modèle (`fr.insa.toto.model`)

#### `Tournoi`
*Classe centrale gérant l'état et la logique du tournoi.*
*   `creerTournoi(Tournoi T)` : Enregistre un nouveau tournoi en base de données.
*   `getAllTournois()` : Récupère la liste de tous les tournois existants.
*   `getTournoiById(int id)` : Récupère un tournoi spécifique via son identifiant.
*   `inscrireJoueurs(List<Joueur> joueurs)` : Inscrit une liste de joueurs au tournoi.
*   `compterJoueursInscrits()` : Renvoie le nombre actuel de participants.
*   `estNombreJoueursSuffisant()` : Vérifie s'il y a assez de joueurs pour démarrer.
*   `lancerTournoi()` : Démarre le tournoi (change le statut, ferme les inscriptions).
*   `genererMatchsPourRonde(Ronde ronde)` : Crée les matchs pour une ronde donnée (logique d'appariement).
*   `passerRondeSuivante()` : Clôture la ronde actuelle et prépare la suivante.
*   `getClassement()` : Calcule et retourne le classement des joueurs du tournoi.

#### `Joueur`
*Représente un participant.*
*   `creerJoueur(Joueur J)` : Crée un nouveau profil joueur en base.
*   `getJoueurById(int id)` : Récupère un joueur par son ID.
*   `rechercherParSurnom(String recherche)` : Trouve des joueurs via leur pseudo.
*   `getClassementGeneral()` : Récupère le classement global de tous les joueurs (tous tournois confondus).
*   `getJoueursInscritsComplets(int idTournoi)` : Liste les joueurs inscrits à un tournoi spécifique.

#### `Match`
*Représente une rencontre entre deux équipes.*
*   `saveSansId(Connection con)` : Sauvegarde le match en base.
*   `getMatchById(int matchId)` : Charge un match depuis la base.
*   `sauvegarderScoresTemporaires(...)` : Enregistre les scores en cours de saisie.
*   `distribuerPointsAuxJoueurs(...)` : Calcule et attribue les points aux joueurs en fonction du résultat.
*   `isTermine()` : Vérifie si le match a un résultat final.

#### `GestionSchema`
*Utilitaire de base de données.*
*   `creeSchema(Connection con)` : Crée toutes les tables SQL nécessaires (DDL).
*   `deleteSchema(Connection con)` : Supprime toutes les tables (DROP).
*   `razBdd(Connection con)` : Réinitialise complètement la base (Suppression + Création).

### Classes de l'Interface (`fr.insa.toto.webui`)

#### `VueDetailsTournoi`
*Page principale de gestion d'un tournoi.*
*   `afficherTournoi(Tournoi t)` : Charge les détails du tournoi dans l'interface.
*   `showInscriptionDialog()` : Affiche la pop-up pour inscrire des joueurs.
*   `actualiserAffichageRondes(Tournoi t)` : Met à jour la liste des matchs et rondes affichés.
*   `actualiserClassement()` : Rafraîchit le tableau des scores.

#### `VueInscription`
*Formulaire d'enregistrement des joueurs.*
*   `clearForm()` : Vide les champs du formulaire.
*   `readIntValue(...)` : Utilitaire pour lire les champs numériques de manière sécurisée.

---

## 3. Explication Détaillée des Fonctions Majeures

Voici une analyse plus approfondie des fonctions critiques pour le fonctionnement de l'application.

### A. `Tournoi.lancerTournoi()`
Cette fonction est le point de bascule entre la phase de configuration/inscription et la phase de jeu.
*   **Rôle** : Elle verrouille le tournoi pour empêcher de nouvelles inscriptions et initialise la première ronde de jeu.
*   **Logique** :
    1.  Vérifie que le tournoi n'est pas déjà commencé ou fini.
    2.  Vérifie que le nombre de joueurs inscrits respecte les minima (`estNombreJoueursSuffisant`).
    3.  Change l'état du tournoi (ex: `ouvert = false`).
    4.  Appelle souvent `genererMatchsPourRonde` pour la première ronde afin que les premiers matchs soient prêts dès le lancement.

### B. `Tournoi.genererMatchsPourRonde(Ronde ronde)`
C'est le "cerveau" de l'organisation des matchs.
*   **Rôle** : Déterminer qui joue contre qui.
*   **Logique** :
    1.  Récupère la liste des joueurs ou équipes disponibles.
    2.  Applique un algorithme d'appariement :
        *   *Aléatoire* (souvent pour la 1ère ronde).
        *   *Suisse ou Classement* (pour les rondes suivantes) : essaie de faire jouer les gants ensemble (1er contre 2ème, 3ème contre 4ème...).
    3.  Instancie des objets `Match` avec les paires formées.
    4.  Assigne des `Terrain`s disponibles à ces matchs.
    5.  Sauvegarde tous ces matchs en base de données via `Match.saveSansId()`.

### C. `Match.distribuerPointsAuxJoueurs(...)`
Cette fonction gère la récompense après un match.
*   **Rôle** : Traduire le résultat d'un match (Score A vs Score B) en points pour le classement général ou du tournoi.
*   **Logique** :
    1.  Compare les scores pour déterminer le vainqueur (ou match nul).
    2.  Calcule les points à attribuer (ex: 3 points pour une victoire, 1 pour un nul, 0 pour une défaite).
    3.  Met à jour les statistiques de chaque `Joueur` participant (points, victoires, défaites).
    4.  Cette mise à jour est transactionnelle pour assurer que tous les joueurs sont mis à jour ou aucun en cas d'erreur.

### D. `GestionSchema.creeSchema(Connection con)`
Fonction d'infrastructure essentielle au déploiement.
*   **Rôle** : Garantir que la base de données possède la structure nécessaire.
*   **Logique** :
    1.  Contient les requêtes SQL `CREATE TABLE` pour toutes les entités (`Joueur`, `Tournoi`, `Match`, `Equipe`, etc.).
    2.  Gère les contraintes de clés étrangères (Foreign Keys) pour assurer l'intégrité des données (ex: un match ne peut pas exister sans tournoi).
    3.  Est exécutée au démarrage ou via une commande d'administration pour installer l'application.
