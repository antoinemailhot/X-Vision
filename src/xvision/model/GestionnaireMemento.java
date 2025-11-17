package xvision.model;

import java.util.Stack;

/**
 * Caretaker du patron Memento.
 * Gère les piles Undo/Redo pour restaurer l'état des perspectives.
 *
 * Principe GRASP : Low Coupling — les contrôleurs ne connaissent pas l'état interne.
 * Patron Memento : Gestion centralisée des états successifs.
 *
 */
public class GestionnaireMemento {

    private Stack<PerspectiveMemento> pileUndo = new Stack<>();
    private Stack<PerspectiveMemento> pileRedo = new Stack<>();

    /**
     * Sauvegarde un moment de l'état.
     * @param memento Le memento à sauvegarder.
     */
    public void sauvegarder(PerspectiveMemento memento) {
        pileUndo.push(memento);
        pileRedo.clear();
    }

    /**
     * Récupère le dernier état Undo.
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
     * Récupère le dernier état Redo.
     */
    public PerspectiveMemento redo() {
        if (!pileRedo.isEmpty()) {
            PerspectiveMemento m = pileRedo.pop();
            pileUndo.push(m);
            return m;
        }
        return null;
    }
}
