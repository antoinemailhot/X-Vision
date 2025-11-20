package xvision;

import xvision.model.*;
import xvision.controller.*;
import xvision.view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Point d'entrée principal de l'application X-Vision
 * Architecture MVC stricte avec split vertical (navigation + annotations)
 * Patron Observer (GoF): Observe la PerspectiveLeaf active
 * Principe GRASP: High Cohesion - centralise l'affichage et les contrôles
 */
public class MainGUI extends JFrame implements ObserverIF {

    private GestionnaireDePerspectives gestionnairePerspectives;
    private PerspectiveLeaf perspective;
    private ControleurPerspective controleur;
    private GestionnaireAnnotations gestionnaireAnnotations;
    private GestionnaireCrop gestionnaireCrop;
    private GestionnaireOutils gestionnaireOutils;
    private ContexteMode contexte;

    private JLabel labelZoom, labelPos, labelImage;
    private JPanel panelNavigation;
    private VueAnnotations vuePreview;
    private BufferedImage imageChargee;
    private String cheminImageCache;
    private MinimapOverlay minimap;

    private JToggleButton btnOutilLigne;
    private JToggleButton btnOutilCrop;

    public MainGUI() {
        // Initialisation modèle / contrôleur
        gestionnairePerspectives = new GestionnaireDePerspectives();
        controleur = new ControleurPerspective(gestionnairePerspectives);
        gestionnaireAnnotations = new GestionnaireAnnotations();
        gestionnaireCrop = new GestionnaireCrop();

        // Création perspective initiale
        perspective = controleur.creerPerspective(1.0, 0, 0);
        String cheminImageDefaut = "resources/test_images/NewYorkcityMap.png";
        File imageDefaut = new File(cheminImageDefaut);
        if (imageDefaut.exists()) {
            perspective.setCheminImage(imageDefaut.getAbsolutePath());
        } else {
            System.err.println("[MainGUI] Image par défaut introuvable: " + cheminImageDefaut);
        }
        perspective.attacher(this);

        setTitle("X-Vision - Viewer & Annotations");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        creerPanelNavigation();
        creerVueAnnotations();
        creerToolbar();
        configurerRaccourcis();

        contexte = new ContexteMode(perspective, controleur, gestionnaireAnnotations, gestionnaireCrop);
        if (vuePreview != null) contexte.setVueAnnotations(vuePreview);

        gestionnaireOutils = new GestionnaireOutils(panelNavigation, contexte);

        if (vuePreview != null) {
            vuePreview.setGestionnaireOutils(gestionnaireOutils);
            vuePreview.actualiser();
        }

        add(panelNavigation, BorderLayout.CENTER);
        actualiser();
    }

