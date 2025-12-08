package xvision.unit;

import org.junit.jupiter.api.*;
import xvision.model.GestionnaireMemento;
import xvision.model.PerspectiveMemento;

import java.util.List;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour GestionnaireMemento (Caretaker du patron Memento).
 *
 * <p>
 * Ce test démontre comment tester le patron Memento avec Undo/Redo.
 * Points clés:
 * <ul>
 *   <li>Vérifier que les mementos sont sauvegardés dans la pile Undo</li>
 *   <li>Vérifier que Undo déplace un memento de Undo vers Redo</li>
 *   <li>Vérifier que Redo déplace un memento de Redo vers Undo</li>
 *   <li>Tester l'idempotence (undo→redo→undo = état initial)</li>
 *   <li>Vérifier que nouvelle sauvegarde vide la pile Redo</li>
 * </ul>
 * </p>
 *
 * <p><b>Patron testé</b>: Memento (GoF) — Caretaker</p>
 * <p><b>Principe GRASP testé</b>: Low Coupling (pas de connaissance de Perspective)</p>
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0 (TP2)
 */
@Tag("unit")
@DisplayName("Tests unitaires: GestionnaireMemento (Patron Memento)")
class GestionnaireMementoTest {

    private GestionnaireMemento gestionnaire;

    // Mementos de test (simulent différents états d'une perspective)
    private PerspectiveMemento memento1;
    private PerspectiveMemento memento2;
    private PerspectiveMemento memento3;

    private static final double DELTA = 0.001;

    @BeforeEach
    void setUp() {
        gestionnaire = new GestionnaireMemento();

        // Créer des mementos de test avec différents états
        memento1 = new PerspectiveMemento(1, 1.0, 0, 0, "", List.of(), null);
        memento2 = new PerspectiveMemento(1, 2.0, 100, 50, "", List.of(), null);
        memento3 = new PerspectiveMemento(1, 3.0, 200, 100, "", List.of(), null);
    }

    @AfterEach
    void tearDown() {
        gestionnaire = null;
    }

    // ==================== TESTS: SAUVEGARDE ====================

    /**
     * Test: Sauvegarder un memento l'ajoute à la pile Undo.
     *
     * Style AAA:
     * - Arrange: Gestionnaire vide
     * - Act: Sauvegarder un memento
     * - Assert: La pile Undo contient le memento
     */
    @Test
    @DisplayName("Devrait ajouter le memento à la pile Undo quand sauvegarder() est appelé")
    void shouldAddMementoToUndoStack_whenSauvegarderCalled() {
        // Arrange: Gestionnaire vide (setUp)
        Stack<PerspectiveMemento> undoAvant = gestionnaire.getPileUndoSnapshot();
        assertTrue(undoAvant.isEmpty(), "Précondition: pile Undo vide");

        // Act
        gestionnaire.sauvegarder(memento1);

        // Assert
        Stack<PerspectiveMemento> undoApres = gestionnaire.getPileUndoSnapshot();
        assertEquals(1, undoApres.size(), "La pile Undo devrait contenir 1 memento");
        assertEquals(memento1, undoApres.peek(), "Le memento sauvegardé devrait être en haut de la pile");
    }

    /**
     * Test: Sauvegarder plusieurs mementos les empile dans l'ordre.
     */
    @Test
    @DisplayName("Devrait empiler les mementos dans l'ordre quand sauvegardés successivement")
    void shouldStackMementosInOrder_whenSavedSuccessively() {
        // Act: Sauvegarder 3 mementos
        gestionnaire.sauvegarder(memento1);
        gestionnaire.sauvegarder(memento2);
        gestionnaire.sauvegarder(memento3);

        // Assert
        Stack<PerspectiveMemento> undo = gestionnaire.getPileUndoSnapshot();
        assertEquals(3, undo.size(), "La pile Undo devrait contenir 3 mementos");

        // Le dernier sauvegardé doit être en haut (LIFO - Last In First Out)
        assertEquals(memento3, undo.pop(), "memento3 devrait être en haut");
        assertEquals(memento2, undo.pop(), "memento2 devrait être au milieu");
        assertEquals(memento1, undo.pop(), "memento1 devrait être en bas");
    }

