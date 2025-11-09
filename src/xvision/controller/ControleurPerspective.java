package xvision.controller;

import xvision.model.*;

public class ControleurPerspective {
    private GestionnaireDePerspectives gestionnaire;

    public ControleurPerspective(GestionnaireDePerspectives gestionnaire) {
        this.gestionnaire = gestionnaire;
    }

    public void creerPerspective(double zoom, double x, double y) {
        gestionnaire.ajouterPerspectiveSimple(zoom, x, y);
    }

    public void afficherToutes() {
        gestionnaire.afficherToutes();
    }
}
