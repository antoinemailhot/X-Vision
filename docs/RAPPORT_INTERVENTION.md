# Documentation et Correctifs Appliqués

## 1. Correctif Critique
- **Fichier** : `src/xvision/model/GestionnaireDePerspectives.java`
- **Problème** : Le fichier était tronqué, manquant la méthode `parserJSON` et causant une erreur de compilation.
- **Solution** : Restauration complète du fichier incluant le parseur JSON récursif manuel et la logique de sauvegarde "Deep State".

## 2. Documentation (Javadoc & Patterns)
J'ai ajouté une documentation détaillée (Javadoc) expliquant le fonctionnement et les patrons de conception (GoF/GRASP) pour les fichiers clés suivants :

### Modèle (Model)
- **`PerspectiveLeaf.java`** : Composite (Leaf), Memento, Observer.
- **`PerspectiveComposite.java`** : Composite (Node).
- **`PerspectiveComponent.java`** : Interface Composite.
- **`GestionnaireAnnotations.java`** : Observer (Sujet), Information Expert.
- **`GestionnaireCrop.java`** : Observer (Sujet).

### Vue (View)
- **`VueAnnotations.java`** : Observer (Vue), affichage vectoriel.
- **`MinimapOverlay.java`** : Observer, conversion de coordonnées (Viewport).

### Contrôleur (Controller)
- **`GestionnaireOutils.java`** : Mediator, State/Strategy (Gestion des modes).
- **`ModeInteraction.java`** : Interface Strategy.
- **`ModeNavigation.java`** : Strategy (Pan/Zoom avec inertie).
- **`ModeLigne.java`** : Strategy (Dessin), Adapter (Events).
- **`ModeCrop.java`** : Strategy (Recadrage).
- **`ContexteMode.java`** : Context Object / DTO.
- **`Commande.java`** : Interface Command.

## 3. État du Projet
Le projet devrait maintenant compiler correctement (sous réserve d'installation du JDK) et le code est largement commenté pour faciliter la compréhension de l'architecture MVC et des choix de conception.
