package xvision.controller;

public interface Commande {
    void executer(); // Exécution de la commande
    void annuler();  // Undo de la commande
}
