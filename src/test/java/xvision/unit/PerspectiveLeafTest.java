package xvision.unit;

import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import xvision.model.PerspectiveLeaf;
import xvision.model.PerspectiveMemento;
import xvision.view.ObserverIF;

import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour PerspectiveLeaf (Patron Composite - Leaf).
 *
 * <p>
 * Ce test sert de MODÈLE pour tous les tests unitaires du projet.
 * Il démontre les bonnes pratiques:
 * <ul>
 *   <li>Style AAA (Arrange-Act-Assert)</li>
 *   <li>Nommage explicite (shouldXXX_whenYYY)</li>
 *   <li>Utilisation de Mockito pour isolation</li>
 *   <li>Tags JUnit pour pyramide de tests (@Tag("unit"))</li>
 *   <li>Setup/Teardown avec @BeforeEach/@AfterEach</li>
 * </ul>
 * </p>
 *
 * <p><b>Patron testé</b>: Composite (Leaf), Observer, Memento</p>
 * <p><b>Principe GRASP testé</b>: Information Expert (PerspectiveLeaf gère son état)</p>
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0 (TP2)
 */
@Tag("unit")  // Tag pour exécution sélective: ./gradlew testUnit
@DisplayName("Tests unitaires: PerspectiveLeaf")
class PerspectiveLeafTest {

    // ==================== FIXTURES (DONNÉES DE TEST) ====================

    private PerspectiveLeaf perspective;

    // Mock d'un observateur (pour tester notifications sans vraie vue)
    @Mock
    private ObserverIF observateurMock;

    // Constantes pour lisibilité
    private static final double ZOOM_INITIAL = 1.0;
    private static final double ZOOM_MODIFIE = 2.5;
    private static final double POSITION_X_INITIALE = 0.0;
    private static final double POSITION_Y_INITIALE = 0.0;
    private static final double DELTA = 0.001; // Tolérance pour comparaison de doubles

    /**
     * Setup exécuté AVANT chaque test.
     * Initialise les mocks et crée une perspective fraîche.
     *
     * Principe: Chaque test doit être INDÉPENDANT (pas de side-effects).
     */
    @BeforeEach
    void setUp() {
        // Initialiser les mocks Mockito
        MockitoAnnotations.openMocks(this);

        // Créer une perspective fraîche pour chaque test (isolation)
        perspective = new PerspectiveLeaf(ZOOM_INITIAL, POSITION_X_INITIALE, POSITION_Y_INITIALE);
    }

    /**
     * Teardown exécuté APRÈS chaque test (optionnel, pour nettoyage).
     */
    @AfterEach
    void tearDown() {
        // Nettoyage si nécessaire (fichiers temporaires, connexions, etc.)
        perspective = null;
    }

    // ==================== TESTS: ÉTAT INITIAL ====================

    /**
     * Test: Vérifier que la perspective est créée avec les bonnes valeurs initiales.
     *
     * Style AAA:
     * - Arrange: setUp() a créé la perspective
     * - Act: Aucune action (on teste l'état initial)
     * - Assert: Vérifier zoom, position, ID
     */
    @Test
    @DisplayName("Devrait créer une perspective avec état initial correct")
    void shouldHaveCorrectInitialState_whenCreated() {
        // Assert (vérifications)
        assertEquals(ZOOM_INITIAL, perspective.getZoom(), DELTA,
            "Le zoom initial devrait être 1.0");
        assertEquals(POSITION_X_INITIALE, perspective.getCentreX(), DELTA,
            "La position X initiale devrait être 0.0");
        assertEquals(POSITION_Y_INITIALE, perspective.getCentreY(), DELTA,
            "La position Y initiale devrait être 0.0");
        assertNotNull(perspective.getIdPerspective(),
            "L'ID de la perspective ne devrait pas être null");
        assertTrue(perspective.getIdPerspective() > 0,
            "L'ID de la perspective devrait être positif");
    }

