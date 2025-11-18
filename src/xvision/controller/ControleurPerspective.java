package xvision.controller;

import xvision.model.*;

/**
 * Contrôleur principal pour les perspectives (Principe GRASP: Controller)
 * Gère les actions utilisateur et délègue aux commandes appropriées.
 * Patron Command : toute modification passe par une commande (Undo/Redo).
 */
public class ControleurPerspective {

    private GestionnaireDePerspectives gestionnaire;
    private GestionnaireCommandes gestionnaireCommandes;

    /**
     * Constructeur
     */
    public ControleurPerspective(GestionnaireDePerspectives gestionnaire) {
        this.gestionnaire = gestionnaire;
        this.gestionnaireCommandes = GestionnaireCommandes.getInstance();
    }

    /**
     * Crée une perspective simple.
     */
    public PerspectiveLeaf creerPerspective(double zoom, double x, double y) {
        return gestionnaire.ajouterPerspectiveSimple(zoom, x, y);
    }

    /**
     * Modifie le zoom via une commande (Undo/Redo).
     */
    public void zoomer(PerspectiveLeaf perspective, double nouveauZoom) {
        Commande cmd = new CommandeZoom(perspective, nouveauZoom);
        gestionnaireCommandes.executerCommande(cmd);
    }

    /**
     * Applique un facteur multiplicatif au zoom.
     */
    public void zoomerAvecFacteur(PerspectiveLeaf perspective, double facteurMultiplicatif) {
        double zoomActuel = perspective.getZoom();
        double nouveauZoom = zoomActuel * facteurMultiplicatif;

        nouveauZoom = Math.max(0.1, Math.min(nouveauZoom, 10.0));

        zoomer(perspective, nouveauZoom);
    }

    /**
     * Déplace la perspective vers une nouvelle position absolue.
     */
    public void deplacer(PerspectiveLeaf perspective, double dx, double dy) {
        double newX = perspective.getCentreX() + dx;
        double newY = perspective.getCentreY() + dy;

        Commande cmd = new CommandeTranslation(perspective, newX, newY);
        gestionnaireCommandes.executerCommande(cmd);
    }

    /**
     * Déplacement relatif très fluide (utilisé pendant l’inertie).
     * Sans commande → plus performant, pas de spam d'Undo.
     */
    public void deplacerDelta(PerspectiveLeaf perspective, double dx, double dy) {
        perspective.setCentreX(perspective.getCentreX() + dx);
        perspective.setCentreY(perspective.getCentreY() + dy);
    }

    public void annulerDerniereAction() { gestionnaireCommandes.undo(); }
    public void refaireDerniereAction() { gestionnaireCommandes.redo(); }
    public GestionnaireDePerspectives getGestionnaire() { return gestionnaire; }
}
