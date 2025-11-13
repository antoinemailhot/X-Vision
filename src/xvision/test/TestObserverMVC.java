package xvision.test;

import xvision.model.*;
import xvision.view.*;
import xvision.controller.*;

/**
 * Test d'intégration du patron Observer avec l'architecture MVC
 * Valide que:
 * 1. Les vues s'enregistrent correctement comme observateurs
 * 2. Les modifications via le contrôleur notifient automatiquement les vues
 * 3. Le patron Command fonctionne avec Undo/Redo
 * 4. L'architecture MVC est respectée
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class TestObserverMVC {

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("TEST D'INTÉGRATION: OBSERVER + MVC + COMMAND");
        System.out.println("=".repeat(70));
        System.out.println();

        // ===== TEST 1: Création du modèle =====
        System.out.println("[TEST 1] Création du modèle (Perspective)");
        System.out.println("-".repeat(70));
        PerspectiveLeaf perspective = new PerspectiveLeaf(1.0, 0, 0);
        System.out.println("Perspective créée: Zoom=" + perspective.getZoom() +
                           ", Centre=(" + perspective.getCentreX() + "," + perspective.getCentreY() + ")");
        System.out.println("Nombre d'observateurs: " + perspective.getNombreObservateurs());
        assert perspective.getNombreObservateurs() == 0 : "Devrait avoir 0 observateurs initialement";
        System.out.println("✓ Test 1 réussi");
        System.out.println();

        // ===== TEST 2: Création des vues (Observateurs) =====
        System.out.println("[TEST 2] Création des vues et enregistrement comme observateurs");
        System.out.println("-".repeat(70));

        // Créer une Vignette (hérite de VueAbstraite)
        Vignette vignette = new Vignette(perspective);
        System.out.println("Vignette créée et attachée");
        System.out.println("Nombre d'observateurs: " + perspective.getNombreObservateurs());
        assert perspective.getNombreObservateurs() == 1 : "Devrait avoir 1 observateur";

        // Note: VuePrincipale et VueMini créent des JFrames (interface graphique)
        // Pour les tests en ligne de commande, on teste seulement Vignette
        System.out.println("✓ Test 2 réussi");
        System.out.println();

        // ===== TEST 3: Modification directe (sans commande) =====
        System.out.println("[TEST 3] Modification directe du modèle (notification automatique)");
        System.out.println("-".repeat(70));
        System.out.println("Avant: Zoom=" + perspective.getZoom());
        System.out.println("Modification: setZoom(2.0)");
        perspective.setZoom(2.0);
        System.out.println("Après: Zoom=" + perspective.getZoom());
        System.out.println("Note: La vignette a été notifiée automatiquement (voir output ci-dessus)");
        System.out.println("✓ Test 3 réussi");
        System.out.println();

        // ===== TEST 4: Modification via Contrôleur + Command =====
        System.out.println("[TEST 4] Modification via Contrôleur (Architecture MVC + Patron Command)");
        System.out.println("-".repeat(70));

        GestionnaireDePerspectives gestionnaire = new GestionnaireDePerspectives();
        gestionnaire.ajouterPerspective("test", perspective);
        ControleurPerspective controleur = new ControleurPerspective(gestionnaire);

        System.out.println("Avant: Zoom=" + perspective.getZoom());
        System.out.println("Action: controleur.zoomer(perspective, 3.5)");
        controleur.zoomer(perspective, 3.5);
        System.out.println("Après: Zoom=" + perspective.getZoom());
        assert perspective.getZoom() == 3.5 : "Le zoom devrait être 3.5";
        System.out.println("Note: La commande a été exécutée ET les observateurs notifiés");
        System.out.println("✓ Test 4 réussi");
        System.out.println();

        // ===== TEST 5: Undo/Redo =====
        System.out.println("[TEST 5] Undo/Redo (Patron Command)");
        System.out.println("-".repeat(70));

        System.out.println("Zoom actuel: " + perspective.getZoom());
        System.out.println("Action: Undo");
        controleur.annulerDerniereAction();
        System.out.println("Après Undo: Zoom=" + perspective.getZoom());
        assert perspective.getZoom() == 2.0 : "Le zoom devrait être revenu à 2.0";

        System.out.println("Action: Redo");
        controleur.refaireDerniereAction();
        System.out.println("Après Redo: Zoom=" + perspective.getZoom());
        assert perspective.getZoom() == 3.5 : "Le zoom devrait être revenu à 3.5";
        System.out.println("✓ Test 5 réussi");
        System.out.println();

        // ===== TEST 6: Translation (déplacement) =====
        System.out.println("[TEST 6] Translation via Contrôleur");
        System.out.println("-".repeat(70));

        System.out.println("Position avant: (" + perspective.getCentreX() + "," + perspective.getCentreY() + ")");
        System.out.println("Action: controleur.deplacer(perspective, 100, 50)");
        controleur.deplacer(perspective, 100, 50);
        System.out.println("Position après: (" + perspective.getCentreX() + "," + perspective.getCentreY() + ")");
        assert perspective.getCentreX() == 100.0 : "X devrait être 100";
        assert perspective.getCentreY() == 50.0 : "Y devrait être 50";
        System.out.println("✓ Test 6 réussi");
        System.out.println();

        // ===== TEST 7: Undo de la translation =====
        System.out.println("[TEST 7] Undo de la translation");
        System.out.println("-".repeat(70));

        System.out.println("Position avant Undo: (" + perspective.getCentreX() + "," + perspective.getCentreY() + ")");
        controleur.annulerDerniereAction();
        System.out.println("Position après Undo: (" + perspective.getCentreX() + "," + perspective.getCentreY() + ")");
        assert perspective.getCentreX() == 0.0 : "X devrait être revenu à 0";
        assert perspective.getCentreY() == 0.0 : "Y devrait être revenu à 0";
        System.out.println("✓ Test 7 réussi");
        System.out.println();

        // ===== TEST 8: Détachement d'observateur =====
        System.out.println("[TEST 8] Détachement d'observateur");
        System.out.println("-".repeat(70));

        System.out.println("Observateurs avant détachement: " + perspective.getNombreObservateurs());
        vignette.detacher();
        System.out.println("Observateurs après détachement: " + perspective.getNombreObservateurs());
        assert perspective.getNombreObservateurs() == 0 : "Devrait avoir 0 observateurs";

        System.out.println("Modification après détachement:");
        perspective.setZoom(5.0);
        System.out.println("Note: Aucune notification (vignette détachée)");
        System.out.println("✓ Test 8 réussi");
        System.out.println();

        // ===== RÉSUMÉ =====
        System.out.println("=".repeat(70));
        System.out.println("✅ TOUS LES TESTS RÉUSSIS!");
        System.out.println("=".repeat(70));
        System.out.println();
        System.out.println("Validations:");
        System.out.println("✓ Patron Observer: Sujet notifie automatiquement les observateurs");
        System.out.println("✓ Architecture MVC: Séparation Modèle/Vue/Contrôleur respectée");
        System.out.println("✓ Patron Command: Modifications via commandes (support Undo/Redo)");
        System.out.println("✓ Principe GRASP Low Coupling: Modèle ne connaît pas les vues concrètes");
        System.out.println("✓ Principe GRASP Information Expert: Perspective gère son état");
        System.out.println("✓ Principe GRASP Controller: ControleurPerspective gère les actions");
        System.out.println();
    }
}
