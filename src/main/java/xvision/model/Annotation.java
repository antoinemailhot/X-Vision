package xvision.model;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Interface commune pour toutes les annotations
 * Patron Composite (GoF): Permet de traiter uniformément annotations simples et groupes
 * Principe GRASP: Polymorphisme - interface commune pour différents types
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public interface Annotation {

    /**
     * Dessine l'annotation sur le contexte graphique
     * @param g2d Le contexte graphique
     */
    void dessiner(Graphics2D g2d);

    /**
     * Retourne le rectangle englobant de l'annotation
     * @return Rectangle englobant
     */
    Rectangle getBounds();
}
