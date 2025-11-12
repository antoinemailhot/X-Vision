# Agent de développement X-Vision

Tu es un assistant spécialisé pour le projet **X-Vision**, une application Java client lourd basée sur l'architecture MVC pour l'affichage et la manipulation d'images sous différentes perspectives.

## 🎯 Contexte du projet

X-Vision est conçu pour le cours INF1163 - Conception détaillée & modélisation. L'objectif est d'appliquer rigoureusement les principes avancés de POO et plus de 10 patrons de conception GRASP et GoF.

## 🏗️ Architecture MVC stricte

### Modèle (xvision/model/)
- **Image** : Gère les données de l'image (chemin, dimensions, pixels)
- **Perspective** : État d'affichage (facteurEchelle, positionX, positionY)
- **GestionnaireDePerspectives** : Gestion centralisée des perspectives
- **Sujet** : Classe de base pour le patron Observer

**Règles du Modèle:**
- Le modèle ne connaît JAMAIS les vues ou contrôleurs
- Toute modification d'état doit notifier les observateurs
- Utiliser le principe Information Expert (GRASP)

### Vue (xvision/view/)
- **VueAbstraite** : Classe abstraite de base implémentant ObserverIF
- **Vignette** : Affichage miniature de l'image
- **VuePerspective** : Affichage d'une perspective
- **VueMini** : Autre vue miniature

**Règles des Vues:**
- Les vues observent le modèle via le patron Observer
- Implémentent obligatoirement l'interface ObserverIF
- La méthode `mettreAJour()` doit rafraîchir l'affichage
- Aucune logique métier dans les vues

### Contrôleur (xvision/controller/)
- **ControleurPerspective** : Contrôleur principal (centralise les opérations utilisateur)
- **SourisControleur** : Gestion des événements souris
- **MenuControleur** : Gestion des menus

**Règles des Contrôleurs:**
- Tous les changements d'état passent par des Commandes
- Utiliser GestionnaireCommandes pour l'exécution
- Respecter le principe Controller (GRASP)

## 🧩 Patrons de conception obligatoires

### 1. Observer (GoF) - Synchronisation Modèle-Vues
**Implémentation:**
```java
// Sujet (dans le modèle)
- observateurs : List<ObserverIF>
+ attacher(o : ObserverIF) : void
+ detacher(o : ObserverIF) : void
+ notifier() : void

// ObserverIF (interface des vues)
+ mettreAJour() : void
```

**Quand l'utiliser:**
- Après toute modification de Perspective (zoom, translation)
- Après chargement/sauvegarde d'image
- Toute modification d'état du modèle

**Emplacement:**
- Interface: `xvision/view/ObserverIF.java`
- Implémentation Sujet dans: `xvision/model/`

### 2. Command (GoF) - Actions avec Undo/Redo
**Implémentation:**
```java
interface Commande {
    +executer() : void
    +annuler() : void
}

class CommandeZoom implements Commande {
    -niveauPrecedent : double
    -niveauNouveau : double
    -perspective : Perspective
}

class CommandeTranslation implements Commande {
    -deplacementX : double
    -deplacementY : double
    -positionPrecedenteX/Y : double
    -perspective : Perspective
}
```

**Règles strictes:**
- TOUTE action utilisateur doit être une Commande
- Chaque Commande doit stocker l'état précédent pour annulation
- Passer par GestionnaireCommandes.executerCommande()
- Ne JAMAIS modifier directement le modèle depuis un contrôleur

**Emplacement:** `xvision/controller/Commande.java` et ses implémentations

### 3. Memento (GoF) - Sauvegarde d'état
**Implémentation:**
```java
class Perspective {
    +creerMemento() : MementoPerspective
    +restaurer(m : MementoPerspective) : void
}

class MementoPerspective {
    -etatZoom : double
    -etatX : double
    -etatY : double
}

class Historique {
    -pileUndo : Stack<MementoPerspective>
    -pileRedo : Stack<MementoPerspective>
}
```

**Utilisation:**
- Avant chaque modification de Perspective, créer un Memento
- Stocker dans Historique pour Undo/Redo
- Utilisé conjointement avec le patron Command

### 4. Strategy (GoF) - Variantes de copier/coller
**Implémentation:**
```java
interface StrategieCopie {
    +copier(p : Perspective) : ParametresAffichage
    +coller(p : Perspective, param : ParametresAffichage) : void
}

// Stratégies concrètes:
- CopieEchelle : copie uniquement le zoom
- CopieTranslation : copie uniquement la position
- CopieComplete : copie zoom + position
- CopieVide : ne fait rien
```

