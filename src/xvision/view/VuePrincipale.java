package xvision.view;

import javax.swing.*;
import xvision.model.PerspectiveLeaf;
import xvision.model.Sujet;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Vue principale de l'application X-Vision
 * Affiche les informations de la perspective observée (Patron Observer - GoF)
 * Principe GRASP: Low Coupling - Observe via l'interface, pas de couplage fort
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class VuePrincipale extends JFrame implements ObserverIF {

    private PerspectiveLeaf perspective;
    private JLabel labelZoom, labelPos, labelImage;
    private JPanel panelImage;
    private BufferedImage imageChargee;

    /**
     * Constructeur de la vue principale
     * S'enregistre automatiquement comme observateur de la perspective
     *
     * @param p La perspective à observer
     */
    public VuePrincipale(PerspectiveLeaf p) {
        super();
        this.perspective = p;

        // Enregistrement comme observateur (Patron Observer)
        if (p != null) {
            p.attacher(this);
        }

        setTitle("Vue Principale - X-Vision");
        setSize(600, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panneau supérieur pour les informations
        JPanel panelInfo = new JPanel(new GridLayout(3, 1));
        labelZoom = new JLabel();
        labelPos = new JLabel();
        labelImage = new JLabel();
        panelInfo.add(labelZoom);
        panelInfo.add(labelPos);
        panelInfo.add(labelImage);
        add(panelInfo, BorderLayout.NORTH);

        // Panneau central pour l'image
        panelImage = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imageChargee != null && perspective != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                         RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    // Appliquer le zoom
                    double zoom = perspective.getZoom();
                    int largeur = (int) (imageChargee.getWidth() * zoom);
                    int hauteur = (int) (imageChargee.getHeight() * zoom);

                    // Calculer la position avec translation
                    int x = (int) ((getWidth() - largeur) / 2 + perspective.getCentreX());
                    int y = (int) ((getHeight() - hauteur) / 2 + perspective.getCentreY());

                    g2d.drawImage(imageChargee, x, y, largeur, hauteur, this);
                }
            }
        };
        panelImage.setBackground(Color.WHITE);
        add(panelImage, BorderLayout.CENTER);

        setVisible(true);
        actualiser();
    }

    /**
     * Méthode de l'interface ObserverIF
     * Appelée automatiquement quand la perspective change
     */
    @Override
    public void actualiser() {
        if (perspective != null) {
            labelZoom.setText("Zoom: " + perspective.getZoom());
            labelPos.setText("Position: (" + perspective.getCentreX() + "," + perspective.getCentreY() + ")");

            // Charger l'image si le chemin est défini
            String cheminImage = perspective.getCheminImage();
            if (cheminImage != null && !cheminImage.isEmpty()) {
                try {
                    imageChargee = ImageIO.read(new File(cheminImage));
                    labelImage.setText("Image: " + new File(cheminImage).getName());
                    System.out.println("[VuePrincipale] Image chargée: " + cheminImage);
                } catch (IOException e) {
                    System.err.println("[VuePrincipale] Erreur lors du chargement de l'image: " + e.getMessage());
                    imageChargee = null;
                    labelImage.setText("Image: Erreur de chargement");
                }
            } else {
                labelImage.setText("Image: Aucune");
            }

            // Rafraîchir le panneau d'image
            if (panelImage != null) {
                panelImage.repaint();
            }
        }
    }

    /**
     * Modifie la perspective observée
     * Principe GRASP: Information Expert
     *
     * @param p La nouvelle perspective à observer
     */
    public void setPerspective(PerspectiveLeaf p) {
        // Détacher de l'ancienne perspective
        if (this.perspective != null) {
            this.perspective.detacher(this);
        }

        // Attacher à la nouvelle perspective
        this.perspective = p;
        if (p != null) {
            p.attacher(this);
        }

        // Rafraîchir immédiatement
        actualiser();
    }
}