    /**
     * Test: Sauvegarder un nouveau memento vide la pile Redo.
     *
     * Principe: Nouvelle action = nouvelle branche d'historique, pas d'arbre.
     */
    @Test
    @DisplayName("Devrait vider la pile Redo quand un nouveau memento est sauvegardé")
    void shouldClearRedoStack_whenNewMementoSaved() {
        // Arrange: Créer un historique avec Redo non vide
        gestionnaire.sauvegarder(memento1);
        gestionnaire.sauvegarder(memento2);

        // Undo pour remplir la pile Redo
        gestionnaire.undo(memento2);

        Stack<PerspectiveMemento> redoAvant = gestionnaire.getPileRedoSnapshot();
        assertFalse(redoAvant.isEmpty(), "Précondition: pile Redo non vide");

        // Act: Sauvegarder un nouveau memento
        gestionnaire.sauvegarder(memento3);

        // Assert: La pile Redo doit être vidée
        Stack<PerspectiveMemento> redoApres = gestionnaire.getPileRedoSnapshot();
        assertTrue(redoApres.isEmpty(),
            "La pile Redo devrait être vidée quand un nouveau memento est sauvegardé");
    }

    // ==================== TESTS: UNDO ====================

    /**
     * Test: Undo retourne le memento précédent et le déplace vers Redo.
     *
     * Principe Memento: Le Caretaker ne connaît pas le contenu du memento.
     */
    @Test
    @DisplayName("Devrait retourner le memento précédent quand undo() est appelé")
    void shouldReturnPreviousMemento_whenUndoCalled() {
        // Arrange: Sauvegarder 2 mementos
        gestionnaire.sauvegarder(memento1);
        gestionnaire.sauvegarder(memento2);

        // Act: Undo (état courant = memento2)
        PerspectiveMemento mementoRestauré = gestionnaire.undo(memento2);

        // Assert: Devrait retourner memento1 (l'état précédent)
        assertEquals(memento1, mementoRestauré,
            "Undo devrait retourner le memento précédent (memento1)");
    }

    /**
     * Test: Undo déplace l'état courant vers la pile Redo.
     */
    @Test
    @DisplayName("Devrait déplacer l'état courant vers Redo quand undo() est appelé")
    void shouldMoveCurrentStateToRedo_whenUndoCalled() {
        // Arrange
        gestionnaire.sauvegarder(memento1);
        gestionnaire.sauvegarder(memento2);

        // Act: Undo
        gestionnaire.undo(memento2);

        // Assert: memento2 devrait être dans la pile Redo
        Stack<PerspectiveMemento> redo = gestionnaire.getPileRedoSnapshot();
        assertEquals(1, redo.size(), "La pile Redo devrait contenir 1 memento");
        assertEquals(memento2, redo.peek(), "memento2 devrait être dans la pile Redo");
    }

    /**
     * Test: Undo sur pile vide retourne null (pas d'erreur).
     *
     * Principe: Opération safe, pas d'exception.
     */
    @Test
    @DisplayName("Devrait retourner null si undo() sur pile vide")
    void shouldReturnNull_whenUndoOnEmptyStack() {
        // Arrange: Pile Undo vide (setUp)
        Stack<PerspectiveMemento> undo = gestionnaire.getPileUndoSnapshot();
        assertTrue(undo.isEmpty(), "Précondition: pile Undo vide");

        // Act
        PerspectiveMemento resultat = gestionnaire.undo(memento1);

        // Assert
        assertNull(resultat, "Undo sur pile vide devrait retourner null");
    }

