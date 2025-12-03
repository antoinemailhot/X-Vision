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
 * Point d'entrée principal de l'application X-Vision.
 * <p>
 * Cette classe représente la fenêtre principale (JFrame) et orchestre l'interaction entre
 * les différentes vues et le contrôleur (Architecture MVC).
 * </p>
 * <h3>Patrons de conception utilisés :</h3>
 * <ul>
 *   <li><b>MVC (Modèle-Vue-Contrôleur)</b> : Sépare la logique ({@link ControleurPerspective}),
 *       les données ({@link PerspectiveLeaf}) et l'affichage (cette classe).</li>
 *   <li><b>Observer (GoF)</b> : Implements {@link ObserverIF} pour réagir aux changements du modèle
 *       (zoom, translation, crop, annotations).</li>
 *   <li><b>Command (GoF)</b> : Utilise {@link GestionnaireCommandes} pour les actions annulables.</li>
 *   <li><b>Memento (GoF)</b> : Gère l'historique des états via la persistance et l'undo/redo.</li>
 *   <li><b>Strategy (GoF)</b> : Les modes d'interaction ({@link xvision.controller.ModeInteraction})
 *       définissent le comportement de la souris.</li>
 * </ul>
 * <h3>Fonctionnalités clés :</h3>
 * <ul>
 *   <li>Affichage et navigation dans une image (Zoom/Pan).</li>
 *   <li>Annotations vectorielles (Lignes).</li>
 *   <li>Recadrage (Crop) avec prévisualisation.</li>
 *   <li>Sauvegarde et Chargement complet de l'état ("Deep State").</li>
 *   <li>Chargement asynchrone via {@link SwingWorker}.</li>
 * </ul>
 */
public class MainGUI extends JFrame implements ObserverIF {

    // --- Modèle et Contrôleur ---
    private GestionnaireDePerspectives gestionnairePerspectives;
    private PerspectiveLeaf perspective;
    private ControleurPerspective controleur;
    private GestionnaireAnnotations gestionnaireAnnotations;
    private GestionnaireCrop gestionnaireCrop;
    private GestionnaireOutils gestionnaireOutils;
    private ContexteMode contexte;

    // --- Composants UI ---
    private JLabel labelZoom, labelPos, labelImage;
    private JPanel panelNavigation;
    private VueAnnotations vuePreview;
    private BufferedImage imageChargee;
    private String cheminImageCache;
    private MinimapOverlay minimap;

    private JToggleButton btnOutilLigne;
    private JToggleButton btnOutilCrop;

    // --- Autosave ---
    private final String cheminAutosave = "perspectives/autosave.json";
    private javax.swing.Timer autosaveTimer;

    /**
     * Constructeur principal. Initialise l'application, les modèles et l'interface graphique.
     */
    public MainGUI() {
        // 1. Initialisation du Modèle et du Contrôleur
        gestionnairePerspectives = new GestionnaireDePerspectives();
        controleur = new ControleurPerspective(gestionnairePerspectives);
        gestionnaireAnnotations = new GestionnaireAnnotations();
        gestionnaireCrop = new GestionnaireCrop();

        // 2. Création de la perspective par défaut
        perspective = controleur.creerPerspective(1.0, 0, 0);
        String cheminImageDefaut = "resources/test_images/NewYorkcityMap.png";
        File imageDefaut = new File(cheminImageDefaut);
        if (imageDefaut.exists()) {
            perspective.setCheminImage(imageDefaut.getAbsolutePath());
        } else {
            System.err.println("[MainGUI] Image par défaut introuvable: " + cheminImageDefaut);
        }
        // Abonnement aux notifications du modèle (Observer)
        perspective.attacher(this);

        // 3. Configuration de la fenêtre
        setTitle("X-Vision - Viewer & Annotations");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 4. Construction de l'interface
        creerPanelNavigation();
        creerVueAnnotations();
        creerToolbar();
        configurerRaccourcis();

        // 5. Initialisation du contexte d'interaction (Strategy/State)
        contexte = new ContexteMode(perspective, controleur, gestionnaireAnnotations, gestionnaireCrop);
        if (vuePreview != null) contexte.setVueAnnotations(vuePreview);

        gestionnaireOutils = new GestionnaireOutils(panelNavigation, contexte);

        if (vuePreview != null) {
            vuePreview.setGestionnaireOutils(gestionnaireOutils);
            vuePreview.actualiser();
        }

        add(panelNavigation, BorderLayout.CENTER);

        // 6. Démarrage de l'autosave et premier affichage
        initialiserAutosave();
        actualiser();
    }

