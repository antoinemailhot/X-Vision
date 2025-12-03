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
 * Composant graphique affichant une vue globale (Minimap) en surimpression.
 * <p>
 * Ce panneau semi-transparent se place généralement dans un coin de la vue principale.
 * Il affiche l'image complète en miniature et un rectangle rouge (viewport) représentant
 * la zone actuellement visible dans la fenêtre principale.
 * </p>
 * <h3>Fonctionnalités :</h3>
 * <ul>
 *   <li><b>Visualisation</b> : Affiche l'image entière redimensionnée pour tenir dans la zone de la minimap.</li>
 *   <li><b>Navigation</b> : Permet de cliquer ou glisser sur la minimap pour déplacer rapidement la vue principale (téléportation).</li>
 *   <li><b>Synchronisation</b> : Observe le modèle ({@link PerspectiveLeaf}) pour mettre à jour le viewport en temps réel.</li>
 * </ul>
 * <h3>Patrons de conception :</h3>
 * <ul>
 *   <li><b>Observer (GoF)</b> : S'abonne aux changements de la perspective pour redessiner le viewport.</li>
 *   <li><b>Information Expert (GRASP)</b> : Calcule les ratios de conversion entre l'espace monde et l'espace minimap.</li>
 * </ul>
 */
public class MinimapOverlay extends JPanel implements ObserverIF {

    private PerspectiveLeaf perspective;
    private ControleurPerspective controleur;
    private BufferedImage imageComplete;
    private String cheminImageCache; // Cache du chemin pour éviter rechargements inutiles

    // Dimensions fixes de la minimap
    private static final int MINIMAP_WIDTH = 200;
    private static final int MINIMAP_HEIGHT = 150;
    
    // Style visuel
    private static final Color BG_COLOR = new Color(0, 0, 0, 150); // Fond noir semi-transparent
    private static final Color VIEWPORT_COLOR = new Color(255, 0, 0, 200); // Rectangle rouge vif
    private static final Color BORDER_COLOR = new Color(255, 255, 255, 100); // Bordure blanche subtile

    // Dimensions calculées de l'image à l'intérieur de la minimap (pour respecter le ratio)
    private int imageX, imageY, imageWidth, imageHeight;

    // Référence à la vue parente pour connaître la taille de la fenêtre d'affichage
    private JPanel viewportParent;

