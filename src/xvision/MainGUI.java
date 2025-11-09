package xvision;

import xvision.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainGUI {

    public static void main(String[] args) {
        // Création du gestionnaire
        GestionnaireDePerspectives gestionnaire = new GestionnaireDePerspectives();

        // Interface Swing
        JFrame frame = new JFrame("X-Vision");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        JTextField zoomField = new JTextField("1.0");
        JTextField xField = new JTextField("0");
        JTextField yField = new JTextField("0");
        JButton ajouterBtn = new JButton("Ajouter perspective");
        JButton afficherBtn = new JButton("Afficher toutes");
        JButton sauvegarderBtn = new JButton("Sauvegarder toutes");

        panel.add(new JLabel("Zoom:"));
        panel.add(zoomField);
        panel.add(new JLabel("X:"));
        panel.add(xField);
        panel.add(new JLabel("Y:"));
        panel.add(yField);

        frame.add(panel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(ajouterBtn);
        bottomPanel.add(afficherBtn);
        bottomPanel.add(sauvegarderBtn);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        ajouterBtn.addActionListener(e -> {
            double zoom = Double.parseDouble(zoomField.getText());
            double x = Double.parseDouble(xField.getText());
            double y = Double.parseDouble(yField.getText());
            String id = "P" + System.currentTimeMillis();
            PerspectiveLeaf p = new PerspectiveLeaf(zoom, x, y);
            gestionnaire.ajouterPerspective(id, p);
            JOptionPane.showMessageDialog(frame, "Perspective ajoutée: " + id);
        });

        afficherBtn.addActionListener(e -> gestionnaire.afficherToutes());

        sauvegarderBtn.addActionListener(e -> {
            gestionnaire.sauvegarderToutes("perspectives");
            JOptionPane.showMessageDialog(frame, "Perspectives sauvegardées !");
        });

        frame.setVisible(true);
    }
}