    /**
     * Crée le panneau principal de navigation (zone centrale).
     * Ce panneau gère l'affichage de l'image, des annotations et des overlays.
     */
    private void creerPanelNavigation() {
        panelNavigation = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imageChargee == null || perspective == null) return;

                Graphics2D g2d = (Graphics2D) g;
                // Activation de l'antialiasing pour un rendu fluide
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                     RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                     RenderingHints.VALUE_ANTIALIAS_ON);

                // Calcul des transformations (Zoom/Pan)
                double zoom = perspective.getZoom();
                int largeur = (int) (imageChargee.getWidth() * zoom);
                int hauteur = (int) (imageChargee.getHeight() * zoom);
                // Centrage de l'image + décalage (pan)
                int x = (int) ((getWidth() - largeur) / 2 + perspective.getCentreX());
                int y = (int) ((getHeight() - hauteur) / 2 + perspective.getCentreY());

                // Dessin de l'image de fond
                g2d.drawImage(imageChargee, x, y, largeur, hauteur, this);

                // Création d'un contexte graphique transformé pour les éléments vectoriels
                Graphics2D g2dTransformed = (Graphics2D) g2d.create();
                g2dTransformed.translate(x, y);
                g2dTransformed.scale(zoom, zoom);

                // 1. Dessin des annotations existantes
                if (gestionnaireAnnotations != null) {
                    for (Annotation annotation : gestionnaireAnnotations.getAnnotations()) {
                        annotation.dessiner(g2dTransformed);
                    }
                }

                // 2. Aperçu de l'outil ligne (en cours de tracé)
                if (gestionnaireOutils != null && gestionnaireOutils.getModeLigne() != null) {
                    Annotation preview = gestionnaireOutils.getModeLigne().getOutil().getAnnotationPreview();
                    if (preview != null) preview.dessiner(g2dTransformed);
                }

                // 3. Dessin du Crop validé
                if (gestionnaireCrop != null && gestionnaireCrop.hasCrop()) {
                    Rectangle cropRect = gestionnaireCrop.getZoneCrop();
                    g2dTransformed.setColor(Color.RED);
                    g2dTransformed.setStroke(new BasicStroke(4));
                    g2dTransformed.drawRect(cropRect.x, cropRect.y, cropRect.width, cropRect.height);
                }

                // 4. Aperçu de l'outil Crop (en cours de sélection)
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
        panelNavigation.setLayout(null); // Layout absolu pour positionner les overlays manuellement

        // Ajout de la Minimap (Overlay)
        minimap = new MinimapOverlay(perspective, controleur, panelNavigation);
        panelNavigation.add(minimap);

        // Repositionnement des overlays lors du redimensionnement
        panelNavigation.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                positionnerOverlays();
            }
        });

        // Zoom à la molette
        panelNavigation.addMouseWheelListener(e -> {
            if (perspective != null) {
                double facteur = Math.pow(1.1, -e.getWheelRotation());
                controleur.zoomerAvecFacteur(perspective, facteur);
                programmerAutosave();
                actualiser();
            }
        });
    }

    /**
     * Crée la vue secondaire pour les annotations et la prévisualisation du crop.
     */
    private void creerVueAnnotations() {
        vuePreview = new VueAnnotations(perspective, gestionnaireAnnotations, gestionnaireCrop);
        panelNavigation.add(vuePreview);
    }

    /**
     * Positionne la minimap et la vue preview dans les coins du panel de navigation.
     */
    private void positionnerOverlays() {
        positionnerMinimap();
        if (vuePreview != null && panelNavigation != null) {
            int margin = 15;
            int x = panelNavigation.getWidth() - vuePreview.getPreferredSize().width - margin;
            int y = margin; // Coin supérieur droit
            vuePreview.setBounds(x, y, vuePreview.getPreferredSize().width,
                    vuePreview.getPreferredSize().height);
        }
    }

    /**
     * Crée la barre d'outils latérale avec les boutons de contrôle.
     */
    private void creerToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.Y_AXIS));
        toolbar.setBorder(BorderFactory.createTitledBorder("Outils"));

        // Labels d'information
        labelZoom = new JLabel("Zoom: 1.00");
        labelPos = new JLabel("Pos: (0, 0)");
        labelImage = new JLabel("Image: -");
        toolbar.add(labelZoom);
        toolbar.add(labelPos);
        toolbar.add(labelImage);
        toolbar.add(Box.createVerticalStrut(10));

        // --- Bouton Charger Image ---
        JButton btnChargerImage = new JButton("Charger Image");
        btnChargerImage.setMaximumSize(new Dimension(150, 30));
        btnChargerImage.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("resources/test_images"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                perspective.setCheminImage(selectedFile.getAbsolutePath());
                programmerAutosave();
                actualiser();
            }
        });
        toolbar.add(btnChargerImage);
        toolbar.add(Box.createVerticalStrut(10));

        // --- Bouton Sauvegarder Perspective ---
        JButton btnSauvegarderPerspective = new JButton("Sauvegarder Perspective");
        btnSauvegarderPerspective.setMaximumSize(new Dimension(150, 30));
        btnSauvegarderPerspective.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Fichiers JSON", "json"));
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File fichier = fileChooser.getSelectedFile();
                String chemin = fichier.getAbsolutePath();
                if (!chemin.toLowerCase().endsWith(".json")) {
                    chemin += ".json";
                }
                controleur.sauvegarderPerspectiveActuelle(chemin);
                JOptionPane.showMessageDialog(this, "Sauvegarde effectuée avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        toolbar.add(btnSauvegarderPerspective);
        toolbar.add(Box.createVerticalStrut(5));

        // --- Bouton Charger Perspective (Async) ---
        JButton btnChargerPerspective = new JButton("Charger Perspective");
        btnChargerPerspective.setMaximumSize(new Dimension(150, 30));
        btnChargerPerspective.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Fichiers JSON", "json"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File fichier = fileChooser.getSelectedFile();

                // Dialogue de chargement modal
                JDialog loadingDialog = new JDialog(this, "Chargement", true);
                loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                JPanel p = new JPanel(new BorderLayout(10, 10));
                p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                p.add(new JLabel("Chargement de la perspective..."), BorderLayout.NORTH);
                JProgressBar progressBar = new JProgressBar();
                progressBar.setIndeterminate(true);
                p.add(progressBar, BorderLayout.CENTER);
                loadingDialog.add(p);
                loadingDialog.pack();
                loadingDialog.setLocationRelativeTo(this);

                // Tâche de fond (SwingWorker) pour ne pas bloquer l'interface
                SwingWorker<PerspectiveLeaf, Void> worker = new SwingWorker<>() {
                    @Override
                    protected PerspectiveLeaf doInBackground() throws Exception {
                        return controleur.chargerPerspective(fichier.getAbsolutePath());
                    }

                    @Override
                    protected void done() {
                        loadingDialog.dispose();
                        try {
                            PerspectiveLeaf p = get();
                            if (p != null) {
                                perspective = p;
                                perspective.attacher(MainGUI.this);
                                // Mise à jour du contexte avec la nouvelle perspective
                                contexte = new ContexteMode(perspective, controleur, gestionnaireAnnotations, gestionnaireCrop);
                                if (vuePreview != null) contexte.setVueAnnotations(vuePreview);
                                if (imageChargee != null) contexte.setImageChargee(imageChargee);
                                
                                // Nettoyage des commandes de la session précédente pour éviter les incohérences
                                GestionnaireCommandes.getInstance().clear();
                                
                                // Synchronisation du GestionnaireCrop avec la perspective chargée
                                if (perspective.getCrop() != null) {
                                    gestionnaireCrop.definirCrop(perspective.getCrop().getZone());
                                } else {
                                    gestionnaireCrop.definirCrop(null);
                                }

                                gestionnaireOutils.setContexte(contexte);
                                if (vuePreview != null) {
                                    vuePreview.setPerspective(perspective);
                                    vuePreview.actualiser();
                                }
                                actualiser();
                                JOptionPane.showMessageDialog(MainGUI.this, "Chargement terminé !");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(MainGUI.this, "Erreur lors du chargement : " + ex.getMessage());
                        }
                    }
                };
                
                worker.execute();
                loadingDialog.setVisible(true);
            }
        });
        toolbar.add(btnChargerPerspective);
        toolbar.add(Box.createVerticalStrut(10));

        // --- Outils (Crop / Ligne) ---
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

        // --- Undo / Redo (Stratégie Hybride) ---
        JButton btnUndo = new JButton("Undo (Ctrl+Z)");
        btnUndo.setMaximumSize(new Dimension(150, 30));
        btnUndo.addActionListener(e -> {
            // Priorité aux commandes de session (annotations), sinon historique interne du modèle (mouvements/crop)
            if (!GestionnaireCommandes.getInstance().isEmpty()) {
                GestionnaireCommandes.getInstance().undo();
            } else if (perspective != null) {
                perspective.undo();
            }
            programmerAutosave();
            actualiser();
        });
        toolbar.add(btnUndo);

        JButton btnRedo = new JButton("Redo (Ctrl+Y)");
        btnRedo.setMaximumSize(new Dimension(150, 30));
        btnRedo.addActionListener(e -> {
            if (!GestionnaireCommandes.getInstance().isRedoEmpty()) {
                GestionnaireCommandes.getInstance().redo();
            } else if (perspective != null) {
                perspective.redo();
            }
            programmerAutosave();
            actualiser();
        });
        toolbar.add(btnRedo);

        // --- Effacer tout ---
        JButton btnEffacerAnnotations = new JButton("Effacer tout");
        btnEffacerAnnotations.setMaximumSize(new Dimension(150, 30));
        btnEffacerAnnotations.addActionListener(e -> {
            gestionnaireAnnotations.effacerTout();
            programmerAutosave();
            panelNavigation.repaint();
        });
        toolbar.add(btnEffacerAnnotations);

        toolbar.add(Box.createVerticalGlue());
        add(toolbar, BorderLayout.EAST);
    }

    /**
     * Configure les raccourcis clavier globaux (Ctrl+Z, Ctrl+Y).
     */
    private void configurerRaccourcis() {
        int shortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('Z', shortcutMask), "undo");
        getRootPane().getActionMap().put("undo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!GestionnaireCommandes.getInstance().isEmpty()) {
                    GestionnaireCommandes.getInstance().undo();
                } else if (perspective != null) {
                    perspective.undo();
                }
                programmerAutosave();
                actualiser();
            }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('Y', shortcutMask), "redo");
        getRootPane().getActionMap().put("redo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!GestionnaireCommandes.getInstance().isRedoEmpty()) {
                    GestionnaireCommandes.getInstance().redo();
                } else if (perspective != null) {
                    perspective.redo();
                }
                programmerAutosave();
                actualiser();
            }
        });
    }

    /**
     * Positionne la minimap en bas à droite.
     */
    private void positionnerMinimap() {
        if (minimap != null && panelNavigation != null) {
            int margin = 15;
            int x = panelNavigation.getWidth() - minimap.getPreferredSize().width - margin;
            int y = panelNavigation.getHeight() - minimap.getPreferredSize().height - margin;
            minimap.setBounds(x, y, minimap.getPreferredSize().width, minimap.getPreferredSize().height);
        }
    }

    // ---------------------- AUTOSAVE -----------------------
    private void initialiserAutosave() {
        File dossier = new File("perspectives");
        if (!dossier.exists()) dossier.mkdirs();

        autosaveTimer = new javax.swing.Timer(2000, e -> {
            controleur.sauvegarderPerspectiveActuelle(cheminAutosave);
        });
        autosaveTimer.setRepeats(false);
    }

    private void programmerAutosave() {
        if (autosaveTimer != null) autosaveTimer.restart();
    }

    // ---------------------- ACTUALISER (Observer) -----------------------
    /**
     * Méthode appelée lorsque le modèle notifie un changement.
     * Met à jour l'affichage, synchronise les vues et charge l'image si nécessaire.
     */
    @Override
    public void actualiser() {
        if (perspective == null) return;
        
        // Synchronisation du crop visuel avec le modèle (pour Undo/Redo)
        // Si un Undo a restauré un ancien état de crop dans le modèle, on doit mettre à jour le gestionnaire de crop
        if (gestionnaireCrop != null) {
            Rectangle modelCrop = (perspective.getCrop() != null) ? perspective.getCrop().getZone() : null;
            Rectangle viewCrop = gestionnaireCrop.getZoneCrop();
            
            // On met à jour seulement si différent pour éviter les boucles de notification infinies
            boolean different = (modelCrop == null && viewCrop != null) ||
                                (modelCrop != null && viewCrop == null) ||
                                (modelCrop != null && !modelCrop.equals(viewCrop));
            
            if (different) {
                gestionnaireCrop.definirCrop(modelCrop);
            }
        }

        // Mise à jour des labels
        labelZoom.setText("Zoom: " + String.format("%.2f", perspective.getZoom()));
        labelPos.setText("Position: (" + String.format("%.1f", perspective.getCentreX()) + ", " +
                String.format("%.1f", perspective.getCentreY()) + ")");
        
        // Chargement intelligent de l'image (cache)
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
        
        // Redessiner l'interface
        panelNavigation.repaint();
        positionnerOverlays();
    }

    // --- Gestion des modes d'outils ---

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

    /**
     * Point d'entrée de l'application.
     * @param args Arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainGUI app = new MainGUI();
            app.setVisible(true);
        });
    }
}
