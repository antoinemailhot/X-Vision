package xvision.model;

/**
 * Adaptateur pour les images PNG
 *
 * Patron de conception: Adapter (GoF)
 * Rôle: Adapte l'interface spécifique d'ImagePNG à l'interface générique ImageTarget
 *
 * Ce adaptateur permet d'utiliser ImagePNG de manière uniforme avec les autres
 * formats d'images dans le système X-Vision.
 *
 * Principe GRASP: Low Coupling - Les clients n'ont pas besoin de connaître
 * les détails spécifiques au format PNG, ils utilisent l'interface ImageTarget.
 *
 * Structure du patron Adapter:
 * - Target (ImageTarget) : Interface que le client utilise
 * - Adaptee (ImagePNG) : Classe existante avec interface incompatible
 * - Adapter (AdapterPNG) : Cette classe, qui fait le pont entre les deux
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class AdapterPNG implements ImageTarget {

    // L'adaptée : contient la référence à l'objet qu'on adapte
    private ImagePNG adaptee;

    /**
     * Constructeur par défaut
     * Principe GRASP: Creator - AdapterPNG crée son ImagePNG associé
     */
    public AdapterPNG() {
        this.adaptee = new ImagePNG();
        System.out.println("[AdapterPNG] Adaptateur PNG créé");
    }

    /**
     * Constructeur avec ImagePNG existant
     * Permet l'injection de dépendance pour plus de flexibilité
     *
     * @param imagePNG L'instance d'ImagePNG à adapter
     */
    public AdapterPNG(ImagePNG imagePNG) {
        this.adaptee = imagePNG;
        System.out.println("[AdapterPNG] Adaptateur PNG créé avec image existante");
    }

    /**
     * Adapte la méthode chargerPNG() à l'interface ImageTarget
     * Délègue l'appel à l'adaptée
     */
    @Override
    public void charger(String chemin) {
        System.out.println("[AdapterPNG] Adaptation: charger() -> chargerPNG()");
        adaptee.chargerPNG(chemin);
    }

    /**
     * Adapte la méthode afficherPNG() à l'interface ImageTarget
     * Délègue l'appel à l'adaptée
     */
    @Override
    public void afficher() {
        System.out.println("[AdapterPNG] Adaptation: afficher() -> afficherPNG()");
        adaptee.afficherPNG();
    }

    /**
     * Adapte la méthode getLargeurPNG() à l'interface ImageTarget
     * Délègue l'appel à l'adaptée
     */
    @Override
    public int getLargeur() {
        return adaptee.getLargeurPNG();
    }

    /**
     * Adapte la méthode getHauteurPNG() à l'interface ImageTarget
     * Délègue l'appel à l'adaptée
     */
    @Override
    public int getHauteur() {
        return adaptee.getHauteurPNG();
    }

    /**
     * Adapte la méthode getDonneesPNG() à l'interface ImageTarget
     * Délègue l'appel à l'adaptée
     */
    @Override
    public byte[] getDonneesPixels() {
        return adaptee.getDonneesPNG();
    }

    /**
     * Donne accès à l'objet ImagePNG sous-jacent si nécessaire
     * (pour des opérations spécifiques au PNG)
     *
     * @return L'instance d'ImagePNG adaptée
     */
    public ImagePNG getImagePNG() {
        return adaptee;
    }
}
