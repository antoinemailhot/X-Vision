package xvision.controller;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;

/**
 * Implémentation du mode de dessin de lignes (Annotations).
 * <p>
 * Ce mode capture les événements souris, convertit les coordonnées de l'écran (Vue)
 * vers les coordonnées de l'image (Modèle) et délègue la création de la ligne à {@link OutilLigne}.
 * </p>
 * <h3>Responsabilités :</h3>
 * <ul>
 *   <li><b>Conversion de coordonnées</b> : Transforme les clics souris (affectés par le zoom/pan) en coordonnées réelles sur l'image.</li>
 *   <li><b>Délégation</b> : Utilise {@link OutilLigne} pour gérer la logique métier de création de l'annotation.</li>
 *   <li><b>Feedback visuel</b> : Déclenche le redessin de la vue pour afficher le trait en cours de construction.</li>
 * </ul>
 */
public class ModeLigne implements ModeInteraction {

    private OutilLigne outil;
    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;

    /**
     * Constructeur du mode ligne.
     * @param outil L'instance de l'outil qui contient la configuration (couleur, épaisseur) et la logique de création.
     */
    public ModeLigne(OutilLigne outil) {
        this.outil = outil;
    }

    @Override
    public void activer(JPanel panel, ContexteMode context) {
        mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Conversion des coordonnées écran -> image
                Point pointImage = convertirVersImage(e.getPoint(), panel, context);
                
                // Création d'un événement virtuel avec les coordonnées corrigées
                MouseEvent eventConverti = new MouseEvent(
                    panel, e.getID(), e.getWhen(), e.getModifiersEx(),
                    pointImage.x, pointImage.y, e.getClickCount(), e.isPopupTrigger()
                );
                
                outil.mousePressed(eventConverti);
                panel.repaint(); // Force le rafraîchissement pour voir le début du trait
                rafraichirVueAnnotations(context);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point pointImage = convertirVersImage(e.getPoint(), panel, context);
                MouseEvent eventConverti = new MouseEvent(
                    panel, e.getID(), e.getWhen(), e.getModifiersEx(),
                    pointImage.x, pointImage.y, e.getClickCount(), e.isPopupTrigger()
                );
                
                // Finalisation du trait
                outil.mouseReleased(eventConverti);

                // Ajout au modèle via une Commande si une annotation a été créée
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
                panel.repaint(); // Important pour voir le trait suivre la souris
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
