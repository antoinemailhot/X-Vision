package xvision.view;

import javax.swing.*;
import xvision.model.PerspectiveLeaf;

public class VueMini extends JFrame implements ObserverIF {
    private PerspectiveLeaf perspective;
    private JLabel label;

    public VueMini(PerspectiveLeaf p) {
        this.perspective = p;
        setTitle("Mini Vue"); setSize(200,100);
        label = new JLabel();
        add(label);
        setVisible(true);
        actualiser();
    }

    @Override
    public void actualiser() {
        label.setText("[Mini] Zoom=" + perspective.getZoom() +
                      ", Pos=(" + perspective.getCentreX() + "," + perspective.getCentreY() + ")");
    }
}
