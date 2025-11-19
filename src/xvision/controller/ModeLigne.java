package xvision.controller;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;

/**
 * Mode de dessin de lignes
 * Patron State (GoF): État concret pour le dessin de lignes
 * Utilise OutilLigne (Strategy) pour créer les annotations
 * Principe GRASP: High Cohesion - responsabilité = interaction pour dessiner des lignes
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class ModeLigne implements ModeInteraction {

    private OutilLigne outil;
    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;

    /**
     * Constructeur
     * @param outil L'outil ligne à utiliser
     */
    public ModeLigne(OutilLigne outil) {
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

                if (outil.getAnnotationFinale() != null) {
                    CommandeAjouterAnnotation cmd = new CommandeAjouterAnnotation(
                        context.getGestionnaireAnnotations(),
                        outil.getAnnotationFinale()
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
     * Retourne l'outil ligne utilisé par ce mode
     * @return L'outil ligne
     */
    public OutilLigne getOutil() {
        return outil;
    }
}
