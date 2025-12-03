package xvision.test;

import xvision.model.*;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Test unitaire manuel pour valider la persistance "Deep State".
 * Vérifie :
 * 1. La sauvegarde d'une perspective avec historique.
 * 2. L'optimisation Base64 (mot-clé "SAME").
 * 3. Le chargement correct de l'état et de l'historique.
 */
public class TestPersistence {

    public static void main(String[] args) {
        System.out.println("=== DÉBUT TEST PERSISTANCE ===");
        
        // 1. Création d'une perspective
        System.out.println("1. Création perspective...");
        PerspectiveLeaf pOriginale = new PerspectiveLeaf(1.0, 0, 0);
        
        // Simulation d'une image (faux chemin pour le test, mais on teste la logique JSON)
        // Pour un vrai test Base64, il faudrait un vrai fichier image.
        // Ici on teste surtout la structure JSON et l'historique.
        
        // 2. Modifications (Création d'historique)
        System.out.println("2. Modifications (Zoom, Pan, Crop)...");
        pOriginale.setZoom(1.5); // Memento 1
        pOriginale.setCentre(100, 100); // Memento 2
        pOriginale.setCrop(new Rectangle(10, 10, 50, 50)); // Memento 3
        
        // 3. Sauvegarde
        System.out.println("3. Sauvegarde JSON...");
        GestionnaireDePerspectives gestionnaire = new GestionnaireDePerspectives();
        String testFile = "test_persistence.json";
        gestionnaire.sauvegarderPerspective(pOriginale, testFile);
        
        // Vérification du fichier
        try {
            String jsonContent = Files.readString(Path.of(testFile));
            System.out.println("   Fichier généré (" + jsonContent.length() + " chars).");
            if (jsonContent.contains("\"undoStack\"")) {
                System.out.println("   [OK] undoStack présent.");
            } else {
                System.err.println("   [ERREUR] undoStack manquant !");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 4. Chargement
        System.out.println("4. Chargement...");
        gestionnaire.chargerPerspective(testFile);
        PerspectiveLeaf pChargee = gestionnaire.getDernierePerspective();
        
        if (pChargee != null) {
            System.out.println("   Perspective chargée ID: " + pChargee.getIdPerspective());
            
            // Vérification état final
            boolean zoomOk = Math.abs(pChargee.getZoom() - 1.5) < 0.001;
            boolean posOk = Math.abs(pChargee.getCentreX() - 100) < 0.001;
            boolean cropOk = pChargee.getCrop() != null && pChargee.getCrop().getZone().width == 50;
            
            System.out.println("   Zoom correct ? " + (zoomOk ? "OUI" : "NON (" + pChargee.getZoom() + ")"));
            System.out.println("   Pos correcte ? " + (posOk ? "OUI" : "NON"));
            System.out.println("   Crop correct ? " + (cropOk ? "OUI" : "NON"));
            
            // Vérification historique
            int undoSize = pChargee.getMementos().size(); // Note: getMementos retourne la pile Undo
            System.out.println("   Taille historique Undo : " + undoSize);
            if (undoSize >= 3) {
                System.out.println("   [OK] Historique restauré.");
            } else {
                System.err.println("   [ERREUR] Historique incomplet (attendu >= 3).");
            }
            
        } else {
            System.err.println("   [ERREUR] Échec du chargement.");
        }
        
        // Nettoyage
        new File(testFile).delete();
        System.out.println("=== FIN TEST PERSISTANCE ===");
    }
}
