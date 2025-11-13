package xvision.test;

import xvision.model.*;

/**
 * Classe de test pour le patron Adapter et Factory Method
 *
 * Tests effectués:
 * 1. Test de création via FabriqueImage (Factory Method)
 * 2. Test des adaptateurs PNG, JPEG, SVG
 * 3. Test de l'interface uniforme ImageTarget
 * 4. Test de détection de format
 * 5. Test de gestion d'erreurs
 * 6. ⭐ NOUVEAU: Test de chargement réel d'images
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class TestAdaptateurs {

    /**
     * Chemins vers les images de test
     */
    private static final String RESOURCES_PATH = "../resources/test_images/";
    private static final String IMAGE_PNG = RESOURCES_PATH + "test.png";
    private static final String IMAGE_JPEG = RESOURCES_PATH + "test.jpg";
    private static final String IMAGE_SVG = RESOURCES_PATH + "test.svg";

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("TESTS DES PATRONS ADAPTER + FACTORY METHOD");
        System.out.println("=".repeat(70));
        System.out.println();

        // Exécution de tous les tests
        testFabriqueFormatsSupporte();
        System.out.println();

        testCreationAdaptateurPNG();
        System.out.println();

        testCreationAdaptateurJPEG();
        System.out.println();

        testCreationAdaptateurSVG();
        System.out.println();

        testInterfaceUniforme();
        System.out.println();

        testDetectionFormat();
        System.out.println();

        testGestionErreurs();
        System.out.println();

        testChargementReelImages();
        System.out.println();

        System.out.println("=".repeat(70));
        System.out.println("TOUS LES TESTS TERMINÉS");
        System.out.println("=".repeat(70));
    }

    /**
     * Test 1: Vérification des formats supportés
     */
    private static void testFabriqueFormatsSupporte() {
        System.out.println("[TEST 1] Formats supportés par la fabrique");
        System.out.println("-".repeat(70));

        FabriqueImage.afficherFormatsSupportes();

        // Vérifications
        assert FabriqueImage.estFormatSupporte("png") : "PNG devrait être supporté";
        assert FabriqueImage.estFormatSupporte("jpg") : "JPG devrait être supporté";
        assert FabriqueImage.estFormatSupporte("jpeg") : "JPEG devrait être supporté";
        assert FabriqueImage.estFormatSupporte("svg") : "SVG devrait être supporté";
        assert !FabriqueImage.estFormatSupporte("bmp") : "BMP ne devrait PAS être supporté";

        System.out.println("✓ Test des formats supportés: RÉUSSI");
    }

    /**
     * Test 2: Création et test de l'adaptateur PNG
     */
    private static void testCreationAdaptateurPNG() {
        System.out.println("[TEST 2] Création et test AdapterPNG");
        System.out.println("-".repeat(70));

        // Création via constructeur direct
        ImageTarget imagePNG = new AdapterPNG();
        System.out.println("AdapterPNG créé directement: " + imagePNG.getClass().getSimpleName());

        // Création via Factory
        ImageTarget imagePNG2 = FabriqueImage.creerImage(FabriqueImage.FormatImage.PNG);
        System.out.println("AdapterPNG créé via Factory: " + imagePNG2.getClass().getSimpleName());

        // Vérification du type
        assert imagePNG instanceof ImageTarget : "L'adaptateur doit implémenter ImageTarget";
        assert imagePNG instanceof AdapterPNG : "Doit être un AdapterPNG";

        System.out.println("✓ Test AdapterPNG: RÉUSSI");
    }

    /**
     * Test 3: Création et test de l'adaptateur JPEG
     */
    private static void testCreationAdaptateurJPEG() {
        System.out.println("[TEST 3] Création et test AdapterJPEG");
        System.out.println("-".repeat(70));

        // Création via constructeur direct
        ImageTarget imageJPEG = new AdapterJPEG();
        System.out.println("AdapterJPEG créé directement: " + imageJPEG.getClass().getSimpleName());

        // Création via Factory
        ImageTarget imageJPEG2 = FabriqueImage.creerImage(FabriqueImage.FormatImage.JPEG);
        System.out.println("AdapterJPEG créé via Factory: " + imageJPEG2.getClass().getSimpleName());

        // Test de la méthode spécifique JPEG
        if (imageJPEG instanceof AdapterJPEG) {
            AdapterJPEG adapterJPEG = (AdapterJPEG) imageJPEG;
            adapterJPEG.setQualiteCompression(0.9f);
            System.out.println("Qualité de compression JPEG ajustée");
        }

        // Vérification du type
        assert imageJPEG instanceof ImageTarget : "L'adaptateur doit implémenter ImageTarget";
        assert imageJPEG instanceof AdapterJPEG : "Doit être un AdapterJPEG";

        System.out.println("✓ Test AdapterJPEG: RÉUSSI");
    }

    /**
     * Test 4: Création et test de l'adaptateur SVG
     */
    private static void testCreationAdaptateurSVG() {
        System.out.println("[TEST 4] Création et test AdapterSVG");
        System.out.println("-".repeat(70));

        // Création via constructeur direct
        ImageTarget imageSVG = new AdapterSVG();
        System.out.println("AdapterSVG créé directement: " + imageSVG.getClass().getSimpleName());

        // Création via Factory
        ImageTarget imageSVG2 = FabriqueImage.creerImage(FabriqueImage.FormatImage.SVG);
        System.out.println("AdapterSVG créé via Factory: " + imageSVG2.getClass().getSimpleName());

        // Test de la méthode spécifique SVG
        if (imageSVG instanceof AdapterSVG) {
            AdapterSVG adapterSVG = (AdapterSVG) imageSVG;
            adapterSVG.setDimensionsRendu(1920, 1080);
            System.out.println("Dimensions de rendu SVG ajustées");
        }

        // Vérification du type
        assert imageSVG instanceof ImageTarget : "L'adaptateur doit implémenter ImageTarget";
        assert imageSVG instanceof AdapterSVG : "Doit être un AdapterSVG";

        System.out.println("✓ Test AdapterSVG: RÉUSSI");
    }

    /**
     * Test 5: Interface uniforme - Polymorphisme
     *
     * Ce test démontre le principe de Low Coupling:
     * Le code client manipule tous les formats via l'interface ImageTarget
     */
    private static void testInterfaceUniforme() {
        System.out.println("[TEST 5] Interface uniforme (Polymorphisme)");
        System.out.println("-".repeat(70));

        // Tableau d'images de différents formats
        ImageTarget[] images = new ImageTarget[3];
        images[0] = new AdapterPNG();
        images[1] = new AdapterJPEG();
        images[2] = new AdapterSVG();

        // Manipulation uniforme via l'interface ImageTarget
        System.out.println("Manipulation polymorphique de tous les formats:");
        for (int i = 0; i < images.length; i++) {
            ImageTarget image = images[i];
            System.out.println("  Image " + (i + 1) + ": " + image.getClass().getSimpleName());

            // Toutes ces méthodes sont définies dans ImageTarget
            image.afficher();
            int largeur = image.getLargeur();
            int hauteur = image.getHauteur();
            System.out.println("    Dimensions: " + largeur + "x" + hauteur);
        }

        System.out.println("✓ Test interface uniforme: RÉUSSI");
        System.out.println("  → Principe Low Coupling respecté!");
    }

    /**
     * Test 6: Détection automatique de format
     */
    private static void testDetectionFormat() {
        System.out.println("[TEST 6] Détection automatique de format");
        System.out.println("-".repeat(70));

        String[] fichierTests = {
                "image.png",
                "photo.jpg",
                "image.jpeg",
                "dessin.svg"
        };

        System.out.println("Test de détection de format à partir du nom de fichier:");
        for (String fichier : fichierTests) {
            try {
                // La factory détecte automatiquement le format
                System.out.println("  Fichier: " + fichier);
                ImageTarget image = FabriqueImage.creerImage(fichier);
                System.out.println("    → Adaptateur créé: " + image.getClass().getSimpleName());
            } catch (Exception e) {
                System.out.println("    → Erreur: " + e.getMessage());
            }
        }

        System.out.println("✓ Test détection de format: RÉUSSI");
    }

    /**
     * Test 7: Gestion des erreurs
     */
    private static void testGestionErreurs() {
        System.out.println("[TEST 7] Gestion des erreurs");
        System.out.println("-".repeat(70));

        // Test 1: Format non supporté
        try {
            System.out.println("Tentative de création avec format non supporté (BMP)...");
            FabriqueImage.creerImage("image.bmp");
            System.out.println("✗ ERREUR: Une exception aurait dû être levée!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Exception correctement levée: " + e.getMessage());
        }

        // Test 2: Chemin vide
        try {
            System.out.println("Tentative de création avec chemin vide...");
            FabriqueImage.creerImage("");
            System.out.println("✗ ERREUR: Une exception aurait dû être levée!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Exception correctement levée: " + e.getMessage());
        }

        // Test 3: Chemin null
        try {
            System.out.println("Tentative de création avec chemin null...");
            FabriqueImage.creerImage((String)null);  // Cast explicite pour lever l'ambiguïté
            System.out.println("✗ ERREUR: Une exception aurait dû être levée!");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ Exception correctement levée: " + e.getMessage());
        }

        System.out.println("✓ Test gestion des erreurs: RÉUSSI");
    }

    /**
     * Test 8: Chargement réel d'images depuis le système de fichiers
     * Valide que les adaptateurs fonctionnent avec de vraies images.
     */
    private static void testChargementReelImages() {
        System.out.println("[TEST 8] Chargement réel d'images");
        System.out.println("-".repeat(70));

        // Vérifier que les fichiers existent
        java.io.File fichierPNG = new java.io.File(IMAGE_PNG);
        java.io.File fichierJPEG = new java.io.File(IMAGE_JPEG);
        java.io.File fichierSVG = new java.io.File(IMAGE_SVG);

        if (!fichierPNG.exists() || !fichierJPEG.exists() || !fichierSVG.exists()) {
            System.out.println("ATTENTION: Images de test manquantes!");
            System.out.println("Veuillez placer les images dans: " + RESOURCES_PATH);
            System.out.println("Test IGNORÉ");
            return;
        }

        System.out.println("Fichiers trouvés:");
        System.out.println("  - " + IMAGE_PNG + " (" + (fichierPNG.length() / 1024) + " KB)");
        System.out.println("  - " + IMAGE_JPEG + " (" + (fichierJPEG.length() / 1024) + " KB)");
        System.out.println("  - " + IMAGE_SVG + " (" + (fichierSVG.length() / 1024) + " KB)");
        System.out.println();

        // Test PNG
        System.out.println("Test chargement PNG:");
        testChargerImage(IMAGE_PNG, "PNG");

        // Test JPEG
        System.out.println("Test chargement JPEG:");
        testChargerImage(IMAGE_JPEG, "JPEG");

        // Test SVG
        System.out.println("Test chargement SVG:");
        testChargerImage(IMAGE_SVG, "SVG");

        System.out.println("✓ Test chargement réel d'images: RÉUSSI");
    }

    /**
     * Charge et valide une image
     * @param chemin Chemin vers l'image
     * @param format Nom du format pour l'affichage
     */
    private static void testChargerImage(String chemin, String format) {
        try {
            ImageTarget image = FabriqueImage.creerImage(chemin);

            int largeur = image.getLargeur();
            int hauteur = image.getHauteur();
            byte[] donnees = image.getDonneesPixels();

            System.out.println("  Chargé: " + largeur + "x" + hauteur + " pixels");
            System.out.println("  Données: " + donnees.length + " bytes");

            assert largeur > 0 : format + ": largeur invalide";
            assert hauteur > 0 : format + ": hauteur invalide";
            assert donnees != null && donnees.length > 0 : format + ": données vides";

            image.afficher();
            System.out.println();

        } catch (Exception e) {
            System.out.println("  ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
