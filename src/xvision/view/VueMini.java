package xvision.view;

import javax.swing.*;
import xvision.model.PerspectiveLeaf;

/**
 * Vue miniature alternative d'une perspective
 * Implémente le patron Observer (GoF)
 * Principe GRASP: Low Coupling - Observe via l'interface
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class VueMini extends JFrame implements ObserverIF {
    private PerspectiveLeaf perspective;
    private JLabel label;

    /**
     * Constructeur de la vue miniature
     * S'enregistre automatiquement comme observateur
     *
     * @param p La perspective à observer
     */
    public VueMini(PerspectiveLeaf p) {
        super();
        this.perspective = p;

        // Enregistrement comme observateur (Patron Observer)
        if (p != null) {
            p.attacher(this);
        }

        setTitle("Mini Vue");
        setSize(200, 100);
        label = new JLabel();
        add(label);
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
            String cheminImage = perspective.getCheminImage();
            String nomImage = (cheminImage != null && !cheminImage.isEmpty())
                ? java.nio.file.Paths.get(cheminImage).getFileName().toString()
                : "Aucune";

            label.setText("<html>[Mini] Zoom=" + perspective.getZoom() +
                          "<br>Pos=(" + perspective.getCentreX() + "," + perspective.getCentreY() + ")" +
                          "<br>Image: " + nomImage + "</html>");
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
