package xvision.controller;

import java.awt.*;
import java.awt.event.MouseEvent;
import xvision.model.Annotation;
import xvision.model.AnnotationLigne;

/**
 * Outil pour dessiner des lignes
 * Patron Strategy (GoF): Implémentation concrète de OutilDessin
 * Principe GRASP: Information Expert - sait comment créer une ligne à partir de points
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class OutilLigne implements OutilDessin {

    private Point pointDebut;
    private Point pointCourant;
    private Color couleur;
    private int epaisseur;
    private AnnotationLigne annotationFinale;

    /**
     * Constructeur
     * @param couleur Couleur de la ligne
     * @param epaisseur Épaisseur en pixels
     */
    public OutilLigne(Color couleur, int epaisseur) {
        this.couleur = couleur;
        this.epaisseur = epaisseur;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pointDebut = e.getPoint();
        pointCourant = e.getPoint();
        annotationFinale = null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        pointCourant = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (pointDebut != null) {
            pointCourant = e.getPoint();
            annotationFinale = new AnnotationLigne(pointDebut, pointCourant, couleur, epaisseur);
            pointDebut = null;
            pointCourant = null;
        }
    }

    @Override
    public Annotation getAnnotationPreview() {
        if (pointDebut != null && pointCourant != null) {
            return new AnnotationLigne(pointDebut, pointCourant, couleur, epaisseur);
        }
        return null;
    }

    @Override
    public Annotation getAnnotationFinale() {
        return annotationFinale;
    }

    public void setCouleur(Color couleur) {
        this.couleur = couleur;
    }

    public void setEpaisseur(int epaisseur) {
        this.epaisseur = epaisseur;
    }
}
