package xvision.unit;

import org.junit.jupiter.api.*;
import xvision.controller.CommandeTranslation;
import xvision.model.PerspectiveLeaf;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour CommandeTranslation (Patron Command).
 *
 * <p>
 * Ce test démontre comment tester le patron Command pour les translations.
 * Points clés testés (similaires à CommandeZoomTest):
 * <ul>
 *   <li>Vérifier que executer() modifie la position</li>
 *   <li>Vérifier que annuler() restaure la position précédente</li>
 *   <li>Tester l'idempotence (execute → undo → execute = même résultat)</li>
 *   <li>Vérifier l'enchaînement de commandes</li>
 * </ul>
 * </p>
 *
 * <p><b>Patron testé</b>: Command (GoF)</p>
 * <p><b>Principe GRASP testé</b>: Controller (commandes encapsulent les actions)</p>
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0 (TP2)
 */
@Tag("unit")
@DisplayName("Tests unitaires: CommandeTranslation (Patron Command)")
class CommandeTranslationTest {

    private PerspectiveLeaf perspective;
    private CommandeTranslation commande;

    private static final double POSITION_X_INITIALE = 0.0;
    private static final double POSITION_Y_INITIALE = 0.0;
    private static final double NOUVELLE_X = 100.0;
    private static final double NOUVELLE_Y = 50.0;
    private static final double DELTA = 0.001;

    @BeforeEach
    void setUp() {
        // Créer une perspective avec position initiale (0, 0)
        perspective = new PerspectiveLeaf(1.0, POSITION_X_INITIALE, POSITION_Y_INITIALE);

        // Créer la commande (translation vers (100, 50))
        commande = new CommandeTranslation(perspective, NOUVELLE_X, NOUVELLE_Y);
    }

    // ==================== TESTS: EXÉCUTION ====================

    /**
     * Test: Exécuter la commande modifie la position.
     *
     * Style AAA:
     * - Arrange: setUp() a créé perspective et commande
     * - Act: Exécuter la commande
     * - Assert: La position doit être modifiée
     */
    @Test
    @DisplayName("Devrait modifier la position quand executer() est appelé")
    void shouldChangePosition_whenExecuterCalled() {
        // Arrange: Vérifier précondition
        assertEquals(POSITION_X_INITIALE, perspective.getCentreX(), DELTA,
            "Précondition: position X initiale devrait être " + POSITION_X_INITIALE);
        assertEquals(POSITION_Y_INITIALE, perspective.getCentreY(), DELTA,
            "Précondition: position Y initiale devrait être " + POSITION_Y_INITIALE);

        // Act
        commande.executer();

        // Assert
        assertEquals(NOUVELLE_X, perspective.getCentreX(), DELTA,
            "La position X devrait être modifiée à " + NOUVELLE_X + " après executer()");
        assertEquals(NOUVELLE_Y, perspective.getCentreY(), DELTA,
            "La position Y devrait être modifiée à " + NOUVELLE_Y + " après executer()");
    }

    // ==================== TESTS: UNDO ====================

    /**
     * Test: Annuler la commande restaure la position précédente.
     *
     * Principe Command: Chaque commande doit stocker l'état précédent.
     */
    @Test
    @DisplayName("Devrait restaurer la position précédente quand annuler() est appelé")
    void shouldRestorePreviousPosition_whenAnnulerCalled() {
        // Arrange: Exécuter la commande
        commande.executer();
        assertEquals(NOUVELLE_X, perspective.getCentreX(), DELTA, "Précondition X");
        assertEquals(NOUVELLE_Y, perspective.getCentreY(), DELTA, "Précondition Y");

        // Act: Annuler
        commande.annuler();

        // Assert: Retour à la position initiale
        assertEquals(POSITION_X_INITIALE, perspective.getCentreX(), DELTA,
            "La position X devrait être restaurée à " + POSITION_X_INITIALE + " après annuler()");
        assertEquals(POSITION_Y_INITIALE, perspective.getCentreY(), DELTA,
            "La position Y devrait être restaurée à " + POSITION_Y_INITIALE + " après annuler()");
    }

