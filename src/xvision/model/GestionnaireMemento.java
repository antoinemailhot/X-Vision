package xvision.model;

import java.util.ArrayList;
import java.util.List;
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
     * @param etatCourant L'état actuel avant l'annulation (pour le Redo).
     * @return Le memento à restaurer, ou null s'il n’y a rien à annuler.
     */
    public PerspectiveMemento undo(PerspectiveMemento etatCourant) {
        if (!pileUndo.isEmpty()) {
            PerspectiveMemento m = pileUndo.pop();
            pileRedo.push(etatCourant);
            return m;
        }
        return null;
    }

    /**
     * Refait la dernière action annulée (Redo).
     * @param etatCourant L'état actuel avant le rétablissement (pour l'Undo).
     * @return Le memento à restaurer, ou null s'il n’y a rien à refaire.
     */
    public PerspectiveMemento redo(PerspectiveMemento etatCourant) {
        if (!pileRedo.isEmpty()) {
            PerspectiveMemento m = pileRedo.pop();
            pileUndo.push(etatCourant);
            return m;
        }
        return null;
    }

    /**
     * Fournit une copie de la pile Undo pour sérialisation ou inspection.
     * @return Une copie immuable de la pile Undo.
     */
    public Stack<PerspectiveMemento> getPileUndoSnapshot() {
        Stack<PerspectiveMemento> copie = new Stack<>();
        copie.addAll(pileUndo);
        return copie;
    }   

    /**
     * Fournit une copie de la pile Redo pour sérialisation ou inspection.
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

    /**
     * Retourne l'historique complet des mementos (pile Undo).
     * Utile pour la sérialisation dans la sauvegarde de perspective.
     * @return Liste des mementos en ordre d'ajout.
     */
    public List<PerspectiveMemento> getHistorique() {
        return new ArrayList<>(pileUndo); // copie défensive
    }

    /**
     * Retourne l'historique complet combiné Undo + Redo (optionnel)
     * @return Liste des mementos incluant les états annulés.
     */
    public List<PerspectiveMemento> getHistoriqueComplet() {
        List<PerspectiveMemento> hist = new ArrayList<>(pileUndo);
        hist.addAll(pileRedo);
        return hist;
    }
}
