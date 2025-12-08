package xvision.unit;

import org.junit.jupiter.api.*;
import xvision.controller.CommandeZoom;
import xvision.model.PerspectiveLeaf;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour CommandeZoom (Patron Command).
 *
 * <p>
 * Ce test démontre comment tester le patron Command avec Undo/Redo.
 * Points clés:
 * <ul>
 *   <li>Vérifier que executer() modifie l'état</li>
 *   <li>Vérifier que annuler() restaure l'état précédent</li>
 *   <li>Tester l'idempotence (execute → undo → execute = même résultat)</li>
 *   <li>Vérifier que l'état précédent est stocké correctement</li>
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
@DisplayName("Tests unitaires: CommandeZoom (Patron Command)")
class CommandeZoomTest {

    private PerspectiveLeaf perspective;
    private CommandeZoom commande;

    private static final double ZOOM_INITIAL = 1.0;
    private static final double NOUVEAU_ZOOM = 2.5;
    private static final double DELTA = 0.001;

    @BeforeEach
    void setUp() {
        // Créer une perspective avec zoom initial
        perspective = new PerspectiveLeaf(ZOOM_INITIAL, 0, 0);

        // Créer la commande (zoom de 1.0 → 2.5)
        commande = new CommandeZoom(perspective, NOUVEAU_ZOOM);
    }

    // ==================== TESTS: EXÉCUTION ====================

    /**
     * Test: Exécuter la commande modifie le zoom.
     *
     * Style AAA:
     * - Arrange: setUp() a créé perspective et commande
     * - Act: Exécuter la commande
     * - Assert: Le zoom doit être modifié
     */
    @Test
    @DisplayName("Devrait modifier le zoom quand executer() est appelé")
    void shouldChangeZoom_whenExecuterCalled() {
        // Arrange: Vérifier précondition
        assertEquals(ZOOM_INITIAL, perspective.getZoom(), DELTA,
            "Précondition: zoom initial devrait être " + ZOOM_INITIAL);

        // Act
        commande.executer();

        // Assert
        assertEquals(NOUVEAU_ZOOM, perspective.getZoom(), DELTA,
            "Le zoom devrait être modifié à " + NOUVEAU_ZOOM + " après executer()");
    }

    // ==================== TESTS: UNDO ====================

    /**
     * Test: Annuler la commande restaure le zoom précédent.
     *
     * Principe Command: Chaque commande doit stocker l'état précédent.
     */
    @Test
    @DisplayName("Devrait restaurer le zoom précédent quand annuler() est appelé")
    void shouldRestorePreviousZoom_whenAnnulerCalled() {
        // Arrange: Exécuter la commande
        commande.executer();
        assertEquals(NOUVEAU_ZOOM, perspective.getZoom(), DELTA, "Précondition");

        // Act: Annuler
        commande.annuler();

        // Assert: Retour au zoom initial
        assertEquals(ZOOM_INITIAL, perspective.getZoom(), DELTA,
            "Le zoom devrait être restauré à " + ZOOM_INITIAL + " après annuler()");
    }

    /**
     * Test: Annuler sans exécuter d'abord ne devrait rien faire.
     *
     * Principe: Annuler une commande non exécutée est un no-op (safe).
     */
    @Test
    @DisplayName("Ne devrait rien faire si annuler() est appelé sans executer()")
    void shouldDoNothing_whenAnnulerCalledWithoutExecute() {
        // Arrange: NE PAS exécuter la commande
        double zoomAvant = perspective.getZoom();

        // Act: Annuler directement
        commande.annuler();

        // Assert: Le zoom ne devrait PAS changer
        assertEquals(zoomAvant, perspective.getZoom(), DELTA,
            "Le zoom ne devrait pas changer si annuler() est appelé sans executer()");
    }

    // ==================== TESTS: IDEMPOTENCE ====================

    /**
     * Test d'idempotence: Exécuter → Annuler → Exécuter = même résultat.
     *
     * Principe: Les commandes doivent être reproductibles.
     * Exécuter la même commande 2 fois doit donner le même résultat.
     */
    @Test
    @DisplayName("Devrait être idempotent (execute→undo→execute = même état)")
    void shouldBeIdempotent_whenExecuteUndoExecuteCalled() {
        // Act: Execute → Undo → Execute
        commande.executer();
        double zoomApresExecute1 = perspective.getZoom();

        commande.annuler();
        commande.executer();
        double zoomApresExecute2 = perspective.getZoom();

        // Assert: Les deux exécutions donnent le même résultat
        assertEquals(zoomApresExecute1, zoomApresExecute2, DELTA,
            "Exécuter la commande 2 fois devrait donner le même résultat");
        assertEquals(NOUVEAU_ZOOM, zoomApresExecute2, DELTA,
            "Le zoom final devrait être " + NOUVEAU_ZOOM);
    }

