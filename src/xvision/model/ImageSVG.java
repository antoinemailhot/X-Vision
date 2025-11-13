package xvision.model;

import java.io.File;
import java.nio.file.Files;

/**
 * Classe existante pour gérer les images SVG (Scalable Vector Graphics)
 *
 * Cette classe sera adaptée par AdapterSVG pour implémenter l'interface ImageTarget.
 * Elle possède sa propre API spécifique au format SVG vectoriel.
 *
 * Note: Le format SVG est vectoriel, contrairement à PNG/JPEG qui sont matriciels.
 * Cette implémentation simule le chargement SVG pour les besoins du projet.
 *
 * Principe GRASP: Information Expert - Cette classe sait comment gérer
 * les spécificités du format SVG (vectoriel, redimensionnable sans perte)
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class ImageSVG {

    private String contenuSVG;
    private String cheminFichier;
    private int largeurRendu = 800;  // Largeur par défaut pour le rendu
    private int hauteurRendu = 600;  // Hauteur par défaut pour le rendu

    /**
     * Charge une image SVG depuis un fichier
     * Méthode spécifique au format SVG
     *
     * @param chemin Le chemin vers le fichier SVG
     */
    public void chargerSVG(String chemin) {
        try {
            this.cheminFichier = chemin;
            File fichier = new File(chemin);
            this.contenuSVG = Files.readString(fichier.toPath());

            // Extraire les dimensions si présentes dans le SVG
            extraireDimensionsSVG();

            System.out.println("[ImageSVG] Image SVG chargée: " + chemin);
            System.out.println("[ImageSVG] Format vectoriel (redimensionnable sans perte)");
        } catch (Exception e) {
            System.err.println("[ImageSVG] Erreur lors du chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Affiche l'image SVG
     * Méthode spécifique au format SVG
     */
    public void afficherSVG() {
        if (contenuSVG != null && !contenuSVG.isEmpty()) {
            System.out.println("[ImageSVG] Affichage SVG - Dimensions de rendu: " +
                    largeurRendu + "x" + hauteurRendu);
            System.out.println("[ImageSVG] Format vectoriel - qualité indépendante de l'échelle");
        } else {
            System.out.println("[ImageSVG] Aucune image chargée");
        }
    }

    /**
     * Retourne la largeur de rendu de l'image SVG
     */
    public int getLargeurSVG() {
        return largeurRendu;
    }

    /**
     * Retourne la hauteur de rendu de l'image SVG
     */
    public int getHauteurSVG() {
        return hauteurRendu;
    }

    /**
     * Retourne les données brutes du SVG (le contenu XML)
     */
    public byte[] getDonneesSVG() {
        if (contenuSVG == null) return new byte[0];
        return contenuSVG.getBytes();
    }

    /**
     * Définit les dimensions de rendu pour le SVG
     *
     * @param largeur La largeur de rendu souhaitée
     * @param hauteur La hauteur de rendu souhaitée
     */
    public void setDimensionsRendu(int largeur, int hauteur) {
        this.largeurRendu = largeur;
        this.hauteurRendu = hauteur;
        System.out.println("[ImageSVG] Dimensions de rendu ajustées: " + largeur + "x" + hauteur);
    }

    /**
     * Extrait les dimensions du contenu SVG si présentes
     */
    private void extraireDimensionsSVG() {
        if (contenuSVG == null) return;

        // Recherche simple des attributs width et height
        try {
            if (contenuSVG.contains("width=")) {
                String widthStr = contenuSVG.split("width=\"")[1].split("\"")[0];
                largeurRendu = Integer.parseInt(widthStr.replaceAll("[^0-9]", ""));
            }
            if (contenuSVG.contains("height=")) {
                String heightStr = contenuSVG.split("height=\"")[1].split("\"")[0];
                hauteurRendu = Integer.parseInt(heightStr.replaceAll("[^0-9]", ""));
            }
        } catch (Exception e) {
            // Si extraction échoue, garder les dimensions par défaut
            System.out.println("[ImageSVG] Utilisation des dimensions par défaut");
        }
    }

    /**
     * Retourne le contenu XML du SVG
     */
    public String getContenuSVG() {
        return contenuSVG;
    }
}
