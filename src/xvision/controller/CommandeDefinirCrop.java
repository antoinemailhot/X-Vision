package xvision.controller;

import java.awt.Rectangle;
import xvision.model.GestionnaireCrop;

/**
 * Commande pour définir une zone de crop
 * Patron Command (GoF): Encapsule l'action avec Undo/Redo
 * Principe GRASP: Low Coupling - dépend d'interfaces
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class CommandeDefinirCrop implements Commande {

    private GestionnaireCrop gestionnaire;
    private Rectangle nouveauCrop;
    private Rectangle ancienCrop;

    /**
     * Constructeur
     * @param gestionnaire Le gestionnaire de crop
     * @param crop Le nouveau rectangle de crop
     */
    public CommandeDefinirCrop(GestionnaireCrop gestionnaire, Rectangle crop) {
        this.gestionnaire = gestionnaire;
        this.nouveauCrop = crop;
        this.ancienCrop = gestionnaire.getZoneCrop();
    }

    /**
     * Exécute la commande
     */
    @Override
    public void executer() {
        gestionnaire.definirCrop(nouveauCrop);
        System.out.println("[CommandeDefinirCrop] Crop défini");
    }

    /**
     * Annule la commande
     */
    @Override
    public void annuler() {
        gestionnaire.definirCrop(ancienCrop);
        System.out.println("[CommandeDefinirCrop] Crop annulé (undo)");
    }
}
