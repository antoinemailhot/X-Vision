package xvision.model;

import java.util.List;

/**
 * Interface représentant un composant de perspective.
 * Elle définit les opérations communes pour les perspectives simples et composites.
 */
public interface PerspectiveComponent {
    void afficher(); // Affiche la perspective ou l’ensemble
    void ajouter(PerspectiveComponent p); // Ajoute un enfant (optionnel)
    void supprimer(PerspectiveComponent p); // Supprime un enfant (optionnel)
    List<PerspectiveComponent> getEnfants(); // Retourne les enfants (vide si feuille)
}
