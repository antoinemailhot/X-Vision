package xvision.model;

/**
 * Adaptateur pour les images SVG
 *
 * Patron de conception: Adapter (GoF)
 * Rôle: Adapte l'interface spécifique d'ImageSVG à l'interface générique ImageTarget
 *
 * Ce adaptateur permet d'utiliser ImageSVG de manière uniforme avec les autres
 * formats d'images dans le système X-Vision, malgré la nature vectorielle du SVG
 * (contrairement aux formats matriciels PNG/JPEG).
 *
 * Principe GRASP: Low Coupling - Les clients n'ont pas besoin de connaître
 * les détails spécifiques au format SVG vectoriel, ils utilisent l'interface ImageTarget.
 *
 * Structure du patron Adapter:
 * - Target (ImageTarget) : Interface que le client utilise
 * - Adaptee (ImageSVG) : Classe existante avec interface incompatible
 * - Adapter (AdapterSVG) : Cette classe, qui fait le pont entre les deux
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class AdapterSVG implements ImageTarget {

    // L'adaptée : contient la référence à l'objet qu'on adapte
    private ImageSVG adaptee;

    /**
     * Constructeur par défaut
     * Principe GRASP: Creator - AdapterSVG crée son ImageSVG associé
     */
    public AdapterSVG() {
        this.adaptee = new ImageSVG();
        System.out.println("[AdapterSVG] Adaptateur SVG créé");
    }

    /**
     * Constructeur avec ImageSVG existant
     * Permet l'injection de dépendance pour plus de flexibilité
     *
     * @param imageSVG L'instance d'ImageSVG à adapter
     */
    public AdapterSVG(ImageSVG imageSVG) {
        this.adaptee = imageSVG;
        System.out.println("[AdapterSVG] Adaptateur SVG créé avec image existante");
    }

    /**
     * Adapte la méthode chargerSVG() à l'interface ImageTarget
     * Délègue l'appel à l'adaptée
     */
    @Override
    public void charger(String chemin) {
        System.out.println("[AdapterSVG] Adaptation: charger() -> chargerSVG()");
        adaptee.chargerSVG(chemin);
    }

    /**
     * Adapte la méthode afficherSVG() à l'interface ImageTarget
     * Délègue l'appel à l'adaptée
     */
    @Override
    public void afficher() {
        System.out.println("[AdapterSVG] Adaptation: afficher() -> afficherSVG()");
        adaptee.afficherSVG();
    }

    /**
     * Adapte la méthode getLargeurSVG() à l'interface ImageTarget
     * Délègue l'appel à l'adaptée
     */
    @Override
    public int getLargeur() {
        return adaptee.getLargeurSVG();
    }

    /**
     * Adapte la méthode getHauteurSVG() à l'interface ImageTarget
     * Délègue l'appel à l'adaptée
     */
    @Override
    public int getHauteur() {
        return adaptee.getHauteurSVG();
    }

    /**
     * Adapte la méthode getDonneesSVG() à l'interface ImageTarget
     * Délègue l'appel à l'adaptée
     */
    @Override
    public byte[] getDonneesPixels() {
        return adaptee.getDonneesSVG();
    }

    /**
     * Permet d'ajuster les dimensions de rendu du SVG
     * Méthode spécifique au format SVG
     *
     * Avantage du SVG: redimensionnable sans perte de qualité
     *
     * @param largeur La largeur de rendu souhaitée
     * @param hauteur La hauteur de rendu souhaitée
     */
    public void setDimensionsRendu(int largeur, int hauteur) {
        adaptee.setDimensionsRendu(largeur, hauteur);
        System.out.println("[AdapterSVG] Dimensions de rendu ajustées (sans perte de qualité)");
    }

    /**
     * Donne accès à l'objet ImageSVG sous-jacent si nécessaire
     * (pour des opérations spécifiques au SVG)
     *
     * @return L'instance d'ImageSVG adaptée
     */
    public ImageSVG getImageSVG() {
        return adaptee;
    }
}
