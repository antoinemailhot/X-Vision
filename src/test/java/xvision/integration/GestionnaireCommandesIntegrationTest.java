package xvision.integration;

import org.junit.jupiter.api.*;
import xvision.controller.*;
import xvision.model.GestionnaireDePerspectives;
import xvision.model.PerspectiveLeaf;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour le GestionnaireCommandes avec le modèle.
 *
 * <p>
 * <b>DIFFÉRENCE AVEC TESTS UNITAIRES</b>:
 * <ul>
 *   <li>Tests unitaires: Testent UNE classe isolément (avec mocks)</li>
 *   <li>Tests d'intégration: Testent l'INTERACTION entre plusieurs classes (vraies instances)</li>
 * </ul>
 * </p>
 *
 * <p>
 * Ce test d'intégration vérifie:
 * <ul>
 *   <li>Contrôleur ↔ Modèle: Les commandes modifient correctement le modèle</li>
 *   <li>Historique global: Undo/Redo fonctionnent à travers le gestionnaire</li>
 *   <li>Commandes interchangeables: Menu vs souris → même effet</li>
 * </ul>
 * </p>
 *
 * <p><b>Patrons testés</b>: Command + Singleton + Observer</p>
 * <p><b>Principe GRASP testé</b>: Controller (centralisation des commandes)</p>
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0 (TP2)
 */
@Tag("integration")  // Tag pour: ./gradlew testIntegration
@DisplayName("Tests d'intégration: GestionnaireCommandes ↔ Modèle")
class GestionnaireCommandesIntegrationTest {

    private GestionnaireCommandes gestionnaire;
    private ControleurPerspective controleur;
    private GestionnaireDePerspectives gestionnairePerspectives;
    private PerspectiveLeaf perspective;

    private static final double ZOOM_INITIAL = 1.0;
    private static final double DELTA = 0.001;

    @BeforeEach
    void setUp() {
        // Créer le gestionnaire de commandes (Singleton)
        gestionnaire = GestionnaireCommandes.getInstance();
        gestionnaire.clear(); // Nettoyer l'historique (important pour isolation des tests)

        // Créer le gestionnaire de perspectives
        gestionnairePerspectives = new GestionnaireDePerspectives();

        // Créer une perspective
        perspective = new PerspectiveLeaf(ZOOM_INITIAL, 0, 0);
        gestionnairePerspectives.ajouterPerspective("test", perspective);

        // Créer le contrôleur
        controleur = new ControleurPerspective(gestionnairePerspectives);
    }

    @AfterEach
    void tearDown() {
        // Nettoyer l'historique du singleton pour éviter side-effects
        gestionnaire.clear();
    }

    // ==================== TESTS: INTÉGRATION CONTRÔLEUR ↔ MODÈLE ====================

    /**
     * Test d'intégration: Le contrôleur modifie le modèle via une commande.
     *
     * Flux testé: Contrôleur → GestionnaireCommandes → Commande → Modèle
     */
    @Test
    @DisplayName("Devrait modifier le modèle quand le contrôleur exécute une commande")
    void shouldModifyModel_whenControllerExecutesCommand() {
        // Arrange
        double nouveauZoom = 2.5;

        // Act: Le contrôleur exécute une action de zoom
        controleur.zoomer(perspective, nouveauZoom);

        // Assert: Le modèle doit être modifié
        assertEquals(nouveauZoom, perspective.getZoom(), DELTA,
            "Le modèle devrait être modifié par le contrôleur");
    }

    /**
     * Test d'intégration: Undo via le gestionnaire restaure l'état du modèle.
     *
     * Flux testé: GestionnaireCommandes.undo() → Commande.annuler() → Modèle
     */
    @Test
    @DisplayName("Devrait restaurer l'état du modèle quand undo() est appelé")
    void shouldRestoreModelState_whenUndoCalled() {
        // Arrange: Exécuter une commande de zoom
        double nouveauZoom = 2.5;
        controleur.zoomer(perspective, nouveauZoom);
        assertEquals(nouveauZoom, perspective.getZoom(), DELTA, "Précondition");

        // Act: Undo via le contrôleur
        controleur.annulerDerniereAction();

        // Assert: Le modèle doit être restauré
        assertEquals(ZOOM_INITIAL, perspective.getZoom(), DELTA,
            "Le modèle devrait être restauré à l'état initial après undo");
    }

