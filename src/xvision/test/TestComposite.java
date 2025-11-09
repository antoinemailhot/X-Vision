package xvision.test;

import xvision.model.*;

/**
 * Test unitaire du patron Composite pour les perspectives.
 * Permet de valider la structure hiérarchique et l’affichage récursif.
 */
public class TestComposite {
    public static void main(String[] args) {
        // Création de perspectives simples
        PerspectiveLeaf p1 = new PerspectiveLeaf(1.0, 10, 20);
        PerspectiveLeaf p2 = new PerspectiveLeaf(2.0, 30, 40);

        // Création d’un groupe intermédiaire
        PerspectiveComposite groupe = new PerspectiveComposite();
        groupe.ajouter(p1);
        groupe.ajouter(p2);

        // Création d’un groupe racine
        PerspectiveComposite root = new PerspectiveComposite();
        root.ajouter(groupe);
        root.ajouter(new PerspectiveLeaf(1.5, 50, 60));

        // Affichage hiérarchique
        root.afficher();
    }
}
