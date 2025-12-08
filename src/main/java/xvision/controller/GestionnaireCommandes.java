package xvision.controller;

import java.util.Stack;

/**
 * Gestionnaire de l'historique des commandes (Patron Command + Singleton).
 * <p>
 * Cette classe maintient deux piles :
 * <ul>
 *   <li><b>Undo Stack</b> : Commandes exécutées, prêtes à être annulées.</li>
 *   <li><b>Redo Stack</b> : Commandes annulées, prêtes à être refaites.</li>
 * </ul>
 * </p>
 * <p>
 * Elle assure que l'exécution d'une nouvelle commande vide la pile de Redo,
 * garantissant une linéarité cohérente de l'historique.
 * </p>
 */
public class GestionnaireCommandes {
    private static GestionnaireCommandes instance;
    private Stack<Commande> pileUndo = new Stack<>();
    private Stack<Commande> pileRedo = new Stack<>();

    /**
     * Constructeur privé (Singleton).
     */
    private GestionnaireCommandes() { }

    /**
     * Point d'accès unique à l'instance du gestionnaire.
     * @return L'instance unique de GestionnaireCommandes.
     */
    public static GestionnaireCommandes getInstance() {
        if (instance == null) instance = new GestionnaireCommandes();
        return instance;
    }

    /**
     * Exécute une commande et l'ajoute à l'historique.
     * Vide la pile de Redo car une nouvelle branche d'historique est créée.
     * @param cmd La commande à exécuter.
     */
    public void executerCommande(Commande cmd) {
        cmd.executer();
        pileUndo.push(cmd);
        pileRedo.clear();
    }

    /**
     * Annule la dernière commande exécutée.
     * Déplace la commande de la pile Undo vers la pile Redo.
     */
    public void undo() {
        if (!pileUndo.isEmpty()) {
            Commande cmd = pileUndo.pop();
            cmd.annuler();
            pileRedo.push(cmd);
        } else System.out.println("[Undo] Rien à annuler.");
    }

    /**
     * Refait la dernière commande annulée.
     * Déplace la commande de la pile Redo vers la pile Undo.
     */
    public void redo() {
        if (!pileRedo.isEmpty()) {
            Commande cmd = pileRedo.pop();
            cmd.executer();
            pileUndo.push(cmd);
        } else System.out.println("[Redo] Rien à refaire.");
    }

    /**
     * Vérifie si la pile d'annulation est vide.
     * @return true si aucune action à annuler, false sinon.
     */
    public boolean isEmpty() {
        return pileUndo.isEmpty();
    }

    /**
     * Vérifie si la pile de rétablissement est vide.
     * @return true si aucune action à refaire, false sinon.
     */
    public boolean isRedoEmpty() {
        return pileRedo.isEmpty();
    }

    /**
     * Vide complètement l'historique (Undo et Redo).
     * Utile lors du chargement d'un nouveau fichier ou de la réinitialisation de l'application.
     */
    public void clear() {
        pileUndo.clear();
        pileRedo.clear();
    }
}