    /**
     * Constructeur de la minimap.
     * @param perspective La perspective à observer.
     * @param controleur Le contrôleur pour effectuer les déplacements via la minimap.
     * @param viewportParent Le panneau parent dont on affiche la zone visible.
     */
    public MinimapOverlay(PerspectiveLeaf perspective, ControleurPerspective controleur, JPanel viewportParent) {
        this.perspective = perspective;
        this.controleur = controleur;
        this.viewportParent = viewportParent;

        // Enregistrement comme observateur (Patron Observer)
        if (perspective != null) {
            perspective.attacher(this);
        }

        // Configuration du panel
        setOpaque(false); // Transparent pour voir à travers
        setPreferredSize(new Dimension(MINIMAP_WIDTH, MINIMAP_HEIGHT));

        // Interaction: click pour naviguer
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                naviguerVersPosition(e.getX(), e.getY());
            }
        });

        // Curseur main pour indiquer la clickabilité
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        actualiser();
    }

    /**
     * Méthode de l'interface ObserverIF
     * Appelée automatiquement quand la perspective change
     * Optimisation: charge l'image uniquement si le chemin a changé
     */
    @Override
    public void actualiser() {
        if (perspective != null) {
            String cheminImage = perspective.getCheminImage();

            // Charger l'image SEULEMENT si le chemin a changé
            if (cheminImage != null && !cheminImage.isEmpty() &&
                !cheminImage.equals(cheminImageCache)) {

                try {
                    imageComplete = ImageIO.read(new File(cheminImage));
                    cheminImageCache = cheminImage;
                    calculerDimensionsImage();
                } catch (IOException e) {
                    System.err.println("[MinimapOverlay] Erreur chargement: " + e.getMessage());
                    imageComplete = null;
                    cheminImageCache = null;
                }
            }
        }
        repaint();
    }

    /**
     * Calcule les dimensions de l'image dans la minimap (avec aspect ratio préservé)
     * Principe GRASP: Information Expert - cette classe connaît les dimensions nécessaires
     */
    private void calculerDimensionsImage() {
        if (imageComplete == null) return;

        int imgW = imageComplete.getWidth();
        int imgH = imageComplete.getHeight();

        // Calculer le ratio pour fit dans la minimap avec padding
        int maxW = MINIMAP_WIDTH - 20;
        int maxH = MINIMAP_HEIGHT - 20;

        double ratioW = (double) maxW / imgW;
        double ratioH = (double) maxH / imgH;
        double ratio = Math.min(ratioW, ratioH);

        imageWidth = (int) (imgW * ratio);
        imageHeight = (int) (imgH * ratio);

        // Centrer l'image dans la minimap
        imageX = (MINIMAP_WIDTH - imageWidth) / 2;
        imageY = (MINIMAP_HEIGHT - imageHeight) / 2;
    }

    /**
     * Dessine la minimap avec l'image et le rectangle viewport
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fond semi-transparent
        g2d.setColor(BG_COLOR);
        g2d.fillRoundRect(0, 0, MINIMAP_WIDTH, MINIMAP_HEIGHT, 10, 10);

        // Bordure
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(0, 0, MINIMAP_WIDTH - 1, MINIMAP_HEIGHT - 1, 10, 10);

        // Dessiner l'image complète
        if (imageComplete != null) {
            g2d.drawImage(imageComplete, imageX, imageY, imageWidth, imageHeight, this);

            // Dessiner le rectangle viewport
            dessinerViewport(g2d);
        } else {
            // Texte si pas d'image
            g2d.setColor(Color.WHITE);
            g2d.drawString("No image", MINIMAP_WIDTH / 2 - 25, MINIMAP_HEIGHT / 2);
        }
    }

    /**
     * Dessine le rectangle représentant la zone visible dans VuePrincipale
     * Principe GRASP: Information Expert - calcule basé sur perspective + dimensions
     */
    private void dessinerViewport(Graphics2D g2d) {
        if (perspective == null || viewportParent == null || imageComplete == null) return;

        // Dimensions réelles de l'image
        int imgRealW = imageComplete.getWidth();
        int imgRealH = imageComplete.getHeight();

        // Dimensions du viewport parent (VuePrincipale)
        int parentW = viewportParent.getWidth();
        int parentH = viewportParent.getHeight();

        double zoom = perspective.getZoom();
        double centreX = perspective.getCentreX();
        double centreY = perspective.getCentreY();

        // Taille de l'image zoomée dans la vue principale
        int zoomedW = (int) (imgRealW * zoom);
        int zoomedH = (int) (imgRealH * zoom);

        // Position de l'image dans la vue principale
        int posX = (int) ((parentW - zoomedW) / 2 + centreX);
        int posY = (int) ((parentH - zoomedH) / 2 + centreY);

        // Calculer quelle portion de l'image est visible
        // Les coins du viewport dans les coordonnées de l'image réelle
        double visibleLeft = -posX / zoom;
        double visibleTop = -posY / zoom;
        double visibleRight = visibleLeft + (parentW / zoom);
        double visibleBottom = visibleTop + (parentH / zoom);

        // Convertir en coordonnées minimap
        double scaleX = (double) imageWidth / imgRealW;
        double scaleY = (double) imageHeight / imgRealH;

        int rectX = imageX + (int) (visibleLeft * scaleX);
        int rectY = imageY + (int) (visibleTop * scaleY);
        int rectW = (int) ((visibleRight - visibleLeft) * scaleX);
        int rectH = (int) ((visibleBottom - visibleTop) * scaleY);

        // Dessiner le rectangle viewport
        g2d.setColor(VIEWPORT_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(rectX, rectY, rectW, rectH);

        // Remplissage semi-transparent
        g2d.setColor(new Color(255, 0, 0, 30));
        g2d.fillRect(rectX, rectY, rectW, rectH);
    }

    /**
     * Navigue vers une position cliquée sur la minimap
     * Interaction style Google Maps
     */
    private void naviguerVersPosition(int clickX, int clickY) {
        if (perspective == null || imageComplete == null || controleur == null) return;

        // Vérifier si le click est dans la zone de l'image
        if (clickX < imageX || clickX > imageX + imageWidth ||
            clickY < imageY || clickY > imageY + imageHeight) {
            return; // Click hors de l'image
        }

        // Convertir le click en coordonnées de l'image réelle
        int imgRealW = imageComplete.getWidth();
        int imgRealH = imageComplete.getHeight();

        double scaleX = (double) imgRealW / imageWidth;
        double scaleY = (double) imgRealH / imageHeight;

        double imageClickX = (clickX - imageX) * scaleX;
        double imageClickY = (clickY - imageY) * scaleY;

        // Calculer le déplacement nécessaire pour centrer sur ce point
        // Le centre actuel est à (imgRealW/2, imgRealH/2) en coordonnées image
        // On veut déplacer vers (imageClickX, imageClickY)

        double zoom = perspective.getZoom();
        // Calcul de la nouvelle position du centre
        double newCentreX = -(imageClickX - imgRealW/2.0) * zoom;
        double newCentreY = -(imageClickY - imgRealH/2.0) * zoom;

        // Appliquer le déplacement via le contrôleur
        controleur.deplacer(perspective, newCentreX - perspective.getCentreX(),
                                       newCentreY - perspective.getCentreY());
    }

    /**
     * Change la perspective observée
     */
    public void setPerspective(PerspectiveLeaf p) {
        if (this.perspective != null) {
            this.perspective.detacher(this);
        }

        this.perspective = p;

        if (p != null) {
            p.attacher(this);
        }

        actualiser();
    }
}
