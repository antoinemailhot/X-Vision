package xvision.model;

import java.util.Stack;

/**
 * Caretaker du patron Memento.
 * Gère les piles Undo/Redo pour une perspective unique.
 *
 * Principe GRASP : Low Coupling — aucune connaissance de l’interne de la perspective.
 * Patron Memento : Gestion centralisée des états successifs.
 */
public class GestionnaireMemento {

    // Pile des états précédents (Undo)
    private final Stack<PerspectiveMemento> pileUndo = new Stack<>();

    // Pile des états annulés (Redo)
    private final Stack<PerspectiveMemento> pileRedo = new Stack<>();

    /**
     * Sauvegarde un memento dans la pile Undo.
     * Reset la pile Redo (logique standard d’un éditeur).
     *
     * @param memento Memento représentant l’état de la perspective.
     */
    public void sauvegarder(PerspectiveMemento memento) {
        pileUndo.push(memento);
        pileRedo.clear();
    }

    /**
     * Annule la dernière action (Undo) et bascule l’état vers Redo.
     * @return Le memento à restaurer, ou null s'il n’y a rien à annuler.
     */
    public PerspectiveMemento undo() {
        if (!pileUndo.isEmpty()) {
            PerspectiveMemento m = pileUndo.pop();
            pileRedo.push(m);
            return m;
        }
        return null;
    }

    /**
     * Refait la dernière action annulée (Redo).
     * @return Le memento à restaurer, ou null s'il n’y a rien à refaire.
     */
    public PerspectiveMemento redo() {
        if (!pileRedo.isEmpty()) {
            PerspectiveMemento m = pileRedo.pop();
            pileUndo.push(m);
            return m;
        }
        return null;
    }

    /**
     * Fournit une copie de la pile Undo pour sérialisation (Save Prospect).
     * @return Une copie immuable de la pile Undo.
     */
    public Stack<PerspectiveMemento> getPileUndoSnapshot() {
        Stack<PerspectiveMemento> copie = new Stack<>();
        copie.addAll(pileUndo);
        return copie;
    }   

    /**
     * Fournit une copie de la pile Redo pour sérialisation (Save Prospect).
     * @return Une copie immuable de la pile Redo.
     */
    public Stack<PerspectiveMemento> getPileRedoSnapshot() {
        Stack<PerspectiveMemento> copie = new Stack<>();
        copie.addAll(pileRedo);
        return copie;
    }

    /**
     * Restaure les piles Undo/Redo à partir de données sérialisées (Load Prospect).
     * @param undo Pile des états précédents à restaurer.
     * @param redo Pile des états annulés à restaurer.
     */
    public void restaurerHistorique(Stack<PerspectiveMemento> undo, Stack<PerspectiveMemento> redo) {
        pileUndo.clear();
        pileUndo.addAll(undo);
        pileRedo.clear();
        pileRedo.addAll(redo);
    }


}
