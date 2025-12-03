package xvision.controller;

/**
 * Interface définissant le contrat pour toutes les commandes de l'application.
 * <p>
 * Cette interface est la clé de voûte du patron <b>Command (GoF)</b>. Elle permet d'encapsuler
 * une requête sous forme d'objet, rendant possible :
 * <ul>
 *   <li>L'annulation (Undo) et le rétablissement (Redo) des actions.</li>
 *   <li>La journalisation des opérations.</li>
 *   <li>Le découplage entre l'objet qui invoque l'opération (IHM) et celui qui l'exécute (Modèle).</li>
 * </ul>
 * </p>
 */
public interface Commande {
    
    /**
     * Exécute la logique métier associée à la commande.
     * Cette méthode modifie l'état du modèle.
     */
    void executer();

    /**
     * Annule les effets de la commande pour revenir à l'état précédent.
     * Cette méthode doit inverser exactement les modifications faites par {@link #executer()}.
     */
    void annuler();
}
