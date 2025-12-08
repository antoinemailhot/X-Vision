package xvision.controller;

import xvision.model.PerspectiveLeaf;

/**
 * Commande pour modifier le zoom d'une perspective (Patron Command - GoF)
 * Supporte l'annulation (Undo/Redo)
 * Principe GRASP: Low Coupling - Modifie via les accesseurs, pas directement
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class CommandeZoom implements Commande {
    private PerspectiveLeaf perspective;
    private double ancienZoom;
    private double nouveauZoom;
    private boolean executed = false; // Flag pour tracker si la commande a été exécutée

    /**
     * Constructeur de la commande de zoom
     * @param p La perspective à modifier
     * @param zoom Le nouveau niveau de zoom
     */
    public CommandeZoom(PerspectiveLeaf p, double zoom) {
        this.perspective = p;
        this.nouveauZoom = zoom;
    }

    /**
     * Exécute la commande: applique le nouveau zoom
     * La notification des observateurs est automatique via setZoom()
     */
    @Override
    public void executer() {
        ancienZoom = perspective.getZoom();
        perspective.setZoom(nouveauZoom);
        executed = true; // Marquer comme exécutée
        // Note: setZoom() appelle automatiquement notifier() (patron Observer)
    }

    /**
     * Annule la commande: restaure l'ancien zoom
     * Ne fait rien si la commande n'a pas été exécutée (safe)
     */
    @Override
    public void annuler() {
        if (executed) {
            perspective.setZoom(ancienZoom);
            // Note: setZoom() appelle automatiquement notifier() (patron Observer)
        }
        // Si pas exécutée, ne rien faire (comportement safe)
    }
}