    /**
     * Test: Annuler sans exécuter d'abord ne devrait rien faire (ou restaurer 0,0).
     *
     * Note: Comportement actuel = restaure ancienX/ancienY (non initialisés = 0.0).
     * Ceci pourrait être un bug à corriger (similaire à CommandeZoom).
     */
    @Test
    @DisplayName("Devrait gérer annuler() appelé sans executer() préalable")
    void shouldHandleAnnulerWithoutExecute() {
        // Arrange: NE PAS exécuter la commande
        double posXAvant = perspective.getCentreX();
        double posYAvant = perspective.getCentreY();

        // Act: Annuler directement
        commande.annuler();

        // Assert: Comportement actuel (peut être considéré comme un bug)
        // Si ancienX/ancienY ne sont pas initialisés, ils sont à 0.0
        // Ce test documente le comportement actuel
        assertEquals(0.0, perspective.getCentreX(), DELTA,
            "Position X après annuler() sans executer()");
        assertEquals(0.0, perspective.getCentreY(), DELTA,
            "Position Y après annuler() sans executer()");
    }

    // ==================== TESTS: IDEMPOTENCE ====================

    /**
     * Test d'idempotence: Exécuter → Annuler → Exécuter = même résultat.
     *
     * Principe: Les commandes doivent être reproductibles.
     */
    @Test
    @DisplayName("Devrait être idempotent (execute→undo→execute = même état)")
    void shouldBeIdempotent_whenExecuteUndoExecuteCalled() {
        // Act: Execute → Undo → Execute
        commande.executer();
        double xApresExecute1 = perspective.getCentreX();
        double yApresExecute1 = perspective.getCentreY();

        commande.annuler();
        commande.executer();
        double xApresExecute2 = perspective.getCentreX();
        double yApresExecute2 = perspective.getCentreY();

        // Assert: Les deux exécutions donnent le même résultat
        assertEquals(xApresExecute1, xApresExecute2, DELTA,
            "Position X: exécuter 2 fois devrait donner le même résultat");
        assertEquals(yApresExecute1, yApresExecute2, DELTA,
            "Position Y: exécuter 2 fois devrait donner le même résultat");
        assertEquals(NOUVELLE_X, xApresExecute2, DELTA,
            "Position X finale devrait être " + NOUVELLE_X);
        assertEquals(NOUVELLE_Y, yApresExecute2, DELTA,
            "Position Y finale devrait être " + NOUVELLE_Y);
    }

    /**
     * Test d'idempotence inverse: Annuler → Exécuter → Annuler = état initial.
     */
    @Test
    @DisplayName("Devrait être idempotent (undo→execute→undo = état initial)")
    void shouldBeIdempotent_whenUndoExecuteUndoCalled() {
        // Arrange: Exécuter une fois pour avoir quelque chose à annuler
        commande.executer();

        // Act: Undo → Execute → Undo
        commande.annuler();
        commande.executer();
        commande.annuler();

        // Assert: Retour à l'état initial
        assertEquals(POSITION_X_INITIALE, perspective.getCentreX(), DELTA,
            "Après undo→execute→undo, position X devrait être à l'état initial");
        assertEquals(POSITION_Y_INITIALE, perspective.getCentreY(), DELTA,
            "Après undo→execute→undo, position Y devrait être à l'état initial");
    }

    // ==================== TESTS: EXÉCUTIONS MULTIPLES ====================

