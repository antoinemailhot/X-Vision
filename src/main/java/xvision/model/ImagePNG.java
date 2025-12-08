package xvision.model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.ByteArrayOutputStream;

/**
 * Classe existante pour gérer les images PNG
 *
 * Cette classe sera adaptée par AdapterPNG pour implémenter l'interface ImageTarget.
 * Elle possède sa propre API spécifique au format PNG.
 *
 * Principe GRASP: Information Expert - Cette classe sait comment gérer
 * les spécificités du format PNG.
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class ImagePNG {

    private BufferedImage imagePNG;
    /**
     * Charge une image PNG depuis un fichier
     * Méthode spécifique au format PNG
     *
     * @param chemin Le chemin vers le fichier PNG
     */
    public void chargerPNG(String chemin) {
        try {
            this.imagePNG = ImageIO.read(new File(chemin));
            System.out.println("[ImagePNG] Image PNG chargée: " + chemin);
        } catch (Exception e) {
            System.err.println("[ImagePNG] Erreur lors du chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Affiche l'image PNG
     * Méthode spécifique au format PNG
     */
    public void afficherPNG() {
        if (imagePNG != null) {
            System.out.println("[ImagePNG] Affichage PNG - Dimensions: " +
                    imagePNG.getWidth() + "x" + imagePNG.getHeight());
        } else {
            System.out.println("[ImagePNG] Aucune image chargée");
        }
    }

    /**
     * Retourne la largeur de l'image PNG
     */
    public int getLargeurPNG() {
        return imagePNG != null ? imagePNG.getWidth() : 0;
    }

    /**
     * Retourne la hauteur de l'image PNG
     */
    public int getHauteurPNG() {
        return imagePNG != null ? imagePNG.getHeight() : 0;
    }

    /**
     * Retourne les données brutes de l'image PNG
     */
    public byte[] getDonneesPNG() {
        if (imagePNG == null) return new byte[0];

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imagePNG, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Retourne le BufferedImage pour traitement avancé
     */
    public BufferedImage getBufferedImage() {
        return imagePNG;
    }
}
