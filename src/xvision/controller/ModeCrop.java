package xvision.controller;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;

/**
 * Implémentation du mode de définition de zone de recadrage (Crop).
 * <p>
 * Ce mode permet à l'utilisateur de définir un rectangle de sélection sur l'image.
 * Une fois le rectangle défini (au relâchement de la souris), le mode bascule automatiquement
 * vers le mode de navigation par défaut.
 * </p>
 * <h3>Responsabilités :</h3>
 * <ul>
 *   <li><b>Conversion de coordonnées</b> : Assure que le rectangle de crop correspond aux pixels réels de l'image.</li>
 *   <li><b>Gestion d'état</b> : Utilise le {@link GestionnaireOutils} pour revenir au mode navigation après l'action.</li>
 *   <li><b>Délégation</b> : Utilise {@link OutilCrop} pour la logique de création du rectangle.</li>
 * </ul>
 */
public class ModeCrop implements ModeInteraction {

    private OutilCrop outil;
    private GestionnaireOutils gestionnaire;
    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;

    /**
     * Constructeur du mode crop.
     * @param outil L'outil crop contenant la logique métier.
     * @param gestionnaire Le gestionnaire permettant de changer de mode une fois l'action terminée.
     */
    public ModeCrop(OutilCrop outil, GestionnaireOutils gestionnaire) {
        this.outil = outil;
        this.gestionnaire = gestionnaire;
    }

    @Override
    public void activer(JPanel panel, ContexteMode context) {
        mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point pointImage = convertirVersImage(e.getPoint(), panel, context);
                MouseEvent eventConverti = new MouseEvent(
                    panel, e.getID(), e.getWhen(), e.getModifiersEx(),
                    pointImage.x, pointImage.y, e.getClickCount(), e.isPopupTrigger()
                );
                outil.mousePressed(eventConverti);
                panel.repaint();
                rafraichirVueAnnotations(context);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point pointImage = convertirVersImage(e.getPoint(), panel, context);
                MouseEvent eventConverti = new MouseEvent(
                    panel, e.getID(), e.getWhen(), e.getModifiersEx(),
                    pointImage.x, pointImage.y, e.getClickCount(), e.isPopupTrigger()
                );
                outil.mouseReleased(eventConverti);

                if (outil.getRectangleFinal() != null) {
                    // Utilisation directe du contrôleur pour éviter la duplication de mementos
                    context.getControleur().definirCrop(
                        context.getPerspective(),
                        context.getGestionnaireCrop(),
                        outil.getRectangleFinal()
                    );
                    
                    // Retour automatique au mode navigation après le crop
                    if (gestionnaire != null) {
                        gestionnaire.activerModeNavigation();
                    }
                }

                panel.repaint();
                rafraichirVueAnnotations(context);
            }
        };

        mouseMotionListener = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point pointImage = convertirVersImage(e.getPoint(), panel, context);
                MouseEvent eventConverti = new MouseEvent(
                    panel, e.getID(), e.getWhen(), e.getModifiersEx(),
                    pointImage.x, pointImage.y, e.getClickCount(), e.isPopupTrigger()
                );
                outil.mouseDragged(eventConverti);
                panel.repaint();
                rafraichirVueAnnotations(context);
            }
        };

        panel.addMouseListener(mouseListener);
        panel.addMouseMotionListener(mouseMotionListener);
        panel.setCursor(getCurseur());
    }

    @Override
    public void desactiver(JPanel panel) {
        if (mouseListener != null) {
            panel.removeMouseListener(mouseListener);
            mouseListener = null;
        }

        if (mouseMotionListener != null) {
            panel.removeMouseMotionListener(mouseMotionListener);
            mouseMotionListener = null;
        }
    }

    @Override
    public Cursor getCurseur() {
        return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    }

    /**
     * Convertit un point écran en coordonnées image
     */
    private Point convertirVersImage(Point pointEcran, JPanel panel, ContexteMode context) {
        if (context.getImageChargee() == null) {
            return pointEcran;
        }

        return ConvertisseurCoordonnees.ecranVersImage(
            pointEcran,
            context.getPerspective(),
            panel.getSize(),
            new Dimension(context.getImageChargee().getWidth(), context.getImageChargee().getHeight())
        );
    }

    /**
     * Rafraîchit la vue annotations si elle existe
     */
    private void rafraichirVueAnnotations(ContexteMode context) {
        if (context.getVueAnnotations() != null) {
            context.getVueAnnotations().repaint();
        }
    }

    /**
     * Retourne l'outil crop utilisé par ce mode
     * @return L'outil crop
     */
    public OutilCrop getOutil() {
        return outil;
    }
}
