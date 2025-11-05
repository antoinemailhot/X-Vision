# 🧠 X-Vision

**X-Vision** est une application Java basée sur l’architecture **MVC** permettant d’afficher et de manipuler des images sous différentes perspectives.  
Elle intègre le **zoom**, la **translation**, les fonctionnalités **Undo/Redo**, ainsi que la **sauvegarde et le chargement de perspectives**, tout en appliquant **plus de 10 patrons de conception GRASP et GoF**.

---

## 🎯 Objectif du projet
Ce projet a été réalisé dans le cadre du cours **INF1163 – Conception détaillée & modélisation d’une application**.  
L’objectif est de concevoir et d’implémenter une application client lourd en Java mettant en œuvre des principes avancés de **programmation orientée objet** et de **design patterns** (GRASP/GoF) dans un environnement structuré.

---

## 🏗️ Architecture logicielle
Le projet suit rigoureusement le modèle **Modèle–Vue–Contrôleur (MVC)** :

- **Modèle** : gère les données et l’état des perspectives (ex. `Image`, `Perspective`).
- **Vue** : affiche plusieurs représentations de l’image (vignette, perspectives principales, etc.).
- **Contrôleur** : reçoit les interactions de l’utilisateur (menu, souris, raccourcis) et déclenche les commandes correspondantes.

Les vues sont découplées du modèle grâce au patron **Observer (GoF)**, garantissant une synchronisation fine et réactive.

---

## 🧩 Patrons de conception utilisés
Parmi les patrons GRASP et GoF intégrés à X-Vision :
- **MVC** – séparation des responsabilités  
- **Observer** – mise à jour automatique des vues  
- **Command** – gestion des actions utilisateur et du Undo/Redo  
- **Memento** – restauration d’état des perspectives  
- **Strategy** – variantes de copier/coller (échelle, translation, rien)  
- **Singleton** – gestionnaire d’historique central  
- **Factory Method / Creator** – création contrôlée des objets  
- **Mediator** – coordination des opérations de copie/collage  
- **Information Expert, Low Coupling, High Cohesion** (GRASP)  

Chaque patron est justifié dans le rapport PDF.

---

## 💾 Persistance
Les perspectives peuvent être sauvegardées et restaurées à partir d’un fichier **JSON**.  
Chaque sauvegarde conserve tous les paramètres nécessaires à la reconstruction exacte d’une vue après redémarrage.

---

## 🔁 Fonctionnalités principales
- Affichage d’une **vignette** et de **plusieurs perspectives** d’une même image  
- **Zoom** et **translation** indépendants par perspective  
- **Undo/Redo** par vue active  
- **Copier/Coller** des paramètres d’affichage  
- **Sauvegarde et chargement** d’états via JSON  
- **Contrôleurs interchangeables** (menus, souris, raccourcis clavier)  

---

## ⚙️ Technologies
- **Langage :** Java 21  
- **Bibliothèques :** JavaFX / Swing (selon l’interface choisie)  
- **Architecture :** MVC, Observer, Command  
- **Outils UML :** Visual Paradigm, StarUML ou PlantUML  

---

## 🚀 Exécution
1. Cloner le dépôt :
   ```bash
   git clone https://github.com/<ton-utilisateur>/X-Vision.git
   cd X-Vision
```

Compiler :
```bash
javac -d bin src/**/*.java
```

Exécuter :
```bash
java -cp bin com.xvision.Main
```

📚 Auteurs

```bash
javac -d bin src/**/*.java
```

Exécuter :
```bash
java -cp bin com.xvision.Main
```

```bash
java -cp bin com.xvision.Main
```
📚 Auteurs

Projet réalisé dans le cadre du cours INF1163 – Conception détaillée & modélisation d’une application
Université du Québec en Outaouais (UQO)