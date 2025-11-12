package xvision.model;

/**
 * Interface cible pour le patron Adapter (GoF)
 *
 * Fournit une interface uniforme pour charger et afficher des images,
 * indépendamment de leur format (PNG, JPEG, SVG, etc.)
 *
 * Principe GRASP: Low Coupling - Les clients utilisent cette interface
 * sans connaître les implémentations concrètes des différents formats.
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public interface ImageTarget {

    /**
     * Charge une image depuis un chemin de fichier
     *
     * @param chemin Le chemin vers le fichier image
     * @throws IllegalArgumentException si le chemin est invalide
     */
    void charger(String chemin);

    /**
     * Affiche l'image chargée
     * Cette méthode sera appelée par les vues pour le rendu
     */
    void afficher();

    /**
     * Retourne la largeur de l'image
     *
     * @return La largeur en pixels
     */
    int getLargeur();

    /**
     * Retourne la hauteur de l'image
     *
     * @return La hauteur en pixels
     */
    int getHauteur();

    /**
     * Retourne les données de pixels de l'image
     *
     * @return Un tableau de bytes représentant les pixels
     */
    byte[] getDonneesPixels();
}