    /**
     * Test d'idempotence: Annuler → Exécuter → Annuler = état initial.
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
        assertEquals(ZOOM_INITIAL, perspective.getZoom(), DELTA,
            "Après undo→execute→undo, le zoom devrait être à l'état initial");
    }

    // ==================== TESTS: EXÉCUTIONS MULTIPLES ====================

    /**
     * Test: Exécuter la commande plusieurs fois.
     *
     * Comportement attendu: Chaque exécution applique le nouveau zoom.
     * (Dans certains designs, exécuter 2 fois pourrait être un no-op, mais ici on teste le comportement actuel)
     */
    @Test
    @DisplayName("Devrait appliquer le zoom à chaque exécution")
    void shouldApplyZoomOnEachExecution_whenExecuterCalledMultipleTimes() {
        // Act: Exécuter 3 fois
        commande.executer();
        commande.executer();
        commande.executer();

        // Assert: Le zoom devrait toujours être NOUVEAU_ZOOM
        assertEquals(NOUVEAU_ZOOM, perspective.getZoom(), DELTA,
            "Le zoom devrait être " + NOUVEAU_ZOOM + " même après plusieurs exécutions");
    }

    // ==================== TESTS: ENCHAÎNEMENT DE COMMANDES ====================

    /**
     * Test: Enchaîner plusieurs commandes de zoom.
     *
     * Scenario: Zoom 1.0 → 2.0 → 3.0, puis undo 2 fois.
     */
    @Test
    @DisplayName("Devrait gérer l'enchaînement de commandes correctement")
    void shouldHandleCommandChaining_whenMultipleCommandsExecuted() {
        // Arrange
        CommandeZoom commande1 = new CommandeZoom(perspective, 2.0);
        CommandeZoom commande2 = new CommandeZoom(perspective, 3.0);

        // Act: Exécuter les 2 commandes
        commande1.executer();
        assertEquals(2.0, perspective.getZoom(), DELTA, "Après commande1");

        commande2.executer();
        assertEquals(3.0, perspective.getZoom(), DELTA, "Après commande2");

        // Undo commande2
        commande2.annuler();
        assertEquals(2.0, perspective.getZoom(), DELTA, "Après undo commande2");

        // Undo commande1
        commande1.annuler();
        assertEquals(ZOOM_INITIAL, perspective.getZoom(), DELTA, "Après undo commande1");
    }

    // ==================== TESTS: VALEURS LIMITES ====================

    /**
     * Test: Zoom à une valeur très petite (mais > 0).
     */
    @Test
    @DisplayName("Devrait accepter un zoom très petit (> 0)")
    void shouldAcceptVerySmallZoom_whenExecuterCalled() {
        // Arrange
        double zoomTresPetit = 0.1;
        CommandeZoom commandePetitZoom = new CommandeZoom(perspective, zoomTresPetit);

        // Act
        commandePetitZoom.executer();

        // Assert
        assertEquals(zoomTresPetit, perspective.getZoom(), DELTA,
            "Le zoom devrait accepter des valeurs très petites");
        assertTrue(perspective.getZoom() > 0,
            "Le zoom doit rester strictement positif");
    }

    /**
     * Test: Zoom à une valeur très grande.
     */
    @Test
    @DisplayName("Devrait accepter un zoom très grand")
    void shouldAcceptVeryLargeZoom_whenExecuterCalled() {
        // Arrange
        double zoomTresGrand = 100.0;
        CommandeZoom commandeGrandZoom = new CommandeZoom(perspective, zoomTresGrand);

        // Act
        commandeGrandZoom.executer();

        // Assert
        assertEquals(zoomTresGrand, perspective.getZoom(), DELTA,
            "Le zoom devrait accepter des valeurs très grandes");
    }

    // ==================== TESTS: CAS LIMITES ====================

    /**
     * Test: Zoom à la même valeur (pas de changement).
     *
     * Comportement: La commande s'exécute quand même (pas d'optimisation de no-op).
     */
    @Test
    @DisplayName("Devrait exécuter même si le zoom est identique")
    void shouldExecuteEvenIfZoomIsSame_whenExecuterCalled() {
        // Arrange: Créer une commande avec le même zoom
        CommandeZoom commandeMemeZoom = new CommandeZoom(perspective, ZOOM_INITIAL);

        // Act
        commandeMemeZoom.executer();

        // Assert: Le zoom reste le même (pas d'erreur)
        assertEquals(ZOOM_INITIAL, perspective.getZoom(), DELTA,
            "Le zoom devrait rester inchangé");

        // Undo devrait aussi fonctionner
        commandeMemeZoom.annuler();
        assertEquals(ZOOM_INITIAL, perspective.getZoom(), DELTA,
            "Après undo, le zoom devrait toujours être " + ZOOM_INITIAL);
    }
}
