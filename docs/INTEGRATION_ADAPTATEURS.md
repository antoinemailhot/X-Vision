# Intégration des Adaptateurs d'Images dans X-Vision

## 📋 Vue d'ensemble

Cette documentation explique comment intégrer les adaptateurs d'images (PNG, JPEG, SVG) dans l'architecture MVC de X-Vision.

## 🎯 Patrons de conception utilisés

### 1. **Adapter (GoF)**
- **Objectif**: Unifier l'interface de différents formats d'images
- **Classes**: `AdapterPNG`, `AdapterJPEG`, `AdapterSVG`
- **Interface cible**: `ImageTarget`

### 2. **Factory Method (GoF)**
- **Objectif**: Créer les adaptateurs appropriés sans spécifier la classe concrète
- **Classe**: `FabriqueImage`
- **Méthode factory**: `creerImage(String cheminFichier)`

## 🏗️ Architecture créée

```
xvision.model/
├── ImageTarget.java          ← Interface commune (cible de l'Adapter)
├── ImagePNG.java             ← Classe concrète pour PNG (adaptée)
├── ImageJPEG.java            ← Classe concrète pour JPEG (adaptée)
├── ImageSVG.java             ← Classe concrète pour SVG (adaptée)
├── AdapterPNG.java           ← Adaptateur PNG
├── AdapterJPEG.java          ← Adaptateur JPEG
├── AdapterSVG.java           ← Adaptateur SVG
└── FabriqueImage.java        ← Factory Method
```

## 📚 Utilisation dans l'architecture MVC

### Dans le Modèle

Le modèle peut maintenant gérer différents formats d'images de manière uniforme:

```java
public class GestionnaireImages {
    private Map<String, ImageTarget> images = new HashMap<>();

    /**
     * Charge une image via la Factory
     * Principe: Information Expert + Creator
     */
    public void chargerImage(String id, String cheminFichier) {
        try {
            // La factory détecte automatiquement le format
            ImageTarget image = FabriqueImage.creerImage(cheminFichier);
            images.put(id, image);

            // Notifier les observateurs (patron Observer)
            notifierObservateurs();
        } catch (IllegalArgumentException e) {
            System.err.println("Format non supporté: " + e.getMessage());
        }
    }

    public ImageTarget getImage(String id) {
        return images.get(id);
    }
}
```

### Dans les Vues

Les vues peuvent afficher n'importe quel format via l'interface `ImageTarget`:

```java
public class VuePrincipale extends VueAbstraite implements ObserverIF {

    @Override
    public void mettreAJour() {
        rafraichir();
    }

    private void rafraichir() {
        // Récupérer l'image depuis le modèle
        ImageTarget image = gestionnaire.getImage(idImageActive);

        if (image != null) {
            // Interface uniforme - fonctionne pour tous les formats
            image.afficher();

            // Obtenir les dimensions
            int largeur = image.getLargeur();
            int hauteur = image.getHauteur();

            // Affichage avec la perspective
            afficherAvecPerspective(image, largeur, hauteur);
        }
    }
}
```

### Dans les Contrôleurs

Les contrôleurs peuvent créer des commandes pour charger des images:

```java
public class CommandeChargerImage implements Commande {
    private GestionnaireImages gestionnaire;
    private String id;
    private String cheminFichier;

    public CommandeChargerImage(GestionnaireImages gest, String id, String chemin) {
        this.gestionnaire = gest;
        this.id = id;
        this.cheminFichier = chemin;
    }

    @Override
    public void executer() {
        // Utilisation de la Factory via le gestionnaire
        gestionnaire.chargerImage(id, cheminFichier);
    }

    @Override
    public void annuler() {
        // Supprimer l'image chargée
        gestionnaire.supprimerImage(id);
    }
}
```

## 🔄 Diagramme de séquence - Chargement d'image

```
Utilisateur → ControleurMenu → GestionnaireCommandes → CommandeChargerImage
                                                              ↓
                                                    GestionnaireImages
                                                              ↓
                                                       FabriqueImage
                                                              ↓
                                      AdapterPNG/JPEG/SVG ← ImagePNG/JPEG/SVG
                                                              ↓
                                              Sujet.notifier()
                                                              ↓
                                                    VuePrincipale.mettreAJour()
```

## ✅ Principes GRASP respectés

### 1. Information Expert
- `FabriqueImage` sait comment détecter le format (extension)
- `ImagePNG/JPEG/SVG` savent gérer leurs formats respectifs
- `GestionnaireImages` sait gérer la collection d'images

### 2. Creator
- `FabriqueImage` crée les adaptateurs (elle a la logique de décision)
- `AdapterPNG/JPEG/SVG` créent leurs adaptées respectives

