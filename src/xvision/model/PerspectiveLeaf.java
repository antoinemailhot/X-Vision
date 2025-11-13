package xvision.model;

import java.util.List;

/**
 * Perspective individuelle (feuille) dans l'arbre Composite
 * Hérite de Sujet pour implémenter le patron Observer
 * Principe GRASP: Information Expert - Gère son propre état de visualisation
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class PerspectiveLeaf extends Sujet implements PerspectiveComponent {

    private double niveauZoom;
    private double positionX;
    private double positionY;
    private String cheminImage;

    /**
     * Constructeur d'une perspective feuille
     * @param zoom Niveau de zoom initial
     * @param x Position X du centre
     * @param y Position Y du centre
     */
    public PerspectiveLeaf(double zoom, double x, double y) {
        super();
        this.niveauZoom = zoom;
        this.positionX = x;
        this.positionY = y;
    }

    /**
     * Retourne le niveau de zoom actuel
     * @return Le niveau de zoom
     */
    public double getZoom() {
        return niveauZoom;
    }

    /**
     * Modifie le niveau de zoom et notifie les observateurs
     * @param zoom Le nouveau niveau de zoom
     */
    public void setZoom(double zoom) {
        this.niveauZoom = zoom;
        notifier();
    }

    /**
     * Retourne la position X du centre
     * @return La position X
     */
    public double getCentreX() {
        return positionX;
    }

    /**
     * Retourne la position Y du centre
     * @return La position Y
     */
    public double getCentreY() {
        return positionY;
    }

    /**
     * Modifie le centre de la perspective et notifie les observateurs
     * @param x Nouvelle position X
     * @param y Nouvelle position Y
     */
    public void setCentre(double x, double y) {
        this.positionX = x;
        this.positionY = y;
        notifier();
    }

    /**
     * Retourne le chemin de l'image chargée
     * @return Le chemin de l'image ou null si aucune image n'est chargée
     */
    public String getCheminImage() {
        return cheminImage;
    }

    /**
     * Modifie le chemin de l'image et notifie les observateurs
     * @param cheminImage Le chemin de la nouvelle image
     */
    public void setCheminImage(String cheminImage) {
        this.cheminImage = cheminImage;
        notifier();
    }

    @Override
    public void afficher() {
        System.out.println("Leaf - Zoom: " + niveauZoom + ", Pos=(" + positionX + "," + positionY + ")");
    }

    @Override
    public void ajouter(PerspectiveComponent p) { System.out.println("Impossible d’ajouter à une feuille."); }

    @Override
    public void supprimer(PerspectiveComponent p) { }

    @Override
    public List<PerspectiveComponent> getEnfants() { return List.of(); }
}
