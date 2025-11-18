package xvision.view;

import javax.swing.*;
import xvision.model.PerspectiveLeaf;
import xvision.controller.ControleurPerspective;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Vue principale affichant la perspective active.
 * Déplacement fluide avec inertie.
 */
public class VuePrincipale extends JFrame implements ObserverIF {

    private PerspectiveLeaf perspective;
    private ControleurPerspective controleur;

    private JLabel labelZoom, labelPos, labelImage;
    private JPanel panelImage;
    private BufferedImage imageChargee;

    // --- Variables pour déplacement + inertie ---
    private int lastMouseX, lastMouseY;
    private double vx = 0;
    private double vy = 0;
    private Timer inertieTimer;

    /**
     * Constructeur
     */
    public VuePrincipale(PerspectiveLeaf perspective, ControleurPerspective controleur) {
        this.perspective = perspective;
        this.controleur = controleur;

        if (perspective != null) perspective.attacher(this);

        setTitle("Vue Principale - X-Vision");
        setSize(600, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Zone d'infos
        JPanel panelInfo = new JPanel(new GridLayout(3, 1));
        labelZoom = new JLabel();
        labelPos  = new JLabel();
        labelImage = new JLabel();

        panelInfo.add(labelZoom);
        panelInfo.add(labelPos);
        panelInfo.add(labelImage);
        add(panelInfo, BorderLayout.NORTH);

        // Zone image
        panelImage = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (imageChargee != null && perspective != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                         RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    double zoom = perspective.getZoom();
                    int largeur = (int)(imageChargee.getWidth() * zoom);
                    int hauteur = (int)(imageChargee.getHeight() * zoom);

                    int x = (int)((getWidth() - largeur)/2 + perspective.getCentreX());
                    int y = (int)((getHeight() - hauteur)/2 + perspective.getCentreY());

                    g2d.drawImage(imageChargee, x, y, largeur, hauteur, this);
                }
            }
        };

        panelImage.setBackground(Color.WHITE);
        add(panelImage, BorderLayout.CENTER);

        // ===================== ZOOM ==========================
        panelImage.addMouseWheelListener(e -> {
            if (perspective != null) {
                double facteur = Math.pow(1.1, -e.getWheelRotation());
                controleur.zoomerAvecFacteur(perspective, facteur);
            }
        });

        // ===================== DRAG (avec inertie) ======================
        panelImage.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();

                // Stoppe la précédente inertie
                if (inertieTimer != null && inertieTimer.isRunning()) {
                    inertieTimer.stop();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lancerInertie();
            }
        });

        panelImage.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;

                // Enregistrement de la vitesse (pour inertie)
                vx = dx;
                vy = dy;

                // Déplacement immédiat
                controleur.deplacerDelta(perspective, dx, dy);

                lastMouseX = e.getX();
                lastMouseY = e.getY();

                panelImage.repaint();
            }
        });

        // ================== UNDO / REDO ===================
        int shortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('Z', shortcutMask), "undo");
        getRootPane().getActionMap().put("undo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (perspective != null) perspective.undo();
            }
        });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('Y', shortcutMask), "redo");
        getRootPane().getActionMap().put("redo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (perspective != null) perspective.redo();
            }
        });

        setVisible(true);
        actualiser();
    }

    /**
     * Lance l'inertie après relâchement
     */
    private void lancerInertie() {
        // diminution progressive de la vitesse
        double friction = 0.90;

        inertieTimer = new Timer(16, e -> {
            vx *= friction;
            vy *= friction;

            if (Math.abs(vx) < 0.1 && Math.abs(vy) < 0.1) {
                inertieTimer.stop();
                return;
            }

            controleur.deplacerDelta(perspective, vx, vy);
            panelImage.repaint();
        });

        inertieTimer.start();
    }

    /**
     * Mise à jour Observer
     */
    @Override
    public void actualiser() {
        if (perspective == null) return;

        labelZoom.setText("Zoom: " + String.format("%.2f", perspective.getZoom()));
        labelPos.setText("Position: (" +
                String.format("%.1f", perspective.getCentreX()) + ", " +
                String.format("%.1f", perspective.getCentreY()) + ")");

        String path = perspective.getCheminImage();
        if (path != null && !path.isEmpty()) {
            try {
                imageChargee = ImageIO.read(new File(path));
                labelImage.setText("Image: " + new File(path).getName());
            } catch (IOException e) {
                imageChargee = null;
                labelImage.setText("Image: Erreur de chargement");
            }
        } else {
            imageChargee = null;
            labelImage.setText("Image: Aucune");
        }

        panelImage.repaint();
    }

    public void setPerspective(PerspectiveLeaf p) {
        if (this.perspective != null) this.perspective.detacher(this);
        this.perspective = p;
        if (p != null) p.attacher(this);
        actualiser();
    }
}
