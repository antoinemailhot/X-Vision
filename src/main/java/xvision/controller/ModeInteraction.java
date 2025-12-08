package xvision.controller;

import javax.swing.JPanel;
import java.awt.Cursor;

/**
 * Interface définissant le contrat pour tous les modes d'interaction (Stratégies).
 * <p>
 * Cette interface est au cœur du patron <b>State</b> (ou Strategy) utilisé pour gérer
 * le comportement de la souris sur la vue principale. Chaque implémentation (Navigation, Ligne, Crop)
 * encapsule la logique spécifique aux événements souris (clic, glisser, relâcher).
 * </p>
 * <h3>Patrons de conception :</h3>
 * <ul>
 *   <li><b>Strategy / State (GoF)</b> : Permet de changer le comportement de l'application à l'exécution en intervertissant les objets Mode.</li>
 *   <li><b>Polymorphism (GRASP)</b> : Le gestionnaire d'outils manipule tous les modes de manière uniforme via cette interface.</li>
 * </ul>
 */
public interface ModeInteraction {

    /**
     * Active le mode d'interaction.
     * Cette méthode doit attacher les écouteurs d'événements (MouseListener, MouseMotionListener) au panneau.
     * @param panel Le panneau Swing cible (la vue).
     * @param context Le contexte contenant les références nécessaires (Modèle, Contrôleur) pour agir.
     */
    void activer(JPanel panel, ContexteMode context);

    /**
     * Désactive le mode d'interaction.
     * Cette méthode doit retirer proprement tous les écouteurs attachés lors de l'activation
     * pour éviter les fuites de mémoire ou les comportements parasites.
     * @param panel Le panneau Swing cible.
     */
    void desactiver(JPanel panel);

    /**
     * Retourne le curseur visuel associé à ce mode.
     * Par exemple : une main pour la navigation, une croix pour le dessin.
     * @return L'objet Cursor AWT approprié.
     */
    Cursor getCurseur();
}
