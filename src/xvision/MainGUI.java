package xvision;

import xvision.model.*;
import xvision.view.*;
import xvision.controller.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Point d'entrée principal de l'application X-Vision
 * Architecture MVC stricte: initialise le modèle, les vues et le contrôleur
 */
public class MainGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // ===== MODÈLE =====
            GestionnaireDePerspectives gestionnaire = new GestionnaireDePerspectives();

            // ===== CONTRÔLEUR =====
            ControleurPerspective controleur = new ControleurPerspective(gestionnaire);

            // ===== INTERFACE PRINCIPALE =====
            JFrame frame = new JFrame("X-Vision - Démo Observer + MVC");
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout(10, 10));

            // Panneau de contrôle
            JPanel panelControle = creerPanneauControle();
            frame.add(panelControle, BorderLayout.NORTH);

            // Panneau principal pour les vues
            JPanel panelVues = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            panelVues.setBorder(BorderFactory.createTitledBorder("Vues (Observateurs)"));
            frame.add(panelVues, BorderLayout.CENTER);

            // Références
            final PerspectiveLeaf[] perspectiveCourante = new PerspectiveLeaf[1]; // perspective sélectionnée
            List<PerspectiveLeaf> toutesPerspectives = new ArrayList<>(); // toutes les perspectives

            // ===== BOUTONS =====
            JButton btnCreer = (JButton) panelControle.getComponent(1);
            JButton btnChargerImage = (JButton) panelControle.getComponent(3);
            JButton btnZoom = (JButton) panelControle.getComponent(5);
            JButton btnDeplacer = (JButton) panelControle.getComponent(7);
            JButton btnUndo = (JButton) panelControle.getComponent(9);
            JButton btnRedo = (JButton) panelControle.getComponent(11);

            // ---- ACTIONS ----
            btnCreer.addActionListener(e -> {
                PerspectiveLeaf perspective = controleur.creerPerspective(1.0, 0, 0);
                perspectiveCourante[0] = perspective;
                toutesPerspectives.add(perspective);

                // Créer les vues
                VuePrincipale vuePrincipale = new VuePrincipale(perspective);
                VueMini vueMini = new VueMini(perspective);
                Vignette vignette = new Vignette(perspective);

                // Sélection de la perspective au clic
                vignette.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        perspectiveCourante[0] = perspective;
                        System.out.println("[MainGUI] Perspective sélectionnée: " + perspective.getIdPerspective());
                    }
                });

                panelVues.add(vignette);
                panelVues.revalidate();
                panelVues.repaint();

                JOptionPane.showMessageDialog(frame,
                        "Perspective créée avec 3 vues observatrices!\n" +
                                "Nombre d'observateurs: " + perspective.getNombreObservateurs(),
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            });

            btnChargerImage.addActionListener(e -> {
                if (perspectiveCourante[0] != null) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("ressources"));
                    fileChooser.setDialogTitle("Sélectionner une image test");
                    fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                            "Images", "jpg", "jpeg", "png", "gif", "bmp"));

                    int result = fileChooser.showOpenDialog(frame);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        perspectiveCourante[0].setCheminImage(selectedFile.getAbsolutePath());
                        JOptionPane.showMessageDialog(frame,
                                "Image chargée: " + selectedFile.getName() + "\n" +
                                        "Toutes les vues ont été notifiées.",
                                "Image chargée", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Créez d'abord une perspective!");
                }
            });

            btnZoom.addActionListener(e -> {
                if (perspectiveCourante[0] != null) {
                    String input = JOptionPane.showInputDialog(frame, "Nouveau zoom:", "2.0");
                    if (input != null) {
                        double zoom = Double.parseDouble(input);
                        controleur.zoomer(perspectiveCourante[0], zoom);
                        JOptionPane.showMessageDialog(frame, "Zoom appliqué! Toutes les vues ont été notifiées.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Créez d'abord une perspective!");
                }
            });

            btnDeplacer.addActionListener(e -> {
                if (perspectiveCourante[0] != null) {
                    String inputX = JOptionPane.showInputDialog(frame, "Nouvelle position X:", "100");
                    if (inputX != null) {
                        String inputY = JOptionPane.showInputDialog(frame, "Nouvelle position Y:", "50");
                        if (inputY != null) {
                            double x = Double.parseDouble(inputX);
                            double y = Double.parseDouble(inputY);
                            controleur.deplacer(perspectiveCourante[0], x, y);
                            JOptionPane.showMessageDialog(frame, "Position modifiée! Toutes les vues ont été notifiées.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Créez d'abord une perspective!");
                }
            });

            btnUndo.addActionListener(e -> {
                if (perspectiveCourante[0] != null) {
                    perspectiveCourante[0].undo();
                } else {
                    toutesPerspectives.forEach(PerspectiveLeaf::undo);
                }
            });

            btnRedo.addActionListener(e -> {
                if (perspectiveCourante[0] != null) {
                    perspectiveCourante[0].redo();
                } else {
                    toutesPerspectives.forEach(PerspectiveLeaf::redo);
                }
            });

            // ===== KEY BINDINGS =====
            JRootPane rootPane = frame.getRootPane();
            InputMap im = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap am = rootPane.getActionMap();
            int shortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

            im.put(KeyStroke.getKeyStroke('Z', shortcutMask), "undo");
            am.put("undo", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (perspectiveCourante[0] != null) {
                        perspectiveCourante[0].undo();
                    } else {
                        toutesPerspectives.forEach(PerspectiveLeaf::undo);
                    }
                }
            });

            im.put(KeyStroke.getKeyStroke('Y', shortcutMask), "redo");
            am.put("redo", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (perspectiveCourante[0] != null) {
                        perspectiveCourante[0].redo();
                    } else {
                        toutesPerspectives.forEach(PerspectiveLeaf::redo);
                    }
                }
            });

            // ===== MENU EDITION =====
            JMenuBar menuBar = new JMenuBar();
            JMenu menuEdition = new JMenu("Édition");

            JMenuItem undoMenuItem = new JMenuItem("Undo");
            undoMenuItem.setAccelerator(KeyStroke.getKeyStroke('Z', shortcutMask));
            undoMenuItem.addActionListener(e -> {
                if (perspectiveCourante[0] != null) perspectiveCourante[0].undo();
                else toutesPerspectives.forEach(PerspectiveLeaf::undo);
            });

            JMenuItem redoMenuItem = new JMenuItem("Redo");
            redoMenuItem.setAccelerator(KeyStroke.getKeyStroke('Y', shortcutMask));
            redoMenuItem.addActionListener(e -> {
                if (perspectiveCourante[0] != null) perspectiveCourante[0].redo();
                else toutesPerspectives.forEach(PerspectiveLeaf::redo);
            });

            menuEdition.add(undoMenuItem);
            menuEdition.add(redoMenuItem);
            menuBar.add(menuEdition);
            frame.setJMenuBar(menuBar);

            frame.setVisible(true);

            // Message de bienvenue
            JOptionPane.showMessageDialog(frame,
                    "Bienvenue dans X-Vision!\n\n" +
                            "1. Créez une perspective (3 vues observatrices)\n" +
                            "2. Modifiez le zoom ou la position\n" +
                            "3. Observez la mise à jour automatique des vues!\n" +
                            "4. Testez Undo/Redo\n" +
                            "Cliquez sur une vignette pour sélectionner une perspective ou sur le panneau principal pour agir sur toutes.",
                    "X-Vision - Démo", JOptionPane.INFORMATION_MESSAGE);

            // Clic sur panneau principal désélectionne la perspective
            panelVues.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    perspectiveCourante[0] = null;
                    System.out.println("[MainGUI] Aucune perspective sélectionnée");
                }
            });

        });
    }

    private static JPanel creerPanneauControle() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Contrôles"));

        panel.add(new JLabel("Actions:"));
        panel.add(new JButton("Créer Perspective"));
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(new JButton("Charger Image"));
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(new JButton("Modifier Zoom"));
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(new JButton("Déplacer"));
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(new JButton("Undo"));
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(new JButton("Redo"));

        return panel;
    }
}
