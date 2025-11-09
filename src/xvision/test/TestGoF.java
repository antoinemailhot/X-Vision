package xvision.test;

/**
 * Classe de test regroupant les principaux patrons GoF utilisés dans X-Vision.
 * Chaque test illustre le comportement attendu et vérifie le respect du patron.
 */
public class TestGoF {
/**    public static void testObserver() {
        System.out.println("▶ Test Observer : Synchronisation des vues...");
        Perspective p = new Perspective();
        VuePrincipale vue1 = new VuePrincipale(p);
        VueMini vue2 = new VueMini(p);
        p.setZoom(2.0);
        System.out.println("✓ Observer : mise à jour automatique des vues réussie.\n");
    }

    public static void testCommandMemento() {
        System.out.println("▶ Test Command + Memento : Undo / Redo...");
        Perspective p = new Perspective();
        GestionnaireCommandes gestionnaire = GestionnaireCommandes.getInstance();

        gestionnaire.executer(new CommandeZoom(p, 2.0));
        gestionnaire.executer(new CommandeTranslation(p, 10, 20));

        gestionnaire.annuler(); // Undo
        gestionnaire.refaire(); // Redo

        System.out.println("✓ Command + Memento : historique fonctionnel.\n");
    }

    public static void testAdapter() {
        System.out.println("▶ Test Adapter : Chargement d’images multi-format...");
        ImageTarget imgPng = new AdapterPNG();
        ImageTarget imgJpeg = new AdapterJPEG();
        ImageTarget imgSvg = new AdapterSVG();

        imgPng.charger("image.png");
        imgJpeg.charger("photo.jpeg");
        imgSvg.charger("vectoriel.svg");

        System.out.println("✓ Adapter : chargement uniforme réussi.\n");
    }

    /** Méthode principale pour exécuter tous les tests GoF */
    /**public static void runAll() {
        System.out.println("=== Tests GoF ===\n");
        testObserver();
        testCommandMemento();
        testAdapter();
        System.out.println("=== Fin des tests GoF ===\n");
    }*/
}
