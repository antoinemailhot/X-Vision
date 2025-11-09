🧠 X-Vision

X-Vision est une application Java client lourd basée sur l’architecture MVC, permettant d’afficher et de manipuler des images sous différentes perspectives.
Elle intègre le zoom, la translation, les fonctionnalités Undo/Redo, ainsi que la sauvegarde et le chargement de perspectives, tout en appliquant plus de 10 patrons de conception GRASP et GoF.

🎯 Objectif

Ce projet a été réalisé pour le cours INF1163 – Conception détaillée & modélisation d’une application.
L’objectif est de concevoir et d’implémenter une application Java structurée, en appliquant des principes avancés de POO et de design patterns.

🏗️ Architecture logicielle

Le projet suit le modèle Modèle–Vue–Contrôleur (MVC) :

Modèle : gère les données et l’état des perspectives (Image, Perspective).

Vue : affiche plusieurs représentations de l’image (vignette, perspectives principales, etc.).

Contrôleur : reçoit les interactions de l’utilisateur (menus, souris, raccourcis) et déclenche les actions correspondantes.

Le patron Observer assure la synchronisation automatique entre modèle et vues.

🧩 Patrons de conception utilisés

X-Vision intègre les patrons suivants :

MVC – séparation des responsabilités

Observer – mise à jour automatique des vues

Command – gestion des actions utilisateur et Undo/Redo

Memento – restauration d’état des perspectives

Strategy – variantes de copier/coller (échelle, translation, rien)

Singleton – gestionnaire d’historique central

Factory Method / Creator – création contrôlée des objets

Mediator – coordination des opérations de copie/collage

Information Expert, Low Coupling, High Cohesion (GRASP)

Chaque patron est documenté dans le rapport PDF.

💾 Persistance

Les perspectives sont sauvegardées et restaurées via des fichiers JSON.

Chaque sauvegarde conserve tous les paramètres nécessaires pour reconstruire une vue exacte.

🔁 Fonctionnalités principales

Affichage d’une vignette et de plusieurs perspectives d’une même image

Zoom et translation indépendants par perspective

Undo/Redo par vue active

Copier/Coller des paramètres d’affichage

Sauvegarde et chargement via JSON

Contrôleurs interchangeables (menus, souris, raccourcis clavier)

⚙️ Technologies

Langage : Java 21

Interface graphique : Swing / JavaFX

Architecture : MVC, Observer, Command

Outils UML : PlantUML, StarUML, Visual Paradigm

🚀 Installation et exécution

Cloner le dépôt
```bash
git clone https://github.com/<ton-utilisateur>/X-Vision.git
cd X-Vision
```

(MainGUI.java)

Compiler
```bash
A coder
```

Exécuter
```bash
A coder
```

💡 Astuce : Pour éviter de gérer manuellement les dépendances et compiler tous les fichiers, tu peux utiliser Gradle ou Maven. Je peux te préparer un build.gradle prêt à l’emploi si tu veux.

📚 Auteurs

Projet réalisé dans le cadre du cours INF1163 – Université du Québec en Outaouais (UQO)