    /**
     * Test d'intégration: Redo via le gestionnaire rétablit l'état.
     */
    @Test
    @DisplayName("Devrait rétablir l'état du modèle quand redo() est appelé")
    void shouldRestoreModelState_whenRedoCalled() {
        // Arrange
        double nouveauZoom = 2.5;
        controleur.zoomer(perspective, nouveauZoom);
        controleur.annulerDerniereAction(); // Undo

        // Act: Redo
        controleur.refaireDerniereAction();

        // Assert: Le modèle doit être rétabli
        assertEquals(nouveauZoom, perspective.getZoom(), DELTA,
            "Le modèle devrait être rétabli après redo");
    }

    // ==================== TESTS: HISTORIQUE GLOBAL ====================

    /**
     * Test d'intégration: Historique global gère plusieurs commandes.
     *
     * Scénario: Zoom → Translation → Undo → Undo
     */
    @Test
    @DisplayName("Devrait gérer l'historique de plusieurs commandes")
    void shouldManageMultipleCommandsHistory_whenExecuted() {
        // Arrange & Act: Exécuter 2 commandes
        controleur.zoomer(perspective, 2.0);
        assertEquals(2.0, perspective.getZoom(), DELTA, "Après zoom");

        controleur.deplacer(perspective, 100, 50);
        assertEquals(100.0, perspective.getCentreX(), DELTA, "Après translation");

        // Act: Undo 2 fois
        controleur.annulerDerniereAction(); // Undo translation
        assertEquals(0.0, perspective.getCentreX(), DELTA, "Après undo translation");

        controleur.annulerDerniereAction(); // Undo zoom
        assertEquals(ZOOM_INITIAL, perspective.getZoom(), DELTA, "Après undo zoom");
    }

    /**
     * Test d'intégration: Nouvelle commande vide la pile de Redo.
     *
     * Principe: L'historique est linéaire (pas d'arbre).
     * Scénario: Zoom → Undo → Nouveau Zoom → Redo (devrait échouer)
     */
    @Test
    @DisplayName("Devrait vider la pile Redo quand une nouvelle commande est exécutée")
    void shouldClearRedoStack_whenNewCommandExecuted() {
        // Arrange: Zoom → Undo
        controleur.zoomer(perspective, 2.0);
        controleur.annulerDerniereAction();
        assertEquals(ZOOM_INITIAL, perspective.getZoom(), DELTA, "Après undo");

        // Act: Exécuter une NOUVELLE commande
        controleur.zoomer(perspective, 3.0);

        // Assert: Redo ne devrait plus fonctionner (pile vidée)
        controleur.refaireDerniereAction();
        // Le zoom devrait rester à 3.0 (redo sans effet car pile vide)
        assertEquals(3.0, perspective.getZoom(), DELTA,
            "Redo ne devrait rien faire après une nouvelle commande");
    }

    // ==================== TESTS: CONTRÔLEURS INTERCHANGEABLES ====================

    /**
     * Test d'intégration CRITIQUE pour TP2:
     * Menu vs souris doivent produire le MÊME effet.
     *
     * Principe: Plusieurs contrôleurs (menu, souris, clavier) → mêmes commandes.
     */
    @Test
    @DisplayName("Devrait produire le même effet via menu ou souris (contrôleurs interchangeables)")
    void shouldProduceSameEffect_whenDifferentControllers() {
        // Arrange: 2 perspectives identiques
        PerspectiveLeaf perspective1 = new PerspectiveLeaf(ZOOM_INITIAL, 0, 0);
        PerspectiveLeaf perspective2 = new PerspectiveLeaf(ZOOM_INITIAL, 0, 0);

        // Act: Simuler "zoom via menu" (contrôleur)
        controleur.zoomer(perspective1, 2.5);

        // Simuler "zoom via souris" (même contrôleur, même commande)
        // Dans une vraie appli, ce serait un contrôleur souris différent,
        // mais qui exécuterait la MÊME CommandeZoom via le gestionnaire
        controleur.zoomer(perspective2, 2.5);

        // Assert: Les 2 perspectives doivent avoir le même état
        assertEquals(perspective1.getZoom(), perspective2.getZoom(), DELTA,
            "Menu et souris devraient produire le même effet");
    }

    // ==================== TESTS: COMMANDES MULTIPLES ====================

