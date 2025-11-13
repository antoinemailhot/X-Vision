package xvision;

import xvision.model.*;
import xvision.view.*;
import xvision.controller.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Point d'entrée principal de l'application X-Vision
 * Architecture MVC stricte: initialise le modèle, les vues et le contrôleur
 * Principe GRASP: Low Coupling - Sépare les responsabilités
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
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

            // Panneau de contrôle (en haut)
            JPanel panelControle = creerPanneauControle(controleur, frame);
            frame.add(panelControle, BorderLayout.NORTH);

            // Panneau principal (centre) - Emplacement pour les vues
            JPanel panelVues = new JPanel();
            panelVues.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
            panelVues.setBorder(BorderFactory.createTitledBorder("Vues (Observateurs)"));
            frame.add(panelVues, BorderLayout.CENTER);

            // Référence à la perspective courante (pour les tests)
            final PerspectiveLeaf[] perspectiveCourante = new PerspectiveLeaf[1];

            // Actions des boutons
            JButton btnCreer = (JButton) panelControle.getComponent(1);
            JButton btnChargerImage = (JButton) panelControle.getComponent(3);
            JButton btnZoom = (JButton) panelControle.getComponent(5);
            JButton btnDeplacer = (JButton) panelControle.getComponent(7);
            JButton btnUndo = (JButton) panelControle.getComponent(9);
            JButton btnRedo = (JButton) panelControle.getComponent(11);

            btnCreer.addActionListener(e -> {
                // Créer une nouvelle perspective via le contrôleur
                PerspectiveLeaf perspective = controleur.creerPerspective(1.0, 0, 0);
                perspectiveCourante[0] = perspective;

                // Créer les vues qui observent cette perspective
                VuePrincipale vuePrincipale = new VuePrincipale(perspective);
                VueMini vueMini = new VueMini(perspective);
                Vignette vignette = new Vignette(perspective);

                // Ajouter la vignette au panneau
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
                        // Modification via le contrôleur (Patron Command)
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
                            // Modification via le contrôleur (Patron Command)
                            controleur.deplacer(perspectiveCourante[0], x, y);
                            JOptionPane.showMessageDialog(frame, "Position modifiée! Toutes les vues ont été notifiées.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Créez d'abord une perspective!");
                }
            });

            btnUndo.addActionListener(e -> {
                controleur.annulerDerniereAction();
                System.out.println("[MainGUI] Undo effectué");
            });

            btnRedo.addActionListener(e -> {
                controleur.refaireDerniereAction();
                System.out.println("[MainGUI] Redo effectué");
            });

            frame.setVisible(true);

            // Message de bienvenue
            JOptionPane.showMessageDialog(frame,
                "Bienvenue dans X-Vision!\n\n" +
                "Architecture MVC + Patron Observer\n\n" +
                "1. Créez une perspective (crée 3 vues observatrices)\n" +
                "2. Modifiez le zoom ou la position\n" +
                "3. Observez la mise à jour automatique des vues!\n" +
                "4. Testez Undo/Redo",
                "X-Vision - Démo", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    /**
     * Crée le panneau de contrôle avec les boutons d'action
     * @param controleur Le contrôleur à utiliser
     * @param frame La fenêtre parente
     * @return Le panneau de contrôle
     */
    private static JPanel creerPanneauControle(ControleurPerspective controleur, JFrame frame) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
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
