package xvision.controller;

import java.awt.*;
import java.awt.event.MouseEvent;
import xvision.model.Annotation;

/**
 * Outil pour définir un rectangle de crop
 * Patron Strategy (GoF): Implémentation concrète de OutilDessin
 * Principe GRASP: Information Expert - sait comment créer un rectangle
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class OutilCrop implements OutilDessin {

    private Point pointDebut;
    private Point pointCourant;
    private Rectangle rectangleFinal;

    /**
     * Constructeur
     */
    public OutilCrop() {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pointDebut = e.getPoint();
        pointCourant = e.getPoint();
        rectangleFinal = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        pointCourant = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (pointDebut != null) {
            pointCourant = e.getPoint();

            int x = Math.min(pointDebut.x, pointCourant.x);
            int y = Math.min(pointDebut.y, pointCourant.y);
            int w = Math.abs(pointCourant.x - pointDebut.x);
            int h = Math.abs(pointCourant.y - pointDebut.y);

            rectangleFinal = new Rectangle(x, y, w, h);
            pointDebut = null;
            pointCourant = null;
        }
    }

    @Override
    public Annotation getAnnotationPreview() {
        return null;
    }

    @Override
    public Annotation getAnnotationFinale() {
        return null;
    }

    /**
     * Retourne le rectangle en cours de création (preview)
     * @return Rectangle preview ou null
     */
    public Rectangle getRectanglePreview() {
        if (pointDebut != null && pointCourant != null) {
            int x = Math.min(pointDebut.x, pointCourant.x);
            int y = Math.min(pointDebut.y, pointCourant.y);
            int w = Math.abs(pointCourant.x - pointDebut.x);
            int h = Math.abs(pointCourant.y - pointDebut.y);
            return new Rectangle(x, y, w, h);
        }
        return null;
    }

    /**
     * Retourne le rectangle final après mouseReleased
     * @return Rectangle final ou null
     */
    public Rectangle getRectangleFinal() {
        return rectangleFinal;
    }
}
