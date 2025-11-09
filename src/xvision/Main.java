package xvision;

import xvision.model.*;
import xvision.controller.*;

public class Main {
    public static void main(String[] args) {
        GestionnaireDePerspectives gestionnaire = new GestionnaireDePerspectives();
        ControleurPerspective controleur = new ControleurPerspective(gestionnaire);

        // Ajout de perspectives simples
        controleur.creerPerspective(1.0, 100, 200);
        controleur.creerPerspective(2.0, 300, 400);

        // Création d’un sous-groupe
        PerspectiveComposite groupe = gestionnaire.creerSousGroupe();
        groupe.ajouter(new PerspectiveLeaf(1.5, 50, 75));

        // Affichage de la hiérarchie
        controleur.afficherToutes();
    }
}
