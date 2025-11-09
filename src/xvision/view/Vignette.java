package xvision.view;

import xvision.model.PerspectiveLeaf;

public class Vignette implements ObserverIF {

    private PerspectiveLeaf perspective;

    public Vignette(PerspectiveLeaf p) { this.perspective = p; }

    @Override
    public void actualiser() {
        System.out.println("[Vignette] Zoom=" + perspective.getZoom() +
                           ", Pos=(" + perspective.getCentreX() + "," + perspective.getCentreY() + ")");
    }
}
