package xvision.controller;

import xvision.model.PerspectiveLeaf;
import xvision.model.GestionnaireAnnotations;
import xvision.model.GestionnaireCrop;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * Objet Contexte regroupant les dépendances nécessaires aux modes d'interaction.
 * <p>
 * Cette classe agit comme un conteneur (DTO - Data Transfer Object) pour passer
 * l'ensemble des références (Modèle, Contrôleur, Vue) aux différentes stratégies d'interaction ({@link ModeInteraction}).
 * Cela évite de multiplier les paramètres dans les méthodes d'activation des outils.
 * </p>
 * <h3>Rôle :</h3>
 * <ul>
 *   <li><b>Information Expert (GRASP)</b> : Centralise l'accès aux objets métier pour les outils.</li>
 *   <li><b>Context (Strategy Pattern)</b> : Fournit l'environnement d'exécution aux stratégies concrètes.</li>
 * </ul>
 */
public class ContexteMode {

    private final PerspectiveLeaf perspective;
    private final ControleurPerspective controleur;
    private final GestionnaireAnnotations gestionnaireAnnotations;
    private final GestionnaireCrop gestionnaireCrop;
    private BufferedImage imageChargee;
    private JPanel vueAnnotations;

    /**
     * Constructeur du contexte.
     * @param perspective La perspective active (Modèle).
     * @param controleur Le contrôleur principal pour effectuer des actions.
     * @param gestionnaireAnnotations Le gestionnaire des annotations (Modèle).
     * @param gestionnaireCrop Le gestionnaire de recadrage (Modèle).
     */
    public ContexteMode(PerspectiveLeaf perspective,
                        ControleurPerspective controleur,
                        GestionnaireAnnotations gestionnaireAnnotations,
                        GestionnaireCrop gestionnaireCrop) {
        this.perspective = perspective;
        this.controleur = controleur;
        this.gestionnaireAnnotations = gestionnaireAnnotations;
        this.gestionnaireCrop = gestionnaireCrop;
    }

    /**
     * Retourne la perspective active.
     * @return L'objet PerspectiveLeaf.
     */
    public PerspectiveLeaf getPerspective() {
        return perspective;
    }

    /**
     * Retourne le contrôleur principal.
     * @return L'objet ControleurPerspective.
     */
    public ControleurPerspective getControleur() {
        return controleur;
    }

    /**
     * Retourne le gestionnaire d'annotations.
     * @return L'objet GestionnaireAnnotations.
     */
    public GestionnaireAnnotations getGestionnaireAnnotations() {
        return gestionnaireAnnotations;
    }

    /**
     * Retourne le gestionnaire de crop.
     * @return L'objet GestionnaireCrop.
     */
    public GestionnaireCrop getGestionnaireCrop() {
        return gestionnaireCrop;
    }

    /**
     * Définit l'image source chargée en mémoire.
     * @param image L'image BufferedImage.
     */
    public void setImageChargee(BufferedImage image) {
        this.imageChargee = image;
    }

    /**
     * Retourne l'image source.
     * @return L'image BufferedImage.
     */
    public BufferedImage getImageChargee() {
        return imageChargee;
    }

    /**
     * Définit la référence vers la vue des annotations (pour le rafraîchissement).
     * @param vue Le panneau Swing affichant les annotations.
     */
    public void setVueAnnotations(JPanel vue) {
        this.vueAnnotations = vue;
    }

    /**
     * Retourne la vue des annotations.
     * @return Le panneau Swing.
     */
    public JPanel getVueAnnotations() {
        return vueAnnotations;
    }
}
