package xvision.controller;

import xvision.model.PerspectiveLeaf;

public class CommandeTranslation implements Commande {
    private PerspectiveLeaf perspective;
    private double ancienX, ancienY;
    private double nouveauX, nouveauY;

    public CommandeTranslation(PerspectiveLeaf p, double x, double y) {
        this.perspective = p;
        this.nouveauX = x;
        this.nouveauY = y;
    }

    @Override
    public void executer() {
        ancienX = perspective.getCentreX();
        ancienY = perspective.getCentreY();
        perspective.setCentre(nouveauX, nouveauY);
        perspective.notifierObservateurs();
    }

    @Override
    public void annuler() {
        perspective.setCentre(ancienX, ancienY);
        perspective.notifierObservateurs();
    }
}
