package xvision;

import xvision.model.GestionnaireDePerspectives;
import xvision.model.PerspectiveLeaf;
import xvision.view.VuePrincipale;
import xvision.view.Vignette;
import xvision.controller.ControleurPerspective;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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
            frame.setSize(900, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout(10, 10));

            // Panneau de contrôle
            JPanel panelControle = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            panelControle.setBorder(BorderFactory.createTitledBorder("Contrôles"));
            frame.add(panelControle, BorderLayout.NORTH);

            // Panneau principal pour les vues
            JPanel panelVues = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            panelVues.setBorder(BorderFactory.createTitledBorder("Vues (Observateurs)"));
            frame.add(panelVues, BorderLayout.CENTER);

            // ===== BOUTONS =====
            JButton btnCreer = new JButton("Créer Perspective");
            JButton btnOuvrir = new JButton("Ouvrir fenêtre");
            JButton btnChargerImage = new JButton("Charger Image");
            JButton btnZoom = new JButton("Modifier Zoom");
            JButton btnDeplacer = new JButton("Déplacer");
            JButton btnUndo = new JButton("Undo");
            JButton btnRedo = new JButton("Redo");

            panelControle.add(new JLabel("Actions:"));
            panelControle.add(btnCreer);
            panelControle.add(btnOuvrir);
            panelControle.add(new JSeparator(SwingConstants.VERTICAL));
            panelControle.add(btnChargerImage);
            panelControle.add(new JSeparator(SwingConstants.VERTICAL));
            panelControle.add(btnZoom);
            panelControle.add(new JSeparator(SwingConstants.VERTICAL));
            panelControle.add(btnDeplacer);
            panelControle.add(new JSeparator(SwingConstants.VERTICAL));
            panelControle.add(btnUndo);
            panelControle.add(new JSeparator(SwingConstants.VERTICAL));
            panelControle.add(btnRedo);

            // ===== REFERENCES =====
            final PerspectiveLeaf[] perspectiveCourante = new PerspectiveLeaf[1];
            final Map<PerspectiveLeaf, VuePrincipale> fenetresOuvertes = new HashMap<>();
            List<PerspectiveLeaf> toutesPerspectives = new ArrayList<>();

            // ---- ACTIONS ----
            btnCreer.addActionListener(e -> {
                // Création de la perspective
                PerspectiveLeaf perspective = controleur.creerPerspective(1.0, 0, 0);
                perspectiveCourante[0] = perspective;
                toutesPerspectives.add(perspective);

                // Créer la vignette
                Vignette vignette = new Vignette(perspective);
                panelVues.add(vignette);
                panelVues.revalidate();
                panelVues.repaint();

                // Double-clic sur vignette pour ouvrir ou sélectionner la fenêtre
                vignette.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent me) {
                        if (me.getClickCount() == 2) {
                            ouvrirFenetrePerspective(perspective, fenetresOuvertes, controleur);
                        } else {
                            perspectiveCourante[0] = perspective;
                        }
                    }
                });

                // ===== OUVERTURE AUTOMATIQUE =====
                VuePrincipale vue = ouvrirFenetrePerspective(perspective, fenetresOuvertes, controleur);
                vue.toFront();             
                vue.requestFocus();        
                perspectiveCourante[0] = perspective; 

                JOptionPane.showMessageDialog(frame,
                        "Perspective créée avec 1 vignette!\nLa fenêtre a été ouverte et sélectionnée automatiquement.",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            });

            btnOuvrir.addActionListener(e -> {
                if (perspectiveCourante[0] != null) {
                    ouvrirFenetrePerspective(perspectiveCourante[0], fenetresOuvertes, controleur);
                } else {
                    JOptionPane.showMessageDialog(frame, "Créez ou sélectionnez d'abord une perspective!");
                }
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
                    }
                }
            });

            btnZoom.addActionListener(e -> {
                if (perspectiveCourante[0] != null) {
                    String input = JOptionPane.showInputDialog(frame, "Nouveau zoom:", "2.0");
                    if (input != null) {
                        double zoom = Double.parseDouble(input);
                        controleur.zoomer(perspectiveCourante[0], zoom);
                    }
                }
            });

            btnDeplacer.addActionListener(e -> {
                if (perspectiveCourante[0] != null) {
                    String inputX = JOptionPane.showInputDialog(frame, "Nouvelle position X:", "100");
                    String inputY = JOptionPane.showInputDialog(frame, "Nouvelle position Y:", "50");
                    if (inputX != null && inputY != null) {
                        double x = Double.parseDouble(inputX);
                        double y = Double.parseDouble(inputY);
                        controleur.deplacer(perspectiveCourante[0], x, y);
                    }
                }
            });

            btnUndo.addActionListener(e -> {
                if (perspectiveCourante[0] != null) perspectiveCourante[0].undo();
                else toutesPerspectives.forEach(PerspectiveLeaf::undo);
            });

            btnRedo.addActionListener(e -> {
                if (perspectiveCourante[0] != null) perspectiveCourante[0].redo();
                else toutesPerspectives.forEach(PerspectiveLeaf::redo);
            });

            // Clic sur le panneau principal pour désélectionner la perspective
            panelVues.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    perspectiveCourante[0] = null;
                }
            });

            frame.setVisible(true);
        });
    }

    /**
     * Ouvre la fenêtre VuePrincipale pour la perspective donnée.
     * Si elle existe déjà, la met en avant.
     * Nécessite le contrôleur pour initialiser la vue.
     */
    private static VuePrincipale ouvrirFenetrePerspective(PerspectiveLeaf perspective,
                                                          Map<PerspectiveLeaf, VuePrincipale> fenetres,
                                                          ControleurPerspective controleur) {
        if (fenetres.containsKey(perspective)) {
            VuePrincipale vue = fenetres.get(perspective);
            vue.toFront();
            vue.requestFocus();
            return vue;
        } else {
            VuePrincipale vue = new VuePrincipale(perspective, controleur);
            fenetres.put(perspective, vue);
            vue.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    fenetres.remove(perspective);
                }
            });
            return vue;
        }
    }
}
