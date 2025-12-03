package xvision.controller;

import xvision.model.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ArrayList;
import java.awt.Rectangle;

/**
 * Contrôleur principal pour la gestion des perspectives.
 * <p>
 * Cette classe agit comme un médiateur entre la Vue et le Modèle (Patron MVC/Mediator).
 * Elle expose des méthodes de haut niveau pour manipuler les perspectives (zoom, translation, crop)
 * et délègue la persistance au {@link GestionnaireDePerspectives}.
 * </p>
 * <p>
 * Elle interagit également avec le {@link GestionnaireCommandes} pour les opérations d'annulation/rétablissement,
 * bien que certaines méthodes modifient directement le modèle (laissant la création de Commande à l'appelant ou au contexte).
 * </p>
 */
public class ControleurPerspective {

    private GestionnaireDePerspectives gestionnaire;
    private GestionnaireCommandes gestionnaireCommandes;

    /**
     * Constructeur du contrôleur.
     * @param gestionnaire Le gestionnaire de persistance et de collection de perspectives.
     */
    public ControleurPerspective(GestionnaireDePerspectives gestionnaire) {
        this.gestionnaire = gestionnaire;
        this.gestionnaireCommandes = GestionnaireCommandes.getInstance();
    }

    /**
     * Crée une nouvelle perspective simple (Leaf).
     * @param zoom Niveau de zoom initial.
     * @param x Position X initiale.
     * @param y Position Y initiale.
     * @return La nouvelle perspective créée.
     */
    public PerspectiveLeaf creerPerspective(double zoom, double x, double y) {
        return gestionnaire.ajouterPerspectiveSimple(zoom, x, y);
    }

    /**
     * Applique un niveau de zoom absolu à une perspective.
     * <p>
     * Note : Cette méthode modifie directement le modèle. Pour supporter l'annulation,
     * l'appelant doit encapsuler cet appel dans une {@link CommandeZoom}.
     * </p>
     * @param perspective La perspective cible.
     * @param nouveauZoom Le nouveau niveau de zoom.
     */
    public void zoomer(PerspectiveLeaf perspective, double nouveauZoom) {
        // Modification directe pour utiliser le Memento interne de la perspective
        perspective.setZoom(nouveauZoom);
    }

    /**
     * Applique un zoom relatif (multiplicatif) à une perspective.
     * @param perspective La perspective cible.
     * @param facteurMultiplicatif Facteur de zoom (ex: 1.1 pour +10%, 0.9 pour -10%).
     */
    public void zoomerAvecFacteur(PerspectiveLeaf perspective, double facteurMultiplicatif) {
        double zoomActuel = perspective.getZoom();
        double nouveauZoom = zoomActuel * facteurMultiplicatif;
        // Bornage du zoom entre 0.1x et 10.0x pour éviter les valeurs extrêmes
        nouveauZoom = Math.max(0.1, Math.min(nouveauZoom, 10.0));
        zoomer(perspective, nouveauZoom);
    }

    /**
     * Déplace la perspective d'un décalage (delta).
     * @param perspective La perspective cible.
     * @param dx Décalage en X.
     * @param dy Décalage en Y.
     */
    public void deplacer(PerspectiveLeaf perspective, double dx, double dy) {
        double newX = perspective.getCentreX() + dx;
        double newY = perspective.getCentreY() + dy;
        // Modification directe pour utiliser le Memento interne
        perspective.setCentre(newX, newY);
    }

    /**
     * Définit la zone de recadrage (Crop) pour une perspective.
     * Met à jour à la fois le gestionnaire de crop (logique métier) et la perspective (modèle).
     * @param perspective La perspective cible.
     * @param gestionnaireCrop Le gestionnaire de crop associé.
     * @param crop La zone rectangulaire de découpe.
     */
    public void definirCrop(PerspectiveLeaf perspective, GestionnaireCrop gestionnaireCrop, Rectangle crop) {
        // Modification directe
        if (gestionnaireCrop != null) gestionnaireCrop.definirCrop(crop);
        if (perspective != null) perspective.setCrop(crop);
    }

    /**
     * Déplace la perspective (méthode alternative utilisant setCentreX/Y).
     * @param perspective La perspective cible.
     * @param dx Décalage X.
     * @param dy Décalage Y.
     */
    public void deplacerDelta(PerspectiveLeaf perspective, double dx, double dy) {
        perspective.setCentreX(perspective.getCentreX() + dx);
        perspective.setCentreY(perspective.getCentreY() + dy);
    }

    /**
     * Annule la dernière commande effectuée via le {@link GestionnaireCommandes}.
     */
    public void annulerDerniereAction() { gestionnaireCommandes.undo(); }

    /**
     * Rétablit la dernière commande annulée via le {@link GestionnaireCommandes}.
     */
    public void refaireDerniereAction() { gestionnaireCommandes.redo(); }

    /**
     * Retourne le gestionnaire de perspectives associé.
     * @return Le gestionnaire de perspectives.
     */
    public GestionnaireDePerspectives getGestionnaire() { return gestionnaire; }

    // -------------------- SAUVEGARDER / CHARGER PERSPECTIVE --------------------

    /**
     * Sauvegarde la perspective actuellement active (la dernière ajoutée/sélectionnée) dans un fichier JSON.
     * @param cheminFichier Chemin absolu du fichier de sauvegarde.
     */
    public void sauvegarderPerspectiveActuelle(String cheminFichier) {
        PerspectiveLeaf p = gestionnaire.getDernierePerspective();
        if (p == null) return;
        
        // Sauvegarde directe via le gestionnaire
        gestionnaire.sauvegarderPerspective(p, cheminFichier);
    }

    /**
     * Charge une perspective depuis un fichier JSON.
     * @param cheminFichier Chemin absolu du fichier à charger.
     * @return La perspective chargée (ou null en cas d'échec).
     */
    public PerspectiveLeaf chargerPerspective(String cheminFichier) {
        gestionnaire.chargerPerspective(cheminFichier);
        return gestionnaire.getDernierePerspective();
    }
}
