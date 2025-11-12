package xvision.model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.ByteArrayOutputStream;

/**
 * Classe existante pour gérer les images JPEG
 *
 * Cette classe sera adaptée par AdapterJPEG pour implémenter l'interface ImageTarget.
 * Elle possède sa propre API spécifique au format JPEG.
 *
 * Principe GRASP: Information Expert - Cette classe sait comment gérer
 * les spécificités du format JPEG (compression, qualité, etc.)
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class ImageJPEG {

    private BufferedImage imageJPEG;
    private String cheminFichier;
    private float qualiteCompression = 0.85f; // Qualité par défaut

    /**
     * Charge une image JPEG depuis un fichier
     * Méthode spécifique au format JPEG
     *
     * @param chemin Le chemin vers le fichier JPEG
     */
    public void chargerJPEG(String chemin) {
        try {
            this.cheminFichier = chemin;
            this.imageJPEG = ImageIO.read(new File(chemin));
            System.out.println("[ImageJPEG] Image JPEG chargée: " + chemin);
            System.out.println("[ImageJPEG] Qualité de compression: " + (qualiteCompression * 100) + "%");
        } catch (Exception e) {
            System.err.println("[ImageJPEG] Erreur lors du chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Affiche l'image JPEG
     * Méthode spécifique au format JPEG
     */
    public void afficherJPEG() {
        if (imageJPEG != null) {
            System.out.println("[ImageJPEG] Affichage JPEG - Dimensions: " +
                    imageJPEG.getWidth() + "x" + imageJPEG.getHeight());
            System.out.println("[ImageJPEG] Format compressé avec perte");
        } else {
            System.out.println("[ImageJPEG] Aucune image chargée");
        }
    }

    /**
     * Retourne la largeur de l'image JPEG
     */
    public int getLargeurJPEG() {
        return imageJPEG != null ? imageJPEG.getWidth() : 0;
    }

    /**
     * Retourne la hauteur de l'image JPEG
     */
    public int getHauteurJPEG() {
        return imageJPEG != null ? imageJPEG.getHeight() : 0;
    }

    /**
     * Retourne les données brutes de l'image JPEG
     */
    public byte[] getDonneesJPEG() {
        if (imageJPEG == null) return new byte[0];

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imageJPEG, "jpg", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Définit la qualité de compression pour les futures opérations
     *
     * @param qualite La qualité entre 0.0 et 1.0
     */
    public void setQualiteCompression(float qualite) {
        this.qualiteCompression = Math.max(0.0f, Math.min(1.0f, qualite));
    }

    /**
     * Retourne le BufferedImage pour traitement avancé
     */
    public BufferedImage getBufferedImage() {
        return imageJPEG;
    }
}