    /**
     * Test d'intégration: Enchaînement complexe de commandes.
     *
     * Scénario: Zoom → Translation → Zoom → Undo → Redo → Undo
     */
    @Test
    @DisplayName("Devrait gérer un enchaînement complexe de commandes")
    void shouldHandleComplexCommandSequence_correctly() {
        // Act: Séquence complexe
        controleur.zoomer(perspective, 2.0);        // État 1: zoom=2.0
        controleur.deplacer(perspective, 100, 50);  // État 2: pos=(100,50)
        controleur.zoomer(perspective, 3.0);        // État 3: zoom=3.0

        // Vérifier état final
        assertEquals(3.0, perspective.getZoom(), DELTA);
        assertEquals(100.0, perspective.getCentreX(), DELTA);

        // Undo → Redo → Undo
        controleur.annulerDerniereAction();  // Retour état 2: zoom=2.0
        assertEquals(2.0, perspective.getZoom(), DELTA);

        controleur.refaireDerniereAction();  // Retour état 3: zoom=3.0
        assertEquals(3.0, perspective.getZoom(), DELTA);

        controleur.annulerDerniereAction();  // Retour état 2: zoom=2.0
        assertEquals(2.0, perspective.getZoom(), DELTA);
    }

    // ==================== TESTS: SINGLETON ====================

    /**
     * Test d'intégration: Le gestionnaire est bien un Singleton.
     *
     * Principe: Une seule instance partagée par tous les contrôleurs.
     */
    @Test
    @DisplayName("Devrait retourner la même instance (Singleton)")
    void shouldReturnSameInstance_whenGetInstanceCalled() {
        // Act
        GestionnaireCommandes instance1 = GestionnaireCommandes.getInstance();
        GestionnaireCommandes instance2 = GestionnaireCommandes.getInstance();

        // Assert
        assertSame(instance1, instance2,
            "getInstance() devrait toujours retourner la même instance");
    }

    /**
     * Test d'intégration: L'historique est partagé entre contrôleurs.
     *
     * Principe: Tous les contrôleurs utilisent le même gestionnaire (Singleton).
     */
    @Test
    @DisplayName("Devrait partager l'historique entre contrôleurs (Singleton)")
    void shouldShareHistoryBetweenControllers_whenSingleton() {
        // Arrange: 2 contrôleurs (simuler menu et souris)
        ControleurPerspective controleurMenu = new ControleurPerspective(gestionnairePerspectives);
        ControleurPerspective controleurSouris = new ControleurPerspective(gestionnairePerspectives);

        // Act: Contrôleur 1 exécute une commande
        controleurMenu.zoomer(perspective, 2.0);

        // Contrôleur 2 fait Undo (devrait annuler l'action du contrôleur 1)
        controleurSouris.annulerDerniereAction();

        // Assert: Le zoom devrait être restauré (historique partagé)
        assertEquals(ZOOM_INITIAL, perspective.getZoom(), DELTA,
            "Les contrôleurs devraient partager le même historique");
    }

    // ==================== TESTS: LIMITES ====================

    /**
     * Test d'intégration: Undo sur historique vide ne fait rien.
     */
    @Test
    @DisplayName("Ne devrait rien faire si undo() sur historique vide")
    void shouldDoNothing_whenUndoOnEmptyHistory() {
        // Arrange: Historique vide (setUp() a fait clear())
        assertTrue(gestionnaire.isEmpty(), "Précondition: historique vide");

        // Act: Undo sur historique vide
        controleur.annulerDerniereAction();

        // Assert: Le modèle ne devrait pas changer
        assertEquals(ZOOM_INITIAL, perspective.getZoom(), DELTA,
            "Le modèle ne devrait pas changer si historique vide");
    }

    /**
     * Test d'intégration: Redo sur pile Redo vide ne fait rien.
     */
    @Test
    @DisplayName("Ne devrait rien faire si redo() sur pile Redo vide")
    void shouldDoNothing_whenRedoOnEmptyRedoStack() {
        // Arrange: Pile Redo vide (aucune commande annulée)
        assertTrue(gestionnaire.isRedoEmpty(), "Précondition: pile Redo vide");

        // Act: Redo sans undo préalable
        controleur.refaireDerniereAction();

        // Assert: Le modèle ne devrait pas changer
        assertEquals(ZOOM_INITIAL, perspective.getZoom(), DELTA,
            "Le modèle ne devrait pas changer si pile Redo vide");
    }
}
