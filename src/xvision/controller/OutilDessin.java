package xvision.controller;

import java.awt.event.MouseEvent;
import xvision.model.Annotation;

/**
 * Interface pour les outils de dessin
 * Patron Strategy (GoF): Algorithme interchangeable pour différents outils
 * Principe GRASP: Polymorphisme - interface commune pour tous les outils
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public interface OutilDessin {

    /**
     * Gère l'événement mousePressed
     * @param e L'événement souris
     */
    void mousePressed(MouseEvent e);

    /**
     * Gère l'événement mouseDragged
     * @param e L'événement souris
     */
    void mouseDragged(MouseEvent e);

    /**
     * Gère l'événement mouseReleased
     * @param e L'événement souris
     */
    void mouseReleased(MouseEvent e);

    /**
     * Retourne l'annotation en cours de création (preview)
     * @return L'annotation preview ou null
     */
    Annotation getAnnotationPreview();

    /**
     * Retourne l'annotation finale après mouseReleased
     * @return L'annotation finale ou null
     */
    Annotation getAnnotationFinale();
}
