package xvision.controller;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;

/**
 * Mode de définition d'une zone de crop
 * Patron State (GoF): État concret pour le crop
 * Utilise OutilCrop (Strategy) pour créer le rectangle
 * Principe GRASP: High Cohesion - responsabilité = interaction pour définir un crop
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class ModeCrop implements ModeInteraction {

    private OutilCrop outil;
    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;

    /**
     * Constructeur
     * @param outil L'outil crop à utiliser
     */
    public ModeCrop(OutilCrop outil) {
        this.outil = outil;
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
                    CommandeDefinirCrop cmd = new CommandeDefinirCrop(
                        context.getGestionnaireCrop(),
                        outil.getRectangleFinal()
                    );
                    GestionnaireCommandes.getInstance().executerCommande(cmd);
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