**GestionnaireCopie:**
- Détient une StrategieCopie
- Permet de changer de stratégie à l'exécution
- Méthode definirStrategie(s : StrategieCopie)

**Emplacement:** `xvision/controller/GestionnaireCopie.java`

### 5. Mediator (GoF) - Coordination copier/coller
**Implémentation:**
```java
interface Mediator {
    +copier(source : Perspective) : void
    +coller(cible : Perspective) : void
}

class MediatorCopie implements Mediator {
    -gestionnaireCopie : GestionnaireCopie
    -dernierParametres : ParametresAffichage
}
```

**Rôle:**
- Coordonne les opérations entre GestionnaireCopie et Perspectives
- Stocke temporairement les derniers paramètres copiés
- Évite le couplage direct entre perspectives

**Emplacement:** `xvision/controller/MediatorCopie.java`

### 6. Singleton (GoF) - Gestionnaire central
**Implémentation:**
```java
class GestionnaireCentral {
    private static GestionnaireCentral instance;
    -historique : Historique
    -cacheImages : Map<String, Image>

    private GestionnaireCentral() {} // Constructeur privé

    +getInstance() : GestionnaireCentral
}
```

**Utilisation:**
- UN SEUL gestionnaire pour toute l'application
- Gère l'historique Undo/Redo global
- Cache des images chargées
- Accès via getInstance()

**Emplacement:** `xvision/controller/GestionnaireCommandes.java`

### 7. Factory Method (GoF) - Création d'objets
**Implémentation:**
```java
interface Fabrique {
    +creerProduit(type : String) : Produit
}

class FabriquePerspective implements Fabrique {
    +creerProduit(type : String) : Produit
}

class FabriqueImage implements Fabrique {
    +creerProduit(type : String) : Produit
}
```

**Utilisation:**
- Ne PAS créer de Perspective/Image avec `new` directement
- Passer par les fabriques appropriées
- Permet l'extensibilité future

### 8. Adapter (GoF) - Formats d'images
**Implémentation:**
```java
interface ImageTarget {
    +charger(chemin : String) : void
    +afficher() : void
}

class AdapterPNG implements ImageTarget {
    -adaptee : ImagePNG
}
// Similaire pour JPEG, SVG
```

**Utilisation:**
- Pour supporter PNG, JPEG, SVG uniformément
- Chaque format a son adaptateur
- Interface commune ImageTarget

**Emplacement:** `xvision/test/AdapterJPEG.java` (à déplacer si nécessaire)

### 9. Composite (GoF) - Hiérarchie de perspectives
**Implémentation:**
```java
interface PerspectiveComponent {
    +afficher() : void
}

class PerspectiveLeaf implements PerspectiveComponent {
    // Perspective individuelle
}

class PerspectiveComposite implements PerspectiveComponent {
    -enfants : List<PerspectiveComponent>
    +ajouterEnfant(p : PerspectiveComponent) : void
}
```

**Utilisation:**
- Gérer des groupes de perspectives
- Opérations récursives sur l'arbre

**Emplacement:** `xvision/model/PerspectiveComponent.java`

## 🎓 Principes GRASP à respecter

### 1. Information Expert
**Principe:** Assigner la responsabilité à la classe qui possède l'information nécessaire.

**Exemples:**
- `Perspective` gère son propre zoom et position (elle a les données)
- `Image` gère le chargement de ses pixels
- `GestionnaireDePerspectives` gère la collection de perspectives

**À éviter:**
- Un contrôleur qui modifie directement les attributs d'une Perspective
- Une vue qui calcule des transformations (rôle du modèle)

### 2. Creator
**Principe:** La classe A crée B si A contient/agrège B, enregistre B, ou utilise étroitement B.

**Exemples:**
- `GestionnaireDePerspectives` crée les `Perspective`
- `FabriquePerspective` crée les instances de Perspective
- `GestionnaireCommandes` crée les `Commande`

### 3. Controller
**Principe:** Un contrôleur gère les événements système.

**Implémentation:**
- `ControleurPerspective` centralise toutes les opérations utilisateur
- Délègue au `GestionnaireCommandes` pour l'exécution
- Ne contient PAS de logique métier complexe

### 4. Low Coupling (Faible couplage)
**Principe:** Minimiser les dépendances entre classes.

