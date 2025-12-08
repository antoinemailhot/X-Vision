package xvision.model;

import java.awt.*;

/**
 * Annotation représentant une ligne
 * Patron Composite (GoF): Leaf - élément terminal
 * Principe GRASP: Information Expert - connaît ses propres coordonnées et style
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class AnnotationLigne implements Annotation {

    private Point debut;
    private Point fin;
    private Color couleur;
    private int epaisseur;

    /**
     * Constructeur
     * @param debut Point de départ
     * @param fin Point d'arrivée
     * @param couleur Couleur de la ligne
     * @param epaisseur Épaisseur en pixels
     */
    public AnnotationLigne(Point debut, Point fin, Color couleur, int epaisseur) {
        this.debut = debut;
        this.fin = fin;
        this.couleur = couleur;
        this.epaisseur = epaisseur;
    }

    /**
     * Dessine la ligne
     */
    @Override
    public void dessiner(Graphics2D g2d) {
        g2d.setColor(couleur);
        g2d.setStroke(new BasicStroke(epaisseur, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(debut.x, debut.y, fin.x, fin.y);
    }

    /**
     * Retourne le rectangle englobant
     */
    @Override
    public Rectangle getBounds() {
        int x = Math.min(debut.x, fin.x);
        int y = Math.min(debut.y, fin.y);
        int w = Math.abs(fin.x - debut.x);
        int h = Math.abs(fin.y - debut.y);
        return new Rectangle(x, y, w, h);
    }

    public Point getDebut() { return debut; }
    public Point getFin() { return fin; }
    public Color getCouleur() { return couleur; }
    public int getEpaisseur() { return epaisseur; }
}