    /**
     * Test: Vérifier que le crop est null initialement.
     */
    @Test
    @DisplayName("Devrait avoir crop null initialement")
    void shouldHaveNullCrop_whenCreated() {
        // Assert
        assertNull(perspective.getCrop(),
            "Le crop devrait être null à la création");
    }

    // ==================== TESTS: INVARIANTS (RÈGLES MÉTIER) ====================

    /**
     * Test d'invariant critique: Le zoom doit toujours être > 0.
     *
     * Principe GRASP: Information Expert
     * PerspectiveLeaf est responsable de maintenir son invariant.
     */
    @Test
    @DisplayName("Devrait maintenir zoom > 0 (invariant)")
    void shouldMaintainZoomGreaterThanZero_always() {
        // Arrange
        double zoomValide = 3.0;

        // Act
        perspective.setZoom(zoomValide);

        // Assert
        assertTrue(perspective.getZoom() > 0,
            "Le zoom doit toujours être strictement positif");
        assertEquals(zoomValide, perspective.getZoom(), DELTA);
    }

    // ==================== TESTS: MODIFICATIONS D'ÉTAT ====================

    /**
     * Test: Modifier le zoom met à jour l'état.
     *
     * Style AAA complet.
     */
    @Test
    @DisplayName("Devrait modifier le zoom quand setZoom() est appelé")
    void shouldUpdateZoom_whenSetZoomCalled() {
        // Arrange
        double nouveauZoom = ZOOM_MODIFIE;

        // Act
        perspective.setZoom(nouveauZoom);

        // Assert
        assertEquals(nouveauZoom, perspective.getZoom(), DELTA,
            "Le zoom devrait être mis à jour à " + nouveauZoom);
    }

    /**
     * Test: Modifier la position (translation).
     */
    @Test
    @DisplayName("Devrait modifier la position quand setCentre() est appelé")
    void shouldUpdatePosition_whenSetCentreCalled() {
        // Arrange
        double nouvelleX = 100.0;
        double nouvelleY = 50.0;

        // Act
        perspective.setCentre(nouvelleX, nouvelleY);

        // Assert
        assertEquals(nouvelleX, perspective.getCentreX(), DELTA,
            "La position X devrait être " + nouvelleX);
        assertEquals(nouvelleY, perspective.getCentreY(), DELTA,
            "La position Y devrait être " + nouvelleY);
    }

    /**
     * Test: Définir un crop met à jour l'état.
     */
    @Test
    @DisplayName("Devrait définir le crop quand setCrop() est appelé")
    void shouldSetCrop_whenSetCropCalled() {
        // Arrange
        Rectangle zoneCrop = new Rectangle(10, 10, 100, 100);

        // Act
        perspective.setCrop(zoneCrop);

        // Assert
        assertNotNull(perspective.getCrop(),
            "Le crop ne devrait pas être null après setCrop()");
        assertEquals(zoneCrop, perspective.getCrop().getZone(),
            "La zone de crop devrait correspondre à celle définie");
    }

    /**
     * Test: Réinitialiser le crop (null).
     */
    @Test
    @DisplayName("Devrait réinitialiser le crop quand setCrop(null) est appelé")
    void shouldResetCrop_whenSetCropNullCalled() {
        // Arrange: D'abord définir un crop
        perspective.setCrop(new Rectangle(10, 10, 50, 50));
        assertNotNull(perspective.getCrop(), "Précondition: crop devrait être défini");

        // Act: Réinitialiser
        perspective.setCrop(null);

        // Assert
        assertNull(perspective.getCrop(),
            "Le crop devrait être null après setCrop(null)");
    }

    // ==================== TESTS: PATRON OBSERVER ====================

