package xvision.model;

import java.awt.Rectangle;

/**
 * Gestionnaire du rectangle de crop
 * Patron Observer (GoF): Sujet - notifie les vues quand le crop change
 * Principe GRASP: Information Expert - gère la zone de crop
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class GestionnaireCrop extends Sujet {

    private Rectangle zoneCrop;

    /**
     * Constructeur
     */
    public GestionnaireCrop() {
        this.zoneCrop = null;
    }

    /**
     * Définit la zone de crop
     * @param rect Le rectangle de crop (null pour désactiver)
     */
    public void definirCrop(Rectangle rect) {
        this.zoneCrop = rect;
        notifier();
        if (rect != null) {
            System.out.println("[GestionnaireCrop] Zone définie: " + rect);
        } else {
            System.out.println("[GestionnaireCrop] Crop désactivé");
        }
    }

    /**
     * Retourne la zone de crop
     * @return Rectangle de crop ou null
     */
    public Rectangle getZoneCrop() {
        return zoneCrop;
    }

    /**
     * Vérifie si un crop est défini
     * @return true si un crop existe
     */
    public boolean hasCrop() {
        return zoneCrop != null;
    }

    /**
     * Efface le crop
     */
    public void effacerCrop() {
        definirCrop(null);
    }
}