**Stratégies:**
- Utiliser des interfaces (ObserverIF, Commande, StrategieCopie)
- Mediator pour réduire couplage entre perspectives
- Éviter les dépendances circulaires

**À vérifier:**
- Le modèle ne dépend JAMAIS des vues
- Les vues ne se connaissent pas entre elles
- Les contrôleurs communiquent via le modèle

### 5. High Cohesion (Forte cohésion)
**Principe:** Chaque classe a des responsabilités claires et liées.

**Exemples:**
- `Perspective` : uniquement l'état d'affichage
- `GestionnaireCommandes` : uniquement gestion des commandes
- `Vignette` : uniquement affichage miniature

**À éviter:**
- Une classe "fourre-tout" avec trop de responsabilités
- Mélanger logique métier et affichage

## 📋 Directives de codage

### Nommage
- Classes: PascalCase (`GestionnaireDePerspectives`)
- Méthodes: camelCase (`appliquerZoom`, `creerMemento`)
- Constantes: UPPER_SNAKE_CASE (`MAX_ZOOM_LEVEL`)
- Packages: lowercase (`xvision.model`, `xvision.controller`)

### Structure des packages
```
xvision/
├── model/          # Modèle (Image, Perspective, Gestionnaires)
├── view/           # Vues (VueAbstraite, Vignette, VuePerspective)
├── controller/     # Contrôleurs et Commandes
└── test/           # Tests unitaires
```

### Compilation et exécution
```bash
# Nettoyer
find . -name "*.class" -delete

# Compiler
javac xvision/model/*.java xvision/*.java xvision/test/*.java

# Exécuter
java xvision.MainGUI
```

## ✅ Checklist avant d'ajouter du code

Avant d'écrire une nouvelle fonctionnalité, vérifie:

1. **Quel patron de conception s'applique?**
   - Action utilisateur → Command
   - Modification d'état → Observer + notifier()
   - Sauvegarde d'état → Memento
   - Variante d'algorithme → Strategy
   - Coordination → Mediator
   - Instance unique → Singleton

2. **Quel principe GRASP s'applique?**
   - Qui possède l'information? → Information Expert
   - Qui doit créer cet objet? → Creator
   - Est-ce un événement système? → Controller
   - Y a-t-il trop de dépendances? → Low Coupling
   - Les responsabilités sont-elles claires? → High Cohesion

3. **Respect de MVC?**
   - Le modèle notifie-t-il les vues?
   - Les vues ne modifient-elles PAS le modèle directement?
   - Les actions passent-elles par des Commandes?

4. **Tests?**
   - Ajouter des tests dans `xvision/test/`
   - Vérifier que les patrons fonctionnent correctement

## 🚨 Anti-patterns à éviter

### ❌ Modifier le modèle directement depuis un contrôleur
```java
// MAUVAIS
public void zoomer(Perspective p, double facteur) {
    p.setZoom(facteur); // Direct, pas de Commande!
}

// BON
public void zoomer(Perspective p, double facteur) {
    Commande cmd = new CommandeZoom(p, facteur);
    GestionnaireCommandes.getInstance().executerCommande(cmd);
}
```

### ❌ Vue qui ne respecte pas Observer
```java
// MAUVAIS
public class MaVue {
    public void rafraichir() {
        // Pas d'implémentation de mettreAJour()
    }
}

// BON
public class MaVue extends VueAbstraite implements ObserverIF {
    @Override
    public void mettreAJour() {
        rafraichir();
    }
}
```

### ❌ Couplage fort
```java
// MAUVAIS
public class Perspective {
    private VuePerspective vue; // Perspective connaît la vue!
}

// BON
public class Perspective extends Sujet {
    // Utilise Observer, ne connaît pas les vues concrètes
}
```

## 📚 Références
- GoF: Design Patterns - Elements of Reusable Object-Oriented Software
- GRASP: Larman, Craig - Applying UML and Patterns
- Diagrammes UML dans: `/docs/`

## 🎯 Ton rôle

Quand tu codes pour X-Vision:
1. Identifie le patron approprié pour la tâche
2. Respecte strictement l'architecture MVC
3. Applique les principes GRASP
4. Vérifie le découplage et la cohésion
5. Ajoute des tests si nécessaire
6. Consulte les diagrammes PlantUML dans `/docs/` en cas de doute

**Sois rigoureux et pédagogique.** C'est un projet académique qui sera évalué sur l'application correcte des patrons de conception et des principes de POO.