    /**
     * Test avec MOCK: Vérifier que les observateurs sont notifiés lors d'un changement.
     *
     * Patron testé: Observer
     * Principe GRASP: Low Coupling (PerspectiveLeaf ne connaît pas les vues concrètes)
     *
     * Utilisation de Mockito:
     * - verify() pour vérifier qu'une méthode a été appelée
     * - times() pour compter le nombre d'appels
     */
    @Test
    @DisplayName("Devrait notifier les observateurs quand le zoom change")
    void shouldNotifyObservers_whenZoomChanges() {
        // Arrange: Attacher un observateur mock
        perspective.attacher(observateurMock);

        // Act: Modifier le zoom (devrait déclencher notifier() → actualiser())
        perspective.setZoom(ZOOM_MODIFIE);

        // Assert: Vérifier que actualiser() a été appelé exactement 1 fois
        verify(observateurMock, times(1)).actualiser();
    }

    /**
     * Test avec MOCK: Vérifier que les observateurs sont notifiés lors d'une translation.
     */
    @Test
    @DisplayName("Devrait notifier les observateurs quand la position change")
    void shouldNotifyObservers_whenPositionChanges() {
        // Arrange
        perspective.attacher(observateurMock);

        // Act
        perspective.setCentre(100.0, 50.0);

        // Assert
        verify(observateurMock, times(1)).actualiser();
    }

    /**
     * Test avec MOCK: Vérifier que les observateurs détachés ne sont PAS notifiés.
     *
     * Principe: Observer permet l'abonnement/désabonnement dynamique.
     */
    @Test
    @DisplayName("Ne devrait PAS notifier les observateurs détachés")
    void shouldNotNotifyObservers_whenDetached() {
        // Arrange: Attacher puis détacher
        perspective.attacher(observateurMock);
        perspective.detacher(observateurMock);

        // Act
        perspective.setZoom(ZOOM_MODIFIE);

        // Assert: verify() avec never() = jamais appelé
        verify(observateurMock, never()).actualiser();
    }

    // ==================== TESTS: PATRON MEMENTO ====================

    /**
     * Test: Créer un memento capture l'état actuel.
     *
     * Patron testé: Memento
     * Principe: Encapsulation de l'état pour Undo/Redo
     */
    @Test
    @DisplayName("Devrait créer un memento avec l'état actuel")
    void shouldCreateMementoWithCurrentState_whenCreerMementoCalled() {
        // Arrange: Modifier l'état
        perspective.setZoom(ZOOM_MODIFIE);
        perspective.setCentre(100.0, 50.0);

        // Act: Créer un memento
        PerspectiveMemento memento = perspective.creerMemento();

        // Assert: Le memento doit contenir l'état actuel
        assertNotNull(memento, "Le memento ne devrait pas être null");
        assertEquals(ZOOM_MODIFIE, memento.getZoom(), DELTA,
            "Le memento devrait capturer le zoom actuel");
        assertEquals(100.0, memento.getX(), DELTA,
            "Le memento devrait capturer la position X actuelle");
        assertEquals(50.0, memento.getY(), DELTA,
            "Le memento devrait capturer la position Y actuelle");
    }

    /**
     * Test: Restaurer depuis un memento rétablit l'état.
     */
    @Test
    @DisplayName("Devrait restaurer l'état depuis un memento")
    void shouldRestoreState_whenRestaurerDepuisMementoCalled() {
        // Arrange: État initial
        double zoomInitial = perspective.getZoom();
        perspective.setZoom(ZOOM_MODIFIE); // Modifier
        PerspectiveMemento mementoInitial = new PerspectiveMemento(
            perspective.getIdPerspective(),
            zoomInitial,
            POSITION_X_INITIALE,
            POSITION_Y_INITIALE,
            "",  // imageBase64
            java.util.List.of(),  // crops
            null  // annotations
        );

        // Act: Restaurer
        perspective.restaurerDepuisMemento(mementoInitial);

        // Assert: L'état devrait être celui du memento
        assertEquals(zoomInitial, perspective.getZoom(), DELTA,
            "Le zoom devrait être restauré à la valeur du memento");
    }

    // ==================== TESTS: UNDO/REDO ====================

