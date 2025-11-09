package xvision;

import xvision.model.*;
import xvision.controller.*;

/**
 * Classe principale du projet XVision.
 * 
 * Cette classe illustre l’architecture MVC et l’application de plusieurs patrons :
 *  - GRASP (Controller, Information Expert, Low Coupling / High Cohesion)
 *  - GoF (Composite pour la hiérarchie de perspectives)
 * 
 * Le contrôleur agit comme intermédiaire entre le modèle (GestionnaireDePerspectives)
 * et la logique de création/affichage des perspectives.
 */
public class Main {
    public static void main(String[] args) {
        // --- Initialisation du modèle et du contrôleur ---
        GestionnaireDePerspectives gestionnaire = new GestionnaireDePerspectives();
        ControleurPerspective controleur = new ControleurPerspective(gestionnaire);

        // --- Création de perspectives simples ---
        controleur.creerPerspective(1.0, 100, 200);
        controleur.creerPerspective(2.0, 300, 400);

        // --- Création d’un sous-groupe de perspectives (Composite) ---
        PerspectiveComposite groupe = gestionnaire.creerSousGroupe();
        groupe.ajouter(new PerspectiveLeaf(1.5, 50, 75));

        // --- Affichage de la hiérarchie complète ---
        controleur.afficherToutes();
    }
}
