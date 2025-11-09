package xvision.controller;

import xvision.model.PerspectiveLeaf;

public class CommandeZoom implements Commande {
    private PerspectiveLeaf perspective;
    private double ancienZoom;
    private double nouveauZoom;

    public CommandeZoom(PerspectiveLeaf p, double zoom) {
        this.perspective = p;
        this.nouveauZoom = zoom;
    }

    @Override
    public void executer() {
        ancienZoom = perspective.getZoom();
        perspective.setZoom(nouveauZoom);
        perspective.notifierObservateurs();
    }

    @Override
    public void annuler() {
        perspective.setZoom(ancienZoom);
        perspective.notifierObservateurs();
    }
}
