package xvision.view;

import javax.swing.*;
import xvision.model.PerspectiveLeaf;
import java.awt.*;

public class VuePrincipale extends JFrame implements ObserverIF {

    private PerspectiveLeaf perspective;
    private JLabel labelZoom, labelPos;

    public VuePrincipale(PerspectiveLeaf p) {
        this.perspective = p;
        setTitle("Vue Principale - X-Vision");
        setSize(400,200);
        setLayout(new GridLayout(2,1));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        labelZoom = new JLabel();
        labelPos = new JLabel();
        add(labelZoom); add(labelPos);

        setVisible(true);
        actualiser();
    }

    @Override
    public void actualiser() {
        labelZoom.setText("Zoom: " + perspective.getZoom());
        labelPos.setText("Position: (" + perspective.getCentreX() + "," + perspective.getCentreY() + ")");
    }
}
