package xvision.controller;

import java.awt.Rectangle;
import xvision.model.GestionnaireCrop;
import xvision.model.PerspectiveLeaf;

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
    private PerspectiveLeaf perspective;
    private Rectangle nouveauCrop;
    private Rectangle ancienCrop;

    /**
     * Constructeur
     * @param gestionnaire Le gestionnaire de crop (pour l'affichage)
     * @param perspective La perspective à modifier (pour la persistance)
     * @param crop Le nouveau rectangle de crop
     */
    public CommandeDefinirCrop(GestionnaireCrop gestionnaire, PerspectiveLeaf perspective, Rectangle crop) {
        this.gestionnaire = gestionnaire;
        this.perspective = perspective;
        this.nouveauCrop = crop;
        this.ancienCrop = gestionnaire.getZoneCrop();
    }

    /**
     * Exécute la commande
     */
    @Override
    public void executer() {
        gestionnaire.definirCrop(nouveauCrop);
        if (perspective != null) {
            perspective.setCrop(nouveauCrop);
        }
        System.out.println("[CommandeDefinirCrop] Crop défini");
    }

    /**
     * Annule la commande
     */
    @Override
    public void annuler() {
        gestionnaire.definirCrop(ancienCrop);
        if (perspective != null) {
            perspective.setCrop(ancienCrop);
        }
        System.out.println("[CommandeDefinirCrop] Crop annulé (undo)");
    }
}
