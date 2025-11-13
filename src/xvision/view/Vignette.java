package xvision.view;

import xvision.model.PerspectiveLeaf;
import xvision.model.Sujet;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Vue miniature (vignette) d'une perspective
 * Hérite de VueAbstraite pour bénéficier du patron Observer
 * Principe GRASP: Low Coupling - Dépend de l'abstraction Sujet
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class Vignette extends VueAbstraite {

    private BufferedImage imageChargee;

    /**
     * Constructeur par défaut
     */
    public Vignette() {
        super();
        initUI();
    }

    /**
     * Constructeur avec sujet à observer
     * @param sujet Le sujet (perspective) à observer
     */
    public Vignette(Sujet sujet) {
        super(sujet);
        initUI();
    }

    /**
     * Initialise l'interface utilisateur de la vignette
     */
    private void initUI() {
        setPreferredSize(new Dimension(150, 100));
        setBackground(Color.LIGHT_GRAY);
        setBorder(javax.swing.BorderFactory.createTitledBorder("Vignette"));
    }

    /**
     * Rafraîchit l'affichage de la vignette
     * Implémentation de la méthode abstraite de VueAbstraite
     */
    @Override
    protected void rafraichir() {
        if (sujetObserve instanceof PerspectiveLeaf) {
            PerspectiveLeaf perspective = (PerspectiveLeaf) sujetObserve;
            System.out.println("[Vignette] Zoom=" + perspective.getZoom() +
                               ", Pos=(" + perspective.getCentreX() + "," + perspective.getCentreY() + ")");

            // Charger l'image si le chemin a changé
            String cheminImage = perspective.getCheminImage();
            if (cheminImage != null && !cheminImage.isEmpty()) {
                try {
                    imageChargee = ImageIO.read(new File(cheminImage));
                    System.out.println("[Vignette] Image chargée: " + cheminImage);
                } catch (IOException e) {
                    System.err.println("[Vignette] Erreur lors du chargement de l'image: " + e.getMessage());
                    imageChargee = null;
                }
            }

            repaint();
        }
    }

    /**
     * Dessine la vignette
     * @param g Le contexte graphique
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (sujetObserve instanceof PerspectiveLeaf) {
            PerspectiveLeaf perspective = (PerspectiveLeaf) sujetObserve;
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Afficher l'image si elle est chargée
            if (imageChargee != null) {
                // Appliquer le zoom à l'image
                double zoom = perspective.getZoom();
                int largeurImage = (int) (imageChargee.getWidth() * zoom / 10);
                int hauteurImage = (int) (imageChargee.getHeight() * zoom / 10);

                // Centrer l'image dans la vignette
                int x = (getWidth() - largeurImage) / 2;
                int y = (getHeight() - hauteurImage) / 2 + 10;

                g2d.drawImage(imageChargee, x, y, largeurImage, hauteurImage, this);
            }

            // Affichage du zoom en bas
            String texte = String.format("Zoom: %.2f", perspective.getZoom());
            g2d.drawString(texte, 10, getHeight() - 10);
        }
    }
}
