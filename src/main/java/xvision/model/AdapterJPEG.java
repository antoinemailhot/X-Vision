package xvision.model;

/**
 * Adaptateur pour les images JPEG
 *
 * Patron de conception: Adapter (GoF)
 * Rôle: Adapte l'interface spécifique d'ImageJPEG à l'interface générique ImageTarget
 *
 * Ce adaptateur permet d'utiliser ImageJPEG de manière uniforme avec les autres
 * formats d'images dans le système X-Vision, malgré les spécificités du JPEG
 * (compression avec perte, qualité ajustable, etc.)
 *
 * Principe GRASP: Low Coupling - Les clients n'ont pas besoin de connaître
 * les détails spécifiques au format JPEG, ils utilisent l'interface ImageTarget.
 *
 * Structure du patron Adapter:
 * - Target (ImageTarget) : Interface que le client utilise
 * - Adaptee (ImageJPEG) : Classe existante avec interface incompatible
 * - Adapter (AdapterJPEG) : Cette classe, qui fait le pont entre les deux
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class AdapterJPEG implements ImageTarget {

    // L'adaptée : contient la référence à l'objet qu'on adapte
    private ImageJPEG adaptee;

    /**
     * Constructeur par défaut
     * Principe GRASP: Creator - AdapterJPEG crée son ImageJPEG associé
     */
    public AdapterJPEG() {
        this.adaptee = new ImageJPEG();
        System.out.println("[AdapterJPEG] Adaptateur JPEG créé");
    }

    /**
     * Constructeur avec ImageJPEG existant
     * Permet l'injection de dépendance pour plus de flexibilité
     *
     * @param imageJPEG L'instance d'ImageJPEG à adapter
     */
    public AdapterJPEG(ImageJPEG imageJPEG) {
        this.adaptee = imageJPEG;
        System.out.println("[AdapterJPEG] Adaptateur JPEG créé avec image existante");
    }

    /**
     * Adapte la méthode chargerJPEG() à l'interface ImageTarget
     * Délègue l'appel à l'adaptée
     */
    @Override
    public void charger(String chemin) {
        System.out.println("[AdapterJPEG] Adaptation: charger() -> chargerJPEG()");
        adaptee.chargerJPEG(chemin);
    }

    /**
     * Adapte la méthode afficherJPEG() à l'interface ImageTarget
     * Délègue l'appel à l'adaptée
     */
    @Override
    public void afficher() {
        System.out.println("[AdapterJPEG] Adaptation: afficher() -> afficherJPEG()");
        adaptee.afficherJPEG();
    }

    /**
     * Adapte la méthode getLargeurJPEG() à l'interface ImageTarget
     * Délègue l'appel à l'adaptée
     */
    @Override
    public int getLargeur() {
        return adaptee.getLargeurJPEG();
    }

    /**
     * Adapte la méthode getHauteurJPEG() à l'interface ImageTarget
     * Délègue l'appel à l'adaptée
     */
    @Override
    public int getHauteur() {
        return adaptee.getHauteurJPEG();
    }

    /**
     * Adapte la méthode getDonneesJPEG() à l'interface ImageTarget
     * Délègue l'appel à l'adaptée
     */
    @Override
    public byte[] getDonneesPixels() {
        return adaptee.getDonneesJPEG();
    }

    /**
     * Permet d'ajuster la qualité de compression JPEG
     * Méthode spécifique au format JPEG
     *
     * @param qualite La qualité entre 0.0 (faible) et 1.0 (maximale)
     */
    public void setQualiteCompression(float qualite) {
        adaptee.setQualiteCompression(qualite);
        System.out.println("[AdapterJPEG] Qualité de compression ajustée: " + (qualite * 100) + "%");
    }

    /**
     * Donne accès à l'objet ImageJPEG sous-jacent si nécessaire
     * (pour des opérations spécifiques au JPEG)
     *
     * @return L'instance d'ImageJPEG adaptée
     */
    public ImageJPEG getImageJPEG() {
        return adaptee;
    }
}
