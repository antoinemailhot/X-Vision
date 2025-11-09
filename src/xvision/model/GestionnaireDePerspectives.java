package xvision.model;

import java.util.UUID;

/**
 * Gestionnaire central des perspectives de X-Vision.
 * Implémente le patron Composite pour gérer des structures hiérarchiques
 * et s’intègre dans le modèle MVC comme point d’entrée du module « Perspective ».
 */
public class GestionnaireDePerspectives {

    // Racine de la hiérarchie des perspectives
    private PerspectiveComponent racine;

    public GestionnaireDePerspectives() {
        this.racine = new PerspectiveComposite();
    }

    /**
     * Ajoute une nouvelle perspective simple à la racine ou à un groupe spécifique.
     */
    public void ajouterPerspectiveSimple(double zoom, double x, double y) {
        PerspectiveLeaf nouvelle = new PerspectiveLeaf(zoom, x, y);
        racine.ajouter(nouvelle);
    }

    /**
     * Ajoute un sous-groupe de perspectives.
     */
    public PerspectiveComposite creerSousGroupe() {
        PerspectiveComposite groupe = new PerspectiveComposite();
        racine.ajouter(groupe);
        return groupe;
    }

    /**
     * Supprime une perspective spécifique du composite racine.
     */
    public void supprimerPerspective(PerspectiveComponent p) {
        racine.supprimer(p);
    }

    /**
     * Affiche récursivement toutes les perspectives (utile pour debug ou console MVC).
     */
    public void afficherToutes() {
        System.out.println("=== Hiérarchie des perspectives (UUID: " + UUID.randomUUID() + ") ===");
        racine.afficher();
    }

    /**
     * Retourne la racine du composite (pour interaction directe avec le contrôleur).
     */
    public PerspectiveComponent getRacine() {
        return racine;
    }
}
