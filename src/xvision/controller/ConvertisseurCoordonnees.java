package xvision.controller;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import xvision.model.PerspectiveLeaf;

/**
 * Convertit entre coordonnées écran et coordonnées image
 * Principe GRASP: Information Expert - connaît la transformation écran ↔ image
 * Principe GRASP: Pure Fabrication - classe utilitaire sans équivalent dans le domaine
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class ConvertisseurCoordonnees {

    /**
     * Convertit un point écran en coordonnées image
     * @param pointEcran Point en coordonnées écran (pixels du panel)
     * @param perspective La perspective contenant zoom et position
     * @param taillePanel Taille du panel d'affichage
     * @param tailleImage Taille de l'image originale
     * @return Point en coordonnées image (pixels de l'image)
     */
    public static Point ecranVersImage(Point pointEcran,
                                        PerspectiveLeaf perspective,
                                        Dimension taillePanel,
                                        Dimension tailleImage) {
        if (tailleImage == null || perspective == null) {
            return new Point(0, 0);
        }

        double zoom = perspective.getZoom();

        // Calcul de l'offset de l'image (position du coin supérieur gauche de l'image)
        int largeurImage = (int)(tailleImage.width * zoom);
        int hauteurImage = (int)(tailleImage.height * zoom);

        int offsetX = (taillePanel.width - largeurImage) / 2 + (int)perspective.getCentreX();
        int offsetY = (taillePanel.height - hauteurImage) / 2 + (int)perspective.getCentreY();

        // Conversion écran → image
        int imgX = (int)Math.round((pointEcran.x - offsetX) / zoom);
        int imgY = (int)Math.round((pointEcran.y - offsetY) / zoom);

        return new Point(imgX, imgY);
    }

    /**
     * Convertit un point image en coordonnées écran
     * @param pointImage Point en coordonnées image (pixels de l'image)
     * @param perspective La perspective contenant zoom et position
     * @param taillePanel Taille du panel d'affichage
     * @param tailleImage Taille de l'image originale
     * @return Point en coordonnées écran (pixels du panel)
     */
    public static Point imageVersEcran(Point pointImage,
                                        PerspectiveLeaf perspective,
                                        Dimension taillePanel,
                                        Dimension tailleImage) {
        if (tailleImage == null || perspective == null) {
            return new Point(0, 0);
        }

        double zoom = perspective.getZoom();

        // Calcul de l'offset de l'image
        int largeurImage = (int)(tailleImage.width * zoom);
        int hauteurImage = (int)(tailleImage.height * zoom);

        int offsetX = (taillePanel.width - largeurImage) / 2 + (int)perspective.getCentreX();
        int offsetY = (taillePanel.height - hauteurImage) / 2 + (int)perspective.getCentreY();

        // Conversion image → écran
        int ecranX = (int)Math.round(pointImage.x * zoom + offsetX);
        int ecranY = (int)Math.round(pointImage.y * zoom + offsetY);

        return new Point(ecranX, ecranY);
    }

    /**
     * Calcule l'offset X de l'image dans le panel
     */
    public static int getOffsetX(PerspectiveLeaf perspective,
                                   Dimension taillePanel,
                                   Dimension tailleImage) {
        if (tailleImage == null || perspective == null) return 0;

        double zoom = perspective.getZoom();
        int largeurImage = (int)(tailleImage.width * zoom);
        return (taillePanel.width - largeurImage) / 2 + (int)perspective.getCentreX();
    }

    /**
     * Calcule l'offset Y de l'image dans le panel
     */
    public static int getOffsetY(PerspectiveLeaf perspective,
                                   Dimension taillePanel,
                                   Dimension tailleImage) {
        if (tailleImage == null || perspective == null) return 0;

        double zoom = perspective.getZoom();
        int hauteurImage = (int)(tailleImage.height * zoom);
        return (taillePanel.height - hauteurImage) / 2 + (int)perspective.getCentreY();
    }
}
