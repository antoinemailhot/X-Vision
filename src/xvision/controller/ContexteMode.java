package xvision.controller;

import xvision.model.PerspectiveLeaf;
import xvision.model.GestionnaireAnnotations;
import xvision.model.GestionnaireCrop;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * Contexte contenant les dépendances nécessaires pour les modes d'interaction
 * Principe GRASP: Information Expert - centralise les dépendances partagées
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class ContexteMode {

    private final PerspectiveLeaf perspective;
    private final ControleurPerspective controleur;
    private final GestionnaireAnnotations gestionnaireAnnotations;
    private final GestionnaireCrop gestionnaireCrop;
    private BufferedImage imageChargee;
    private JPanel vueAnnotations;

    /**
     * Constructeur
     * @param perspective La perspective active
     * @param controleur Le contrôleur de perspective
     * @param gestionnaireAnnotations Le gestionnaire d'annotations
     * @param gestionnaireCrop Le gestionnaire de crop
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

    public PerspectiveLeaf getPerspective() {
        return perspective;
    }

    public ControleurPerspective getControleur() {
        return controleur;
    }

    public GestionnaireAnnotations getGestionnaireAnnotations() {
        return gestionnaireAnnotations;
    }

    public GestionnaireCrop getGestionnaireCrop() {
        return gestionnaireCrop;
    }

    public BufferedImage getImageChargee() {
        return imageChargee;
    }

    public void setImageChargee(BufferedImage image) {
        this.imageChargee = image;
    }

    public JPanel getVueAnnotations() {
        return vueAnnotations;
    }

    public void setVueAnnotations(JPanel vueAnnotations) {
        this.vueAnnotations = vueAnnotations;
    }
}