    /**
     * Test: Exécuter la commande plusieurs fois.
     */
    @Test
    @DisplayName("Devrait appliquer la translation à chaque exécution")
    void shouldApplyTranslationOnEachExecution_whenExecuterCalledMultipleTimes() {
        // Act: Exécuter 3 fois
        commande.executer();
        commande.executer();
        commande.executer();

        // Assert: La position devrait toujours être (NOUVELLE_X, NOUVELLE_Y)
        assertEquals(NOUVELLE_X, perspective.getCentreX(), DELTA,
            "Position X devrait être " + NOUVELLE_X + " après plusieurs exécutions");
        assertEquals(NOUVELLE_Y, perspective.getCentreY(), DELTA,
            "Position Y devrait être " + NOUVELLE_Y + " après plusieurs exécutions");
    }

    // ==================== TESTS: ENCHAÎNEMENT DE COMMANDES ====================

    /**
     * Test: Enchaîner plusieurs commandes de translation.
     *
     * Scenario: (0,0) → (100,50) → (200,100), puis undo 2 fois.
     */
    @Test
    @DisplayName("Devrait gérer l'enchaînement de commandes correctement")
    void shouldHandleCommandChaining_whenMultipleCommandsExecuted() {
        // Arrange
        CommandeTranslation commande1 = new CommandeTranslation(perspective, 100.0, 50.0);
        CommandeTranslation commande2 = new CommandeTranslation(perspective, 200.0, 100.0);

        // Act: Exécuter les 2 commandes
        commande1.executer();
        assertEquals(100.0, perspective.getCentreX(), DELTA, "Après commande1 X");
        assertEquals(50.0, perspective.getCentreY(), DELTA, "Après commande1 Y");

        commande2.executer();
        assertEquals(200.0, perspective.getCentreX(), DELTA, "Après commande2 X");
        assertEquals(100.0, perspective.getCentreY(), DELTA, "Après commande2 Y");

        // Undo commande2
        commande2.annuler();
        assertEquals(100.0, perspective.getCentreX(), DELTA, "Après undo commande2 X");
        assertEquals(50.0, perspective.getCentreY(), DELTA, "Après undo commande2 Y");

        // Undo commande1
        commande1.annuler();
        assertEquals(POSITION_X_INITIALE, perspective.getCentreX(), DELTA, "Après undo commande1 X");
        assertEquals(POSITION_Y_INITIALE, perspective.getCentreY(), DELTA, "Après undo commande1 Y");
    }

    // ==================== TESTS: VALEURS LIMITES ====================

    /**
     * Test: Translation vers position négative.
     */
    @Test
    @DisplayName("Devrait accepter des positions négatives")
    void shouldAcceptNegativePositions_whenExecuterCalled() {
        // Arrange
        CommandeTranslation commandeNegative = new CommandeTranslation(perspective, -50.0, -100.0);

        // Act
        commandeNegative.executer();

        // Assert
        assertEquals(-50.0, perspective.getCentreX(), DELTA,
            "Position X devrait accepter des valeurs négatives");
        assertEquals(-100.0, perspective.getCentreY(), DELTA,
            "Position Y devrait accepter des valeurs négatives");
    }

    /**
     * Test: Translation vers (0, 0).
     */
    @Test
    @DisplayName("Devrait accepter la translation vers l'origine (0, 0)")
    void shouldAcceptOriginTranslation_whenExecuterCalled() {
        // Arrange: D'abord déplacer ailleurs
        perspective.setCentre(100.0, 100.0);

        CommandeTranslation commandeOrigine = new CommandeTranslation(perspective, 0.0, 0.0);

        // Act
        commandeOrigine.executer();

        // Assert
        assertEquals(0.0, perspective.getCentreX(), DELTA,
            "Position X devrait être 0");
        assertEquals(0.0, perspective.getCentreY(), DELTA,
            "Position Y devrait être 0");
    }

    /**
     * Test: Translation vers très grandes valeurs.
     */
    @Test
    @DisplayName("Devrait accepter de très grandes positions")
    void shouldAcceptVeryLargePositions_whenExecuterCalled() {
        // Arrange
        double xTresGrand = 10000.0;
        double yTresGrand = 20000.0;
        CommandeTranslation commandeGrande = new CommandeTranslation(perspective, xTresGrand, yTresGrand);

        // Act
        commandeGrande.executer();

        // Assert
        assertEquals(xTresGrand, perspective.getCentreX(), DELTA,
            "Position X devrait accepter de très grandes valeurs");
        assertEquals(yTresGrand, perspective.getCentreY(), DELTA,
            "Position Y devrait accepter de très grandes valeurs");
    }

