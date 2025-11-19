package xvision.controller;

import javax.swing.JPanel;
import java.awt.Color;

/**
 * Gestionnaire centralisé pour les modes d'interaction
 * Patron Mediator (GoF): Coordonne les transitions entre modes
 * Principe GRASP: Controller - gère les changements de mode
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class GestionnaireOutils {

    private ModeInteraction modeActuel;
    private final JPanel panel;
    private final ContexteMode contexte;

    private final ModeNavigation modeNavigation;
    private ModeLigne modeLigne;
    private ModeCrop modeCrop;

    /**
     * Constructeur
     * @param panel Le panel sur lequel appliquer les modes
     * @param contexte Le contexte contenant les dépendances
     */
    public GestionnaireOutils(JPanel panel, ContexteMode contexte) {
        this.panel = panel;
        this.contexte = contexte;

        this.modeNavigation = new ModeNavigation();

        activerModeNavigation();
    }

    /**
     * Active le mode navigation (par défaut)
     */
    public void activerModeNavigation() {
        changerMode(modeNavigation);
    }

    /**
     * Active le mode de dessin de lignes
     * @param couleur Couleur de la ligne
     * @param epaisseur Épaisseur de la ligne
     */
    public void activerModeLigne(Color couleur, int epaisseur) {
        OutilLigne outil = new OutilLigne(couleur, epaisseur);
        modeLigne = new ModeLigne(outil);
        changerMode(modeLigne);
    }

    /**
     * Active le mode de définition de crop
     */
    public void activerModeCrop() {
        OutilCrop outil = new OutilCrop();
        modeCrop = new ModeCrop(outil);
        changerMode(modeCrop);
    }

    /**
     * Change le mode actuel
     * @param nouveauMode Le nouveau mode à activer
     */
    private void changerMode(ModeInteraction nouveauMode) {
        if (modeActuel != null) {
            modeActuel.desactiver(panel);
        }

        modeActuel = nouveauMode;
        modeActuel.activer(panel, contexte);
    }

    /**
     * Retourne le mode actuel
     * @return Le mode actuel
     */
    public ModeInteraction getModeActuel() {
        return modeActuel;
    }

    /**
     * Retourne le mode ligne actuel (pour accéder à l'outil)
     * @return Le mode ligne ou null
     */
    public ModeLigne getModeLigne() {
        return modeLigne;
    }

    /**
     * Retourne le mode crop actuel (pour accéder à l'outil)
     * @return Le mode crop ou null
     */
    public ModeCrop getModeCrop() {
        return modeCrop;
    }
}
