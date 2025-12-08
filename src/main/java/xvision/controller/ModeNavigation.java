package xvision.controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Implémentation du mode de navigation (Pan & Zoom).
 * <p>
 * Ce mode permet à l'utilisateur de déplacer l'image (Pan) en la faisant glisser avec la souris.
 * Il implémente également un effet d'<b>inertie</b> : lorsque l'utilisateur relâche la souris brusquement,
 * le mouvement continue en décélérant progressivement.
 * </p>
 * <h3>Fonctionnement :</h3>
 * <ul>
 *   <li><b>MouseDragged</b> : Calcule le delta de déplacement et délègue au contrôleur la translation.</li>
 *   <li><b>MouseReleased</b> : Lance un Timer Swing pour simuler l'inertie basée sur la dernière vitesse enregistrée.</li>
 * </ul>
 */
public class ModeNavigation implements ModeInteraction {

    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;
    private int lastMouseX, lastMouseY;
    private double vx = 0, vy = 0; // Vitesse actuelle pour l'inertie
    private Timer inertieTimer;

    /**
     * Active le mode navigation.
     * Attache les écouteurs pour gérer le glisser-déposer (drag) et l'inertie.
     */
    @Override
    public void activer(JPanel panel, ContexteMode context) {
        mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();

                // Arrêt de l'inertie si l'utilisateur reprend le contrôle
                if (inertieTimer != null && inertieTimer.isRunning()) {
                    inertieTimer.stop();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lancerInertie(panel, context);
            }
        };

        mouseMotionListener = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;

                // Mise à jour de la vitesse instantanée
                vx = dx;
                vy = dy;

                // Délégation au contrôleur pour appliquer la translation
                context.getControleur().deplacerDelta(context.getPerspective(), dx, dy);

                lastMouseX = e.getX();
                lastMouseY = e.getY();

                panel.repaint();
            }
        };

        panel.addMouseListener(mouseListener);
        panel.addMouseMotionListener(mouseMotionListener);
        panel.setCursor(getCurseur());
    }

    /**
     * Désactive le mode navigation.
     * Retire les écouteurs et arrête le timer d'inertie s'il est en cours.
     */
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

        if (inertieTimer != null && inertieTimer.isRunning()) {
            inertieTimer.stop();
        }
    }

    @Override
    public Cursor getCurseur() {
        return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    }

    /**
     * Lance l'animation d'inertie après le relâchement de la souris.
     * Utilise un Timer Swing pour appliquer une décélération progressive (frottement).
     */
    private void lancerInertie(JPanel panel, ContexteMode context) {
        double friction = 0.90; // Facteur de frottement (10% de perte par frame)

        inertieTimer = new Timer(16, e -> { // ~60 FPS
            vx *= friction;
            vy *= friction;

            // Arrêt si la vitesse est négligeable
            if (Math.abs(vx) < 0.1 && Math.abs(vy) < 0.1) {
                inertieTimer.stop();
                return;
            }

            context.getControleur().deplacerDelta(context.getPerspective(), vx, vy);
            panel.repaint();
        });

        inertieTimer.start();
    }
}
