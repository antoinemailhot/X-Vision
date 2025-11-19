package xvision.controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Mode de navigation (déplacement par drag + inertie)
 * Patron State (GoF): État concret pour la navigation
 * Principe GRASP: High Cohesion - responsabilité = gestion de la navigation
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class ModeNavigation implements ModeInteraction {

    private MouseListener mouseListener;
    private MouseMotionListener mouseMotionListener;
    private int lastMouseX, lastMouseY;
    private double vx = 0, vy = 0;
    private Timer inertieTimer;

    @Override
    public void activer(JPanel panel, ContexteMode context) {
        mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();

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

                vx = dx;
                vy = dy;

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
        return Cursor.getDefaultCursor();
    }

    /**
     * Lance l'effet d'inertie après le relâchement de la souris
     */
    private void lancerInertie(JPanel panel, ContexteMode context) {
        double friction = 0.90;

        inertieTimer = new Timer(16, e -> {
            vx *= friction;
            vy *= friction;

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