    // ==================== TESTS: CAS SPÉCIAUX ====================

    /**
     * Test: Translation vers la même position (pas de changement).
     */
    @Test
    @DisplayName("Devrait exécuter même si la position est identique")
    void shouldExecuteEvenIfPositionIsSame_whenExecuterCalled() {
        // Arrange: Créer une commande avec la même position
        CommandeTranslation commandeMemePos = new CommandeTranslation(
            perspective, POSITION_X_INITIALE, POSITION_Y_INITIALE
        );

        // Act
        commandeMemePos.executer();

        // Assert: La position reste la même (pas d'erreur)
        assertEquals(POSITION_X_INITIALE, perspective.getCentreX(), DELTA,
            "Position X devrait rester inchangée");
        assertEquals(POSITION_Y_INITIALE, perspective.getCentreY(), DELTA,
            "Position Y devrait rester inchangée");

        // Undo devrait aussi fonctionner
        commandeMemePos.annuler();
        assertEquals(POSITION_X_INITIALE, perspective.getCentreX(), DELTA,
            "Après undo, position X devrait toujours être " + POSITION_X_INITIALE);
        assertEquals(POSITION_Y_INITIALE, perspective.getCentreY(), DELTA,
            "Après undo, position Y devrait toujours être " + POSITION_Y_INITIALE);
    }

    /**
     * Test: Translation diagonale (modifier X et Y simultanément).
     */
    @Test
    @DisplayName("Devrait modifier X et Y simultanément (translation diagonale)")
    void shouldModifyBothCoordinates_whenDiagonalTranslation() {
        // Act
        commande.executer();

        // Assert: Les deux coordonnées changent en même temps
        assertEquals(NOUVELLE_X, perspective.getCentreX(), DELTA,
            "X devrait être modifié");
        assertEquals(NOUVELLE_Y, perspective.getCentreY(), DELTA,
            "Y devrait être modifié");

        // Vérifier que c'est bien une modification simultanée (atomique)
        // Si une seule coordonnée avait changé, ce serait un bug
        boolean xChanged = perspective.getCentreX() != POSITION_X_INITIALE;
        boolean yChanged = perspective.getCentreY() != POSITION_Y_INITIALE;

        assertTrue(xChanged && yChanged,
            "Translation diagonale devrait modifier X ET Y simultanément");
    }

    /**
     * Test: Translation horizontale uniquement (Y = 0).
     */
    @Test
    @DisplayName("Devrait permettre une translation purement horizontale")
    void shouldAllowHorizontalTranslation_onlyX() {
        // Arrange
        CommandeTranslation commandeHorizontale = new CommandeTranslation(perspective, 150.0, 0.0);

        // Act
        commandeHorizontale.executer();

        // Assert
        assertEquals(150.0, perspective.getCentreX(), DELTA,
            "X devrait être modifié");
        assertEquals(0.0, perspective.getCentreY(), DELTA,
            "Y devrait rester à 0");
    }

    /**
     * Test: Translation verticale uniquement (X = 0).
     */
    @Test
    @DisplayName("Devrait permettre une translation purement verticale")
    void shouldAllowVerticalTranslation_onlyY() {
        // Arrange
        CommandeTranslation commandeVerticale = new CommandeTranslation(perspective, 0.0, 200.0);

        // Act
        commandeVerticale.executer();

        // Assert
        assertEquals(0.0, perspective.getCentreX(), DELTA,
            "X devrait rester à 0");
        assertEquals(200.0, perspective.getCentreY(), DELTA,
            "Y devrait être modifié");
    }
}
