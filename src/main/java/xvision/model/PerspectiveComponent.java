package xvision.model;

import java.util.List;

/**
 * Interface commune pour tous les éléments de la hiérarchie des perspectives.
 * <p>
 * Cette interface est la base du patron <b>Composite (GoF)</b>. Elle permet de traiter
 * de manière uniforme les objets individuels ({@link PerspectiveLeaf}) et les compositions
 * d'objets ({@link PerspectiveComposite}).
 * </p>
 * <h3>Opérations :</h3>
 * <ul>
 *   <li><b>Gestion de la hiérarchie</b> : ajouter, supprimer, obtenir les enfants.</li>
 *   <li><b>Opérations métier</b> : afficher, exporterEnJSON.</li>
 * </ul>
 */
public interface PerspectiveComponent {
    
    /**
     * Affiche les informations du composant (debug/console).
     */
    void afficher();

    /**
     * Ajoute un composant enfant.
     * Lance une exception ou ne fait rien si l'opération n'est pas supportée (ex: Leaf).
     * @param p Le composant à ajouter.
     */
    void ajouter(PerspectiveComponent p);

    /**
     * Supprime un composant enfant.
     * @param p Le composant à supprimer.
     */
    void supprimer(PerspectiveComponent p);

    /**
     * Retourne la liste des enfants du composant.
     * @return Une liste de composants (vide pour une feuille).
     */
    List<PerspectiveComponent> getEnfants();

    /**
     * Sérialise le composant et toute sa sous-hiérarchie en format JSON.
     * @return Une chaîne de caractères JSON.
     */
    String exporterEnJSON();
}
