package xvision.controller;

import java.util.Stack;

public class GestionnaireCommandes {
    private static GestionnaireCommandes instance;
    private Stack<Commande> pileUndo = new Stack<>();
    private Stack<Commande> pileRedo = new Stack<>();

    private GestionnaireCommandes() { }

    public static GestionnaireCommandes getInstance() {
        if (instance == null) instance = new GestionnaireCommandes();
        return instance;
    }

    public void executerCommande(Commande cmd) {
        cmd.executer();
        pileUndo.push(cmd);
        pileRedo.clear();
    }

    public void undo() {
        if (!pileUndo.isEmpty()) {
            Commande cmd = pileUndo.pop();
            cmd.annuler();
            pileRedo.push(cmd);
        } else System.out.println("[Undo] Rien à annuler.");
    }

    public void redo() {
        if (!pileRedo.isEmpty()) {
            Commande cmd = pileRedo.pop();
            cmd.executer();
            pileUndo.push(cmd);
        } else System.out.println("[Redo] Rien à refaire.");
    }
}
