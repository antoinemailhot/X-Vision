package xvision.controller;

import xvision.model.Annotation;
import xvision.model.GestionnaireAnnotations;

/**
 * Commande pour ajouter une annotation
 * Patron Command (GoF): Encapsule l'action d'ajout avec Undo/Redo
 * Principe GRASP: Low Coupling - dépend d'interfaces, pas d'implémentations
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class CommandeAjouterAnnotation implements Commande {

    private GestionnaireAnnotations gestionnaire;
    private Annotation annotation;

    /**
     * Constructeur
     * @param gestionnaire Le gestionnaire d'annotations
     * @param annotation L'annotation à ajouter
     */
    public CommandeAjouterAnnotation(GestionnaireAnnotations gestionnaire, Annotation annotation) {
        this.gestionnaire = gestionnaire;
        this.annotation = annotation;
    }

    /**
     * Exécute la commande - ajoute l'annotation
     */
    @Override
    public void executer() {
        gestionnaire.ajouterAnnotation(annotation);
        System.out.println("[CommandeAjouterAnnotation] Annotation ajoutée");
    }

    /**
     * Annule la commande - retire l'annotation
     */
    @Override
    public void annuler() {
        gestionnaire.supprimerAnnotation(annotation);
        System.out.println("[CommandeAjouterAnnotation] Annotation retirée (undo)");
    }
}
