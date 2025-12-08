package xvision.controller;

import javax.swing.JPanel;
import java.awt.Color;

/**
 * Gestionnaire centralisé pour les modes d'interaction (Outils).
 * <p>
 * Cette classe agit comme un <b>Médiateur</b> entre l'interface utilisateur (boutons de la barre d'outils)
 * et les stratégies d'interaction (souris). Elle assure qu'un seul mode est actif à la fois
 * et gère les transitions proprement (désactivation de l'ancien mode, activation du nouveau).
 * </p>
 * <h3>Patrons de conception :</h3>
 * <ul>
 *   <li><b>State / Strategy (GoF)</b> : Le comportement de la souris change dynamiquement selon le mode actif ({@link ModeInteraction}).</li>
 *   <li><b>Mediator (GoF)</b> : Centralise la logique de changement d'outil.</li>
 * </ul>
 */
public class GestionnaireOutils {

    private ModeInteraction modeActuel;
    private final JPanel panel;
    private ContexteMode contexte;

    // Instances des modes spécifiques
    private final ModeNavigation modeNavigation;
    private ModeLigne modeLigne;
    private ModeCrop modeCrop;
    
    // Listener pour notifier l'UI des changements de mode
    private Runnable onModeChange;

    /**
     * Constructeur du gestionnaire d'outils.
     * @param panel Le panneau Swing sur lequel les écouteurs de souris seront attachés.
     * @param contexte Le contexte partagé (Modèle, Contrôleur) nécessaire aux outils.
     */
    public GestionnaireOutils(JPanel panel, ContexteMode contexte) {
        this.panel = panel;
        this.contexte = contexte;

        // Le mode navigation est toujours disponible
        this.modeNavigation = new ModeNavigation();

        // Activation par défaut
        activerModeNavigation();
    }

    /**
     * Active le mode de navigation (Zoom/Pan).
     * C'est le mode par défaut.
     */
    public void activerModeNavigation() {
        changerMode(modeNavigation);
    }

    /**
     * Active le mode de dessin de lignes (Annotations).
     * @param couleur La couleur de la ligne à tracer.
     * @param epaisseur L'épaisseur du trait.
     */
    public void activerModeLigne(Color couleur, int epaisseur) {
        OutilLigne outil = new OutilLigne(couleur, epaisseur);
        modeLigne = new ModeLigne(outil);
        changerMode(modeLigne);
    }

    /**
     * Active le mode de définition de zone de recadrage (Crop).
     */
    public void activerModeCrop() {
        OutilCrop outil = new OutilCrop();
        modeCrop = new ModeCrop(outil, this);
        changerMode(modeCrop);
    }

    /**
     * Définit un callback à exécuter lors d'un changement de mode.
     * @param callback Runnable à exécuter.
     */
    public void setOnModeChange(Runnable callback) {
        this.onModeChange = callback;
    }

    /**
     * Méthode interne pour effectuer la transition entre deux modes.
     * Désactive proprement l'ancien mode (nettoyage des listeners) avant d'activer le nouveau.
     * @param nouveauMode Le mode à activer.
     */
    private void changerMode(ModeInteraction nouveauMode) {
        if (modeActuel != null) {
            modeActuel.desactiver(panel);
        }

        modeActuel = nouveauMode;
        if (modeActuel != null) {
            modeActuel.activer(panel, contexte);
        }
        
        if (onModeChange != null) {
            onModeChange.run();
        }
    }

    /**
     * Retourne le mode d'interaction actuellement actif.
     * @return L'objet ModeInteraction actif.
     */
    public ModeInteraction getModeActuel() {
        return modeActuel;
    }

    /**
     * Retourne l'instance du mode ligne (si actif ou récemment utilisé).
     * Utile pour la vue afin de récupérer l'aperçu (preview) du tracé.
     * @return Le mode ligne ou null.
     */
    public ModeLigne getModeLigne() {
        return modeLigne;
    }

    /**
     * Retourne l'instance du mode crop (si actif ou récemment utilisé).
     * Utile pour la vue afin de récupérer l'aperçu du rectangle de sélection.
     * @return Le mode crop ou null.
     */
    public ModeCrop getModeCrop() {
        return modeCrop;
    }

    /**
     * Met à jour le contexte d'exécution (ex: après le chargement d'une nouvelle perspective).
     * Redémarre le mode actuel pour qu'il prenne en compte les nouvelles références (Modèle/Image).
     * @param contexte Le nouveau contexte.
     */
    public void setContexte(ContexteMode contexte) {
        this.contexte = contexte;
        // Si un mode est actif, on le redémarre avec le nouveau contexte
        if (modeActuel != null) {
            modeActuel.desactiver(panel);
            modeActuel.activer(panel, contexte);
        }
    }
}