    /**
     * Test: Undo multiple (annuler plusieurs actions successives).
     */
    @Test
    @DisplayName("Devrait gérer plusieurs undo successifs")
    void shouldHandleMultipleUndos_whenCalledSuccessively() {
        // Arrange: Sauvegarder 3 mementos
        gestionnaire.sauvegarder(memento1);
        gestionnaire.sauvegarder(memento2);
        gestionnaire.sauvegarder(memento3);

        // Act & Assert: Undo 3 fois
        PerspectiveMemento undo1 = gestionnaire.undo(memento3);
        assertEquals(memento2, undo1, "1er undo devrait retourner memento2");

        PerspectiveMemento undo2 = gestionnaire.undo(memento2);
        assertEquals(memento1, undo2, "2e undo devrait retourner memento1");

        PerspectiveMemento undo3 = gestionnaire.undo(memento1);
        assertNull(undo3, "3e undo devrait retourner null (pile vide)");
    }

    // ==================== TESTS: REDO ====================

    /**
     * Test: Redo retourne le memento annulé et le replace dans Undo.
     */
    @Test
    @DisplayName("Devrait retourner le memento annulé quand redo() est appelé")
    void shouldReturnUndoneMemento_whenRedoCalled() {
        // Arrange: Sauvegarder puis annuler
        gestionnaire.sauvegarder(memento1);
        gestionnaire.sauvegarder(memento2);
        gestionnaire.undo(memento2); // Annuler memento2

        // Act: Redo (état courant = memento1)
        PerspectiveMemento mementoRestauré = gestionnaire.redo(memento1);

        // Assert: Devrait retourner memento2 (l'état annulé)
        assertEquals(memento2, mementoRestauré,
            "Redo devrait retourner le memento annulé (memento2)");
    }

    /**
     * Test: Redo déplace le memento de Redo vers Undo.
     */
    @Test
    @DisplayName("Devrait replacer le memento dans Undo quand redo() est appelé")
    void shouldMoveMementoBackToUndo_whenRedoCalled() {
        // Arrange
        gestionnaire.sauvegarder(memento1);
        gestionnaire.sauvegarder(memento2);
        gestionnaire.undo(memento2);

        Stack<PerspectiveMemento> undoAvant = gestionnaire.getPileUndoSnapshot();
        assertEquals(1, undoAvant.size(), "Précondition: 1 memento dans Undo");

        // Act: Redo
        gestionnaire.redo(memento1);

        // Assert: memento2 devrait être de retour dans Undo
        Stack<PerspectiveMemento> undoApres = gestionnaire.getPileUndoSnapshot();
        assertEquals(2, undoApres.size(), "La pile Undo devrait avoir 2 mementos");
        assertEquals(memento1, undoApres.get(undoApres.size() - 1),
            "memento1 devrait être replacé dans Undo");
    }

    /**
     * Test: Redo sur pile Redo vide retourne null.
     */
    @Test
    @DisplayName("Devrait retourner null si redo() sur pile Redo vide")
    void shouldReturnNull_whenRedoOnEmptyRedoStack() {
        // Arrange: Aucun undo effectué → pile Redo vide
        gestionnaire.sauvegarder(memento1);

        Stack<PerspectiveMemento> redo = gestionnaire.getPileRedoSnapshot();
        assertTrue(redo.isEmpty(), "Précondition: pile Redo vide");

        // Act
        PerspectiveMemento resultat = gestionnaire.redo(memento1);

        // Assert
        assertNull(resultat, "Redo sur pile Redo vide devrait retourner null");
    }

    // ==================== TESTS: IDEMPOTENCE ====================

    /**
     * Test d'idempotence: Undo → Redo → Undo = état initial.
     *
     * Principe: Les opérations Undo/Redo doivent être reproductibles.
     */
    @Test
    @DisplayName("Devrait être idempotent (undo→redo→undo = état initial)")
    void shouldBeIdempotent_whenUndoRedoUndoCalled() {
        // Arrange: Sauvegarder 2 mementos
        gestionnaire.sauvegarder(memento1);
        gestionnaire.sauvegarder(memento2);

        // Act: Undo → Redo → Undo
        PerspectiveMemento undo1 = gestionnaire.undo(memento2);
        assertEquals(memento1, undo1, "1er undo devrait retourner memento1");

        PerspectiveMemento redo1 = gestionnaire.redo(memento1);
        assertEquals(memento2, redo1, "Redo devrait retourner memento2");

        PerspectiveMemento undo2 = gestionnaire.undo(memento2);
        assertEquals(memento1, undo2, "2e undo devrait retourner memento1 (idempotent)");
    }