    private void creerPanelNavigation() {
        panelNavigation = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imageChargee == null || perspective == null) return;

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                     RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                     RenderingHints.VALUE_ANTIALIAS_ON);

                double zoom = perspective.getZoom();
                int largeur = (int) (imageChargee.getWidth() * zoom);
                int hauteur = (int) (imageChargee.getHeight() * zoom);
                int x = (int) ((getWidth() - largeur) / 2 + perspective.getCentreX());
                int y = (int) ((getHeight() - hauteur) / 2 + perspective.getCentreY());

                g2d.drawImage(imageChargee, x, y, largeur, hauteur, this);

                Graphics2D g2dTransformed = (Graphics2D) g2d.create();
                g2dTransformed.translate(x, y);
                g2dTransformed.scale(zoom, zoom);

                if (gestionnaireAnnotations != null) {
                    for (Annotation annotation : gestionnaireAnnotations.getAnnotations()) {
                        annotation.dessiner(g2dTransformed);
                    }
                }

                if (gestionnaireOutils != null && gestionnaireOutils.getModeLigne() != null) {
                    Annotation preview = gestionnaireOutils.getModeLigne().getOutil().getAnnotationPreview();
                    if (preview != null) preview.dessiner(g2dTransformed);
                }

                if (gestionnaireCrop != null && gestionnaireCrop.hasCrop()) {
                    Rectangle cropRect = gestionnaireCrop.getZoneCrop();
                    g2dTransformed.setColor(Color.RED);
                    g2dTransformed.setStroke(new BasicStroke(4));
                    g2dTransformed.drawRect(cropRect.x, cropRect.y, cropRect.width, cropRect.height);
                }

                if (gestionnaireOutils != null && gestionnaireOutils.getModeCrop() != null) {
                    Rectangle previewRect = gestionnaireOutils.getModeCrop().getOutil().getRectanglePreview();
                    if (previewRect != null) {
                        g2dTransformed.setColor(new Color(255, 0, 0, 100));
                        g2dTransformed.setStroke(new BasicStroke(4));
                        g2dTransformed.drawRect(previewRect.x, previewRect.y, previewRect.width, previewRect.height);
                    }
                }

                g2dTransformed.dispose();
            }
        };

        panelNavigation.setBackground(Color.DARK_GRAY);
        panelNavigation.setLayout(null);

        minimap = new MinimapOverlay(perspective, controleur, panelNavigation);
        panelNavigation.add(minimap);

        panelNavigation.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                positionnerOverlays();
            }
        });

        panelNavigation.addMouseWheelListener(e -> {
            if (perspective != null) {
                double facteur = Math.pow(1.1, -e.getWheelRotation());
                controleur.zoomerAvecFacteur(perspective, facteur);
            }
        });
    }

    private void creerVueAnnotations() {
        vuePreview = new VueAnnotations(perspective, gestionnaireAnnotations, gestionnaireCrop);
        panelNavigation.add(vuePreview);
    }

    private void positionnerOverlays() {
        positionnerMinimap();
        if (vuePreview != null && panelNavigation != null) {
            int margin = 15;
            int x = panelNavigation.getWidth() - vuePreview.getPreferredSize().width - margin;
            int y = margin;
            vuePreview.setBounds(x, y, vuePreview.getPreferredSize().width,
                    vuePreview.getPreferredSize().height);
        }
    }

    private void creerToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
        toolbar.setBorder(BorderFactory.createTitledBorder("Outils"));

        labelZoom = new JLabel("Zoom: 1.00");
        labelPos = new JLabel("Pos: (0, 0)");
        labelImage = new JLabel("Image: -");
        toolbar.add(labelZoom);
        toolbar.add(labelPos);
        toolbar.add(labelImage);
        toolbar.add(Box.createVerticalStrut(10));

        // ----------------- Charger Image -----------------
        JButton btnChargerImage = new JButton("Charger Image");
        btnChargerImage.setMaximumSize(new Dimension(150, 30));
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
                actualiser();
            }
        });
        toolbar.add(btnChargerImage);
        toolbar.add(Box.createVerticalStrut(10));

        // ----------------- Sauvegarder / Charger Perspective -----------------
        JButton btnSauvegarderPerspective = new JButton("Sauvegarder Perspective");
        btnSauvegarderPerspective.setMaximumSize(new Dimension(150, 30));
        btnSauvegarderPerspective.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Sauvegarder la perspective");
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File fichier = fileChooser.getSelectedFile();
                controleur.sauvegarderPerspectiveActuelle(fichier.getAbsolutePath());
            }
        });
        toolbar.add(btnSauvegarderPerspective);
        toolbar.add(Box.createVerticalStrut(5));

        JButton btnChargerPerspective = new JButton("Charger Perspective");
        btnChargerPerspective.setMaximumSize(new Dimension(150, 30));
        btnChargerPerspective.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Charger une perspective");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON", "json"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File fichier = fileChooser.getSelectedFile();
                PerspectiveLeaf p = controleur.chargerPerspective(fichier.getAbsolutePath());
                if (p != null) {
                    perspective = p;
                    perspective.attacher(this);
                    actualiser();
                }
            }
        });
        toolbar.add(btnChargerPerspective);
        toolbar.add(Box.createVerticalStrut(10));

        // ----------------- Outils ligne / crop -----------------
        btnOutilCrop = new JToggleButton("Outil Crop");
        btnOutilCrop.setMaximumSize(new Dimension(150, 30));
        btnOutilLigne = new JToggleButton("Outil Ligne");
        btnOutilLigne.setMaximumSize(new Dimension(150, 30));
        ButtonGroup groupOutils = new ButtonGroup();
        groupOutils.add(btnOutilCrop);
        groupOutils.add(btnOutilLigne);

        btnOutilCrop.addActionListener(e -> {
            if (btnOutilCrop.isSelected()) activerOutilCrop();
            else desactiverOutilCrop();
        });
        btnOutilLigne.addActionListener(e -> {
            if (btnOutilLigne.isSelected()) activerOutilLigne();
            else desactiverOutilLigne();
        });

        toolbar.add(btnOutilCrop);
        toolbar.add(Box.createVerticalStrut(5));
        toolbar.add(btnOutilLigne);
        toolbar.add(Box.createVerticalStrut(10));

        // ----------------- Undo / Redo -----------------
        JButton btnUndo = new JButton("Undo (Ctrl+Z)");
        btnUndo.setMaximumSize(new Dimension(150, 30));
        btnUndo.addActionListener(e -> GestionnaireCommandes.getInstance().undo());
        toolbar.add(btnUndo);

        JButton btnRedo = new JButton("Redo (Ctrl+Y)");
        btnRedo.setMaximumSize(new Dimension(150, 30));
        btnRedo.addActionListener(e -> GestionnaireCommandes.getInstance().redo());
        toolbar.add(btnRedo);

        // ----------------- Effacer annotations -----------------
        JButton btnEffacerAnnotations = new JButton("Effacer tout");
        btnEffacerAnnotations.setMaximumSize(new Dimension(150, 30));
        btnEffacerAnnotations.addActionListener(e -> {
            gestionnaireAnnotations.effacerTout();
            panelNavigation.repaint();
        });
        toolbar.add(btnEffacerAnnotations);

        toolbar.add(Box.createVerticalGlue());
        add(toolbar, BorderLayout.EAST);
    }

    private void configurerRaccourcis() {
        int shortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('Z', shortcutMask), "undo");
        getRootPane().getActionMap().put("undo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { GestionnaireCommandes.getInstance().undo(); }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('Y', shortcutMask), "redo");
        getRootPane().getActionMap().put("redo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { GestionnaireCommandes.getInstance().redo(); }
        });
    }

    private void positionnerMinimap() {
        if (minimap != null && panelNavigation != null) {
            int margin = 15;
            int x = panelNavigation.getWidth() - minimap.getPreferredSize().width - margin;
            int y = panelNavigation.getHeight() - minimap.getPreferredSize().height - margin;
            minimap.setBounds(x, y, minimap.getPreferredSize().width, minimap.getPreferredSize().height);
        }
    }

    @Override
    public void actualiser() {
        if (perspective == null) return;
        labelZoom.setText("Zoom: " + String.format("%.2f", perspective.getZoom()));
        labelPos.setText("Position: (" + String.format("%.1f", perspective.getCentreX()) + ", " +
                String.format("%.1f", perspective.getCentreY()) + ")");
        String path = perspective.getCheminImage();
        if (path != null && !path.isEmpty() && !path.equals(cheminImageCache)) {
            try {
                imageChargee = ImageIO.read(new File(path));
                cheminImageCache = path;
                labelImage.setText("Image: " + new File(path).getName());
                if (contexte != null) contexte.setImageChargee(imageChargee);
            } catch (IOException e) {
                imageChargee = null;
                cheminImageCache = null;
                labelImage.setText("Image: Erreur de chargement");
                if (contexte != null) contexte.setImageChargee(null);
            }
        } else if (path == null || path.isEmpty()) {
            imageChargee = null;
            cheminImageCache = null;
            labelImage.setText("Image: Aucune");
            if (contexte != null) contexte.setImageChargee(null);
        }
        panelNavigation.repaint();
        positionnerOverlays();
    }

    private void activerOutilLigne() {
        gestionnaireOutils.activerModeLigne(Color.RED, 3);
        panelNavigation.repaint();
        if (vuePreview != null) vuePreview.actualiser();
    }

    private void desactiverOutilLigne() {
        gestionnaireOutils.activerModeNavigation();
        btnOutilLigne.setSelected(false);
        panelNavigation.repaint();
        if (vuePreview != null) vuePreview.actualiser();
    }

    private void activerOutilCrop() {
        gestionnaireOutils.activerModeCrop();
        panelNavigation.repaint();
        if (vuePreview != null) vuePreview.actualiser();
    }

    private void desactiverOutilCrop() {
        gestionnaireOutils.activerModeNavigation();
        btnOutilCrop.setSelected(false);
        panelNavigation.repaint();
        if (vuePreview != null) vuePreview.actualiser();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainGUI app = new MainGUI();
            app.setVisible(true);
        });
    }
}
