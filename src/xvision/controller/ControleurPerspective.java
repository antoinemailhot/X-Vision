package xvision.controller;

import xvision.model.*;

/**
 * Contrôleur principal pour les perspectives (Principe GRASP: Controller)
 * Gère les actions utilisateur et délègue aux commandes appropriées
 * Principe GRASP: Low Coupling - Utilise le patron Command pour modifier le modèle
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class ControleurPerspective {
    private GestionnaireDePerspectives gestionnaire;
    private GestionnaireCommandes gestionnaireCommandes;

    /**
     * Constructeur du contrôleur
     * @param gestionnaire Le gestionnaire de perspectives
     */
    public ControleurPerspective(GestionnaireDePerspectives gestionnaire) {
        this.gestionnaire = gestionnaire;
        this.gestionnaireCommandes = GestionnaireCommandes.getInstance();
    }

    /**
     * Crée une nouvelle perspective
     * Principe GRASP: Creator - Délègue au gestionnaire
     * @param zoom Niveau de zoom initial
     * @param x Position X initiale
     * @param y Position Y initiale
     * @return La perspective créée
     */
    public PerspectiveLeaf creerPerspective(double zoom, double x, double y) {
        return gestionnaire.ajouterPerspectiveSimple(zoom, x, y);
    }

    /**
     * Modifie le zoom d'une perspective via une commande
     * Patron Command: Toute modification passe par une commande (support Undo/Redo)
     * @param perspective La perspective à modifier
     * @param nouveauZoom Le nouveau niveau de zoom
     */
    public void zoomer(PerspectiveLeaf perspective, double nouveauZoom) {
        Commande cmd = new CommandeZoom(perspective, nouveauZoom);
        gestionnaireCommandes.executerCommande(cmd);
    }

    /**
     * Déplace une perspective via une commande
     * Patron Command: Toute modification passe par une commande (support Undo/Redo)
     * @param perspective La perspective à déplacer
     * @param x Nouvelle position X
     * @param y Nouvelle position Y
     */
    public void deplacer(PerspectiveLeaf perspective, double x, double y) {
        Commande cmd = new CommandeTranslation(perspective, x, y);
        gestionnaireCommandes.executerCommande(cmd);
    }

    /**
     * Annule la dernière commande (Undo)
     */
    public void annulerDerniereAction() {
        gestionnaireCommandes.undo();
    }

    /**
     * Refait la dernière commande annulée (Redo)
     */
    public void refaireDerniereAction() {
        gestionnaireCommandes.redo();
    }

    /**
     * Affiche toutes les perspectives
     * Simple délégation au gestionnaire
     */
    public void afficherToutes() {
        gestionnaire.afficherToutes();
    }

    /**
     * Retourne le gestionnaire de perspectives
     * @return Le gestionnaire de perspectives
     */
    public GestionnaireDePerspectives getGestionnaire() {
        return gestionnaire;
    }
}