    /**
     * Test d'idempotence inverse: Redo → Undo → Redo = même résultat.
     */
    @Test
    @DisplayName("Devrait être idempotent (redo→undo→redo = même résultat)")
    void shouldBeIdempotent_whenRedoUndoRedoCalled() {
        // Arrange: Sauvegarder puis undo
        gestionnaire.sauvegarder(memento1);
        gestionnaire.sauvegarder(memento2);
        gestionnaire.undo(memento2);

        // Act: Redo → Undo → Redo
        PerspectiveMemento redo1 = gestionnaire.redo(memento1);
        assertEquals(memento2, redo1, "1er redo");

        PerspectiveMemento undo1 = gestionnaire.undo(memento2);
        assertEquals(memento1, undo1, "Undo");

        PerspectiveMemento redo2 = gestionnaire.redo(memento1);
        assertEquals(memento2, redo2, "2e redo devrait retourner memento2 (idempotent)");
    }

    // ==================== TESTS: HISTORIQUE ====================

    /**
     * Test: getHistorique() retourne tous les mementos de la pile Undo.
     */
    @Test
    @DisplayName("Devrait retourner l'historique complet (pile Undo)")
    void shouldReturnFullHistory_whenGetHistoriqueCalled() {
        // Arrange: Sauvegarder 3 mementos
        gestionnaire.sauvegarder(memento1);
        gestionnaire.sauvegarder(memento2);
        gestionnaire.sauvegarder(memento3);

        // Act
        List<PerspectiveMemento> historique = gestionnaire.getHistorique();

        // Assert
        assertEquals(3, historique.size(), "L'historique devrait contenir 3 mementos");
        assertEquals(memento1, historique.get(0), "Premier memento");
        assertEquals(memento2, historique.get(1), "Deuxième memento");
        assertEquals(memento3, historique.get(2), "Troisième memento");
    }

    /**
     * Test: getHistoriqueComplet() retourne Undo + Redo.
     */
    @Test
    @DisplayName("Devrait retourner l'historique combiné (Undo + Redo)")
    void shouldReturnCombinedHistory_whenGetHistoriqueCompletCalled() {
        // Arrange: Sauvegarder puis undo
        gestionnaire.sauvegarder(memento1);
        gestionnaire.sauvegarder(memento2);
        gestionnaire.sauvegarder(memento3);
        gestionnaire.undo(memento3); // memento3 dans Redo
        gestionnaire.undo(memento2); // memento2 dans Redo

        // Act
        List<PerspectiveMemento> historiqueComplet = gestionnaire.getHistoriqueComplet();

        // Assert: 1 dans Undo (memento1) + 2 dans Redo (memento2, memento3) = 3
        assertEquals(3, historiqueComplet.size(),
            "L'historique complet devrait contenir 3 mementos");
    }

    // ==================== TESTS: RESTAURATION (PERSISTANCE) ====================

