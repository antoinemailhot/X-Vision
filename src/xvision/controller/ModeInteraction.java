package xvision.controller;

import javax.swing.JPanel;
import java.awt.Cursor;

/**
 * Interface pour les modes d'interaction avec la vue
 * Patron State (GoF): Encapsule le comportement d'interaction pour chaque mode
 * Principe GRASP: Polymorphisme - interface commune pour tous les modes
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public interface ModeInteraction {

    /**
     * Active ce mode sur le panel donné
     * @param panel Le panel sur lequel activer le mode
     * @param context Le contexte contenant les dépendances nécessaires
     */
    void activer(JPanel panel, ContexteMode context);

    /**
     * Désactive ce mode du panel donné
     * @param panel Le panel duquel désactiver le mode
     */
    void desactiver(JPanel panel);

    /**
     * Retourne le curseur associé à ce mode
     * @return Le curseur à afficher
     */
    Cursor getCurseur();
}
