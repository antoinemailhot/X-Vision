package xvision;

import xvision.model.GestionnaireDePerspectives;
import xvision.model.PerspectiveLeaf;
import xvision.controller.ControleurPerspective;
import xvision.view.MinimapOverlay;
import xvision.view.ObserverIF;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Point d'entrée principal de l'application X-Vision
 * Architecture MVC stricte avec vue intégrée
 * Patron Observer (GoF): Observe la PerspectiveLeaf active
 * Principe GRASP: High Cohesion - centralise l'affichage et les contrôles
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 2.0
 */
public class MainGUI extends JFrame implements ObserverIF {

    private PerspectiveLeaf perspective;
    private ControleurPerspective controleur;

    private JLabel labelZoom, labelPos, labelImage;
    private JPanel panelImage;
    private BufferedImage imageChargee;
    private String cheminImageCache; // Cache du chemin pour éviter rechargements
    private MinimapOverlay minimap;

    private int lastMouseX, lastMouseY;
    private double vx = 0;
    private double vy = 0;
    private Timer inertieTimer;

    /**
     * Constructeur - initialise le modèle, le contrôleur et la vue
     */
    public MainGUI() {
        GestionnaireDePerspectives gestionnaire = new GestionnaireDePerspectives();
        controleur = new ControleurPerspective(gestionnaire);

        perspective = controleur.creerPerspective(1.0, 0, 0);
        String cheminImageDefaut = "resources/test_images/NewYorkcityMap.png";
        File imageDefaut = new File(cheminImageDefaut);
        if (imageDefaut.exists()) {
            perspective.setCheminImage(imageDefaut.getAbsolutePath());
        } else {
            System.err.println("[MainGUI] Image par défaut introuvable: " + cheminImageDefaut);
        }

        perspective.attacher(this);

        setTitle("X-Vision - Viewer d'Images");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelControle = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panelControle.setBorder(BorderFactory.createTitledBorder("Contrôles"));
        add(panelControle, BorderLayout.NORTH);

        JPanel panelInfo = new JPanel(new GridLayout(3, 1));
        labelZoom = new JLabel();
        labelPos = new JLabel();
        labelImage = new JLabel();
        panelInfo.add(labelZoom);
        panelInfo.add(labelPos);
        panelInfo.add(labelImage);
        panelControle.add(panelInfo);

        JButton btnChargerImage = new JButton("Charger Image");
        JButton btnUndo = new JButton("Undo");
        JButton btnRedo = new JButton("Redo");

        panelControle.add(new JSeparator(SwingConstants.VERTICAL));
        panelControle.add(btnChargerImage);
        panelControle.add(new JSeparator(SwingConstants.VERTICAL));
        panelControle.add(btnUndo);
        panelControle.add(btnRedo);

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

        panelImage.setBackground(Color.DARK_GRAY);
        panelImage.setLayout(null);
        add(panelImage, BorderLayout.CENTER);

        minimap = new MinimapOverlay(perspective, controleur, panelImage);
        panelImage.add(minimap);

        panelImage.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                positionnerMinimap();
            }
        });

        panelImage.addMouseWheelListener(e -> {
            if (perspective != null) {
                double facteur = Math.pow(1.1, -e.getWheelRotation());
                controleur.zoomerAvecFacteur(perspective, facteur);
            }
        });

        panelImage.addMouseListener(new MouseAdapter() {
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
                lancerInertie();
            }
        });

        panelImage.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;

                vx = dx;
                vy = dy;

                controleur.deplacerDelta(perspective, dx, dy);

                lastMouseX = e.getX();
                lastMouseY = e.getY();

                panelImage.repaint();
            }
        });

        btnChargerImage.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("resources/test_images"));
            fileChooser.setDialogTitle("Sélectionner une image");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Images", "jpg", "jpeg", "png", "gif", "bmp"));

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                perspective.setCheminImage(selectedFile.getAbsolutePath());
            }
        });

        btnUndo.addActionListener(e -> {
            if (perspective != null) perspective.undo();
        });

        btnRedo.addActionListener(e -> {
            if (perspective != null) perspective.redo();
        });

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

        actualiser();
    }

    /**
     * Lance l'inertie après relâchement de la souris
     */
    private void lancerInertie() {
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
     * Positionne la minimap en bas à droite
     */
    private void positionnerMinimap() {
        if (minimap != null && panelImage != null) {
            int margin = 15;
            int x = panelImage.getWidth() - minimap.getPreferredSize().width - margin;
            int y = panelImage.getHeight() - minimap.getPreferredSize().height - margin;
            minimap.setBounds(x, y, minimap.getPreferredSize().width, minimap.getPreferredSize().height);
        }
    }

    /**
     * Mise à jour Observer (Patron Observer - GoF)
     * Appelée automatiquement par le modèle quand il change
     * Optimisation: charge l'image uniquement si le chemin a changé
     */
    @Override
    public void actualiser() {
        if (perspective == null) return;

        labelZoom.setText("Zoom: " + String.format("%.2f", perspective.getZoom()));
        labelPos.setText("Position: (" +
                String.format("%.1f", perspective.getCentreX()) + ", " +
                String.format("%.1f", perspective.getCentreY()) + ")");

        String path = perspective.getCheminImage();

        // Charger l'image SEULEMENT si le chemin a changé
        if (path != null && !path.isEmpty() && !path.equals(cheminImageCache)) {
            try {
                imageChargee = ImageIO.read(new File(path));
                cheminImageCache = path;
                labelImage.setText("Image: " + new File(path).getName());
            } catch (IOException e) {
                imageChargee = null;
                cheminImageCache = null;
                labelImage.setText("Image: Erreur de chargement");
            }
        } else if (path == null || path.isEmpty()) {
            imageChargee = null;
            cheminImageCache = null;
            labelImage.setText("Image: Aucune");
        }

        panelImage.repaint();
        positionnerMinimap();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainGUI app = new MainGUI();
            app.setVisible(true);
        });
    }
}
