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
 * Vue principale de l'application X-Vision
 * Affiche les informations de la perspective observée (Patron Observer - GoF)
 * Principe GRASP: Low Coupling - Observe via l'interface, pas de couplage fort
 */
public class VuePrincipale extends JFrame implements ObserverIF {

    private PerspectiveLeaf perspective;
    private ControleurPerspective controleur;
    private JLabel labelZoom, labelPos, labelImage;
    private JPanel panelImage;
    private BufferedImage imageChargee;

    /**
     * Constructeur: initialise la fenêtre avec la perspective et le contrôleur
     */
    public VuePrincipale(PerspectiveLeaf perspective, ControleurPerspective controleur) {
        super();
        this.perspective = perspective;
        this.controleur = controleur;

        // Attacher la vue comme observateur
        if (perspective != null) {
            perspective.attacher(this);
        }

        setTitle("Vue Principale - X-Vision");
        setSize(600, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel info avec labels
        JPanel panelInfo = new JPanel(new GridLayout(3,1));
        labelZoom = new JLabel();
        labelPos = new JLabel();
        labelImage = new JLabel();
        panelInfo.add(labelZoom);
        panelInfo.add(labelPos);
        panelInfo.add(labelImage);
        add(panelInfo, BorderLayout.NORTH);

        // Panel image avec dessin
        panelImage = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imageChargee != null && perspective != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                         RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    double zoom = perspective.getZoom();
                    int largeur = (int) (imageChargee.getWidth() * zoom);
                    int hauteur = (int) (imageChargee.getHeight() * zoom);

                    int x = (int) ((getWidth() - largeur) / 2 + perspective.getCentreX());
                    int y = (int) ((getHeight() - hauteur) / 2 + perspective.getCentreY());

                    g2d.drawImage(imageChargee, x, y, largeur, hauteur, this);
                }
            }
        };
        panelImage.setBackground(Color.WHITE);
        add(panelImage, BorderLayout.CENTER);

        // Zoom via molette avec le contrôleur
        panelImage.addMouseWheelListener(e -> {
            if (perspective != null && controleur != null) {
                double facteur = Math.pow(1.1, -e.getWheelRotation());
                controleur.zoomerAvecFacteur(perspective, facteur);
            }
        });

        // Undo/Redo via raccourcis clavier
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
     * Méthode de l'interface ObserverIF: actualise les informations affichées
     */
    @Override
    public void actualiser() {
        if (perspective != null) {
            labelZoom.setText("Zoom: " + String.format("%.2f", perspective.getZoom()));
            labelPos.setText("Position: (" + String.format("%.1f", perspective.getCentreX())
                             + ", " + String.format("%.1f", perspective.getCentreY()) + ")");

            String cheminImage = perspective.getCheminImage();
            if (cheminImage != null && !cheminImage.isEmpty()) {
                try {
                    imageChargee = ImageIO.read(new File(cheminImage));
                    labelImage.setText("Image: " + new File(cheminImage).getName());
                } catch (IOException e) {
                    imageChargee = null;
                    labelImage.setText("Image: Erreur de chargement");
                }
            } else {
                imageChargee = null;
                labelImage.setText("Image: Aucune");
            }

            if (panelImage != null) panelImage.repaint();
        }
    }

    /**
     * Change la perspective observée
     */
    public void setPerspective(PerspectiveLeaf p) {
        if (this.perspective != null) this.perspective.detacher(this);
        this.perspective = p;
        if (p != null) p.attacher(this);
        actualiser();
    }
}