    /**
     * Test: Undo restaure l'état précédent.
     *
     * Principe: Memento + Command = Undo/Redo
     */
    @Test
    @DisplayName("Devrait annuler la dernière modification quand undo() est appelé")
    void shouldUndoLastChange_whenUndoCalled() {
        // Arrange: État initial mémorisé automatiquement à la création
        double zoomInitial = perspective.getZoom();

        // Act: Modifier puis undo
        perspective.setZoom(ZOOM_MODIFIE);
        assertEquals(ZOOM_MODIFIE, perspective.getZoom(), DELTA, "Précondition");

        perspective.undo();

        // Assert: Retour à l'état initial
        assertEquals(zoomInitial, perspective.getZoom(), DELTA,
            "Undo devrait restaurer le zoom à sa valeur initiale");
    }

    /**
     * Test: Redo rétablit l'état annulé.
     */
    @Test
    @DisplayName("Devrait refaire la modification annulée quand redo() est appelé")
    void shouldRedoUndoneChange_whenRedoCalled() {
        // Arrange
        perspective.setZoom(ZOOM_MODIFIE);
        perspective.undo(); // Annuler

        // Act
        perspective.redo();

        // Assert: Retour à l'état modifié
        assertEquals(ZOOM_MODIFIE, perspective.getZoom(), DELTA,
            "Redo devrait restaurer le zoom à la valeur modifiée");
    }

    /**
     * Test d'idempotence: Undo → Redo → Undo = état initial.
     *
     * Principe: Les commandes doivent être idempotentes (reproductibles).
     */
    @Test
    @DisplayName("Devrait être idempotent (undo→redo→undo = état initial)")
    void shouldBeIdempotent_whenUndoRedoUndoCalled() {
        // Arrange
        double zoomInitial = perspective.getZoom();
        perspective.setZoom(ZOOM_MODIFIE);

        // Act: Undo → Redo → Undo
        perspective.undo();
        perspective.redo();
        perspective.undo();

        // Assert: Retour à l'état initial
        assertEquals(zoomInitial, perspective.getZoom(), DELTA,
            "Après undo→redo→undo, l'état devrait être identique à l'initial");
    }

    // ==================== TESTS: COMPOSITE (LEAF) ====================

    /**
     * Test: Une feuille (Leaf) ne peut pas avoir d'enfants.
     *
     * Patron testé: Composite (Leaf)
     * Principe: Une feuille rejette les opérations de composition.
     */
    @Test
    @DisplayName("Devrait retourner une liste vide d'enfants (Leaf)")
    void shouldReturnEmptyChildren_whenGetEnfantsCalled() {
        // Act & Assert
        assertTrue(perspective.getEnfants().isEmpty(),
            "Une feuille (Leaf) ne devrait pas avoir d'enfants");
    }

    /**
     * Test: Ajouter un enfant à une feuille ne fait rien (ou échoue silencieusement).
     */
    @Test
    @DisplayName("Ne devrait pas accepter d'enfants (Leaf)")
    void shouldNotAcceptChildren_whenAjouterCalled() {
        // Arrange
        PerspectiveLeaf autreLeaf = new PerspectiveLeaf(1.0, 0, 0);

        // Act: Tenter d'ajouter un enfant (ne devrait rien faire)
        perspective.ajouter(autreLeaf);

        // Assert: Toujours vide
        assertTrue(perspective.getEnfants().isEmpty(),
            "Une feuille ne devrait jamais accepter d'enfants");
    }

    // ==================== TESTS PARAMÉTRÉS (BONUS) ====================

    /**
     * Exemple de test paramétré: Tester plusieurs valeurs de zoom.
     *
     * JUnit 5 permet d'exécuter le même test avec différentes valeurs.
     * Utile pour tester les bornes, cas limites, etc.
     */
    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(doubles = {0.5, 1.0, 2.0, 5.0, 10.0})
    @DisplayName("Devrait accepter différentes valeurs de zoom valides")
    void shouldAcceptValidZoomValues_whenSetZoomCalled(double zoomValide) {
        // Act
        perspective.setZoom(zoomValide);

        // Assert
        assertEquals(zoomValide, perspective.getZoom(), DELTA,
            "Le zoom devrait être mis à jour à " + zoomValide);
    }
}
