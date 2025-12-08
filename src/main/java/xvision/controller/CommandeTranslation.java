package xvision.controller;

import xvision.model.PerspectiveLeaf;

/**
 * Commande pour translater (déplacer) une perspective (Patron Command - GoF)
 * Supporte l'annulation (Undo/Redo)
 * Principe GRASP: Low Coupling - Modifie via les accesseurs
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class CommandeTranslation implements Commande {
    private PerspectiveLeaf perspective;
    private double ancienX, ancienY;
    private double nouveauX, nouveauY;
    private boolean executed = false; // Flag pour tracker si la commande a été exécutée

    /**
     * Constructeur de la commande de translation
     * @param p La perspective à déplacer
     * @param x La nouvelle position X
     * @param y La nouvelle position Y
     */
    public CommandeTranslation(PerspectiveLeaf p, double x, double y) {
        this.perspective = p;
        this.nouveauX = x;
        this.nouveauY = y;
    }

    /**
     * Exécute la commande: applique la nouvelle position
     * La notification des observateurs est automatique via setCentre()
     */
    @Override
    public void executer() {
        ancienX = perspective.getCentreX();
        ancienY = perspective.getCentreY();
        perspective.setCentre(nouveauX, nouveauY);
        executed = true; // Marquer comme exécutée
        // Note: setCentre() appelle automatiquement notifier() (patron Observer)
    }

    /**
     * Annule la commande: restaure l'ancienne position
     * Ne fait rien si la commande n'a pas été exécutée (safe)
     */
    @Override
    public void annuler() {
        if (executed) {
            perspective.setCentre(ancienX, ancienY);
            // Note: setCentre() appelle automatiquement notifier() (patron Observer)
        }
        // Si pas exécutée, ne rien faire (comportement safe)
    }
}
