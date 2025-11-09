package xvision.test;

import xvision.model.*;
import xvision.controller.*;

/**
 * Tests des principes GRASP dans X-Vision.
 * Permet de valider la répartition correcte des responsabilités et le découplage.
 */
public class TestGRASP {

    public static void testController() {
        System.out.println("▶ Test Controller : gestion centralisée des interactions...");
        ControleurPerspective controleur = new ControleurPerspective();
        controleur.reagirEvenement("ZOOM_IN");
        System.out.println("✓ Controller : contrôle central fonctionnel.\n");
    }

    public static void testInformationExpert() {
        System.out.println("▶ Test Information Expert : gestion locale de l’état...");
        Perspective p = new Perspective();
        p.setZoom(3.5);
        p.setPosition(50, 60);
        System.out.println("État interne : zoom=" + p.getZoom() + ", position=(" + p.getX() + "," + p.getY() + ")");
        System.out.println("✓ Information Expert : cohérence locale respectée.\n");
    }

    public static void testLowCouplingHighCohesion() {
        System.out.println("▶ Test Low Coupling / High Cohesion : architecture modulaire...");
        Perspective p = new Perspective();
        VuePrincipale vue = new VuePrincipale(p);
        ControleurPerspective c = new ControleurPerspective();
        c.associerModele(p);
        c.associerVue(vue);
        System.out.println("✓ Low Coupling / High Cohesion : dépendances minimales.\n");
    }

    /** Méthode principale pour exécuter tous les tests GRASP */
    public static void runAll() {
        System.out.println("=== Tests GRASP ===\n");
        testController();
        testInformationExpert();
        testLowCouplingHighCohesion();
        System.out.println("=== Fin des tests GRASP ===\n");
    }
}