### 3. Low Coupling
- Les vues ne connaissent que `ImageTarget`, pas les adaptateurs concrets
- Le code client n'a pas besoin de connaître les détails de chaque format
- Ajout d'un nouveau format = créer un nouvel adaptateur (pas de modification du code client)

### 4. High Cohesion
- Chaque adaptateur a une seule responsabilité: adapter un format spécifique
- `FabriqueImage` a une seule responsabilité: créer les adaptateurs

### 5. Controller
- Les contrôleurs utilisent des `Commande` pour charger les images
- Respecte le flux MVC

## 🚀 Exemple d'utilisation complète

```java
public class MainGUI {
    public static void main(String[] args) {
        // 1. Créer le gestionnaire d'images (Modèle)
        GestionnaireImages gestionnaireImages = new GestionnaireImages();

        // 2. Créer les vues
        VuePrincipale vuePrincipale = new VuePrincipale();
        Vignette vignette = new Vignette();

        // 3. Attacher les vues au modèle (Observer)
        gestionnaireImages.attacher(vuePrincipale);
        gestionnaireImages.attacher(vignette);

        // 4. Charger différentes images via la Factory
        try {
            // PNG - détection automatique
            ImageTarget imagePNG = FabriqueImage.creerImage("photos/image1.png");

            // JPEG - détection automatique
            ImageTarget imageJPEG = FabriqueImage.creerImage("photos/photo.jpg");

            // SVG - détection automatique
            ImageTarget imageSVG = FabriqueImage.creerImage("icones/logo.svg");

            // Manipulation uniforme via ImageTarget
            imagePNG.afficher();
            imageJPEG.afficher();
            imageSVG.afficher();

        } catch (IllegalArgumentException e) {
            System.err.println("Erreur: " + e.getMessage());
        }

        // 5. Utilisation via commandes (recommandé)
        GestionnaireCommandes gestCmd = GestionnaireCommandes.getInstance();
        Commande cmdCharger = new CommandeChargerImage(
            gestionnaireImages,
            "image1",
            "photos/image1.png"
        );
        gestCmd.executerCommande(cmdCharger);

        // 6. Les vues sont automatiquement mises à jour (Observer)
    }
}
```

## 🔧 Extensibilité - Ajouter un nouveau format

Pour ajouter le support d'un nouveau format (ex: WebP):

### Étape 1: Créer la classe concrète
```java
public class ImageWebP {
    public void chargerWebP(String chemin) { ... }
    public void afficherWebP() { ... }
    public int getLargeurWebP() { ... }
    public int getHauteurWebP() { ... }
    public byte[] getDonneesWebP() { ... }
}
```

### Étape 2: Créer l'adaptateur
```java
public class AdapterWebP implements ImageTarget {
    private ImageWebP adaptee;

    public AdapterWebP() {
        this.adaptee = new ImageWebP();
    }

    @Override
    public void charger(String chemin) {
        adaptee.chargerWebP(chemin);
    }

    // Implémenter les autres méthodes...
}
```

### Étape 3: Modifier la Factory
```java
public enum FormatImage {
    PNG, JPEG, JPG, SVG, WEBP  // ← Ajouter WEBP
}

private static ImageTarget creerAdaptateur(FormatImage format, String chemin) {
    switch (format) {
        // ... cas existants ...
        case WEBP:
            return new AdapterWebP();  // ← Ajouter ce cas
    }
}
```

**C'est tout!** Le code client n'a pas besoin d'être modifié (principe Open/Closed).

## 📊 Tests

Pour exécuter les tests:

```bash
# Compilation
javac xvision/model/*.java xvision/test/TestAdaptateurs.java

# Exécution
java xvision.test.TestAdaptateurs
```

Les tests couvrent:
- ✓ Création via Factory
- ✓ Détection automatique de format
- ✓ Interface uniforme (polymorphisme)
- ✓ Gestion d'erreurs
- ✓ Méthodes spécifiques aux formats

## 🎓 Références

- **Gang of Four**: Design Patterns - Adapter (p. 139), Factory Method (p. 107)
- **GRASP**: Applying UML and Patterns (Craig Larman)
- **Diagrammes UML**: `/docs/diagramme_adapter.puml`, `/docs/diagramme_factory_method.puml`

## 📝 Notes importantes

1. **Toujours utiliser la Factory** pour créer des images (pas `new AdapterXXX()` directement)
2. **Respecter MVC**: Les vues observent le modèle, les contrôleurs utilisent des Commandes
3. **Low Coupling**: Manipuler via `ImageTarget`, pas les adaptateurs concrets
4. **Gestion d'erreurs**: La Factory lève `IllegalArgumentException` pour les formats non supportés

---

**Auteurs**: Antoine Mailhot & Micaël Racine-Leblanc
**Projet**: X-Vision - INF1163
**Date**: Novembre 2025