    /**
     * Test: restaurerHistorique() restaure les piles Undo et Redo.
     *
     * Cas d'usage: Charger une perspective depuis un fichier JSON.
     */
    @Test
    @DisplayName("Devrait restaurer les piles Undo et Redo quand restaurerHistorique() est appelé")
    void shouldRestoreUndoAndRedoStacks_whenRestaurerHistoriqueCalled() {
        // Arrange: Créer des piles de test
        Stack<PerspectiveMemento> undoTest = new Stack<>();
        undoTest.push(memento1);
        undoTest.push(memento2);

        Stack<PerspectiveMemento> redoTest = new Stack<>();
        redoTest.push(memento3);

        // Act: Restaurer
        gestionnaire.restaurerHistorique(undoTest, redoTest);

        // Assert: Vérifier que les piles sont correctement restaurées
        Stack<PerspectiveMemento> undoRestauré = gestionnaire.getPileUndoSnapshot();
        assertEquals(2, undoRestauré.size(), "Pile Undo devrait avoir 2 mementos");
        assertEquals(memento2, undoRestauré.peek(), "memento2 devrait être en haut de Undo");

        Stack<PerspectiveMemento> redoRestauré = gestionnaire.getPileRedoSnapshot();
        assertEquals(1, redoRestauré.size(), "Pile Redo devrait avoir 1 memento");
        assertEquals(memento3, redoRestauré.peek(), "memento3 devrait être en haut de Redo");
    }

    /**
     * Test: restaurerHistorique() vide les piles existantes avant de restaurer.
     */
    @Test
    @DisplayName("Devrait vider les piles existantes avant restauration")
    void shouldClearExistingStacks_beforeRestoration() {
        // Arrange: Créer un historique existant
        gestionnaire.sauvegarder(memento1);
        gestionnaire.sauvegarder(memento2);

        Stack<PerspectiveMemento> undoAvant = gestionnaire.getPileUndoSnapshot();
        assertEquals(2, undoAvant.size(), "Précondition: 2 mementos dans Undo");

        // Créer de nouvelles piles pour restauration
        Stack<PerspectiveMemento> nouvelUndo = new Stack<>();
        nouvelUndo.push(memento3);

        Stack<PerspectiveMemento> nouvelRedo = new Stack<>();

        // Act: Restaurer (devrait remplacer, pas ajouter)
        gestionnaire.restaurerHistorique(nouvelUndo, nouvelRedo);

        // Assert: Les anciennes piles doivent être remplacées
        Stack<PerspectiveMemento> undoApres = gestionnaire.getPileUndoSnapshot();
        assertEquals(1, undoApres.size(),
            "Pile Undo devrait contenir seulement le nouveau memento");
        assertEquals(memento3, undoApres.peek(),
            "Seul memento3 devrait être présent");
    }

    // ==================== TESTS: SNAPSHOTS (COPIES DÉFENSIVES) ====================

    /**
     * Test: getPileUndoSnapshot() retourne une COPIE (pas la pile originale).
     *
     * Principe: Encapsulation, ne pas exposer l'état interne mutable.
     */
    @Test
    @DisplayName("Devrait retourner une copie de la pile Undo (pas l'original)")
    void shouldReturnCopyOfUndoStack_notOriginal() {
        // Arrange
        gestionnaire.sauvegarder(memento1);

        // Act: Récupérer le snapshot
        Stack<PerspectiveMemento> snapshot = gestionnaire.getPileUndoSnapshot();

        // Modifier le snapshot (ne devrait pas affecter l'original)
        snapshot.push(memento2);

        // Assert: L'original ne doit pas être modifié
        Stack<PerspectiveMemento> original = gestionnaire.getPileUndoSnapshot();
        assertEquals(1, original.size(),
            "La pile Undo originale ne devrait pas être affectée par modifications du snapshot");
    }

    /**
     * Test: getPileRedoSnapshot() retourne une copie.
     */
    @Test
    @DisplayName("Devrait retourner une copie de la pile Redo (pas l'original)")
    void shouldReturnCopyOfRedoStack_notOriginal() {
        // Arrange
        gestionnaire.sauvegarder(memento1);
        gestionnaire.sauvegarder(memento2);
        gestionnaire.undo(memento2);

        // Act: Récupérer le snapshot
        Stack<PerspectiveMemento> snapshot = gestionnaire.getPileRedoSnapshot();

        // Modifier le snapshot
        snapshot.push(memento3);

        // Assert: L'original ne doit pas être modifié
        Stack<PerspectiveMemento> original = gestionnaire.getPileRedoSnapshot();
        assertEquals(1, original.size(),
            "La pile Redo originale ne devrait pas être affectée");
    }
}
