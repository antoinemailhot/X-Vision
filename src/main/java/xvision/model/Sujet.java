package xvision.model;

import xvision.view.ObserverIF;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstraite représentant un Sujet observable (Patron Observer - GoF)
 * Gère l'enregistrement et la notification des observateurs.
 * Principe GRASP: Information Expert - Le sujet possède la liste d'observateurs
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public abstract class Sujet {

    /**
     * Liste des observateurs enregistrés
     */
    private List<ObserverIF> observateurs;

    /**
     * Constructeur par défaut
     * Initialise la liste d'observateurs vide
     */
    public Sujet() {
        this.observateurs = new ArrayList<>();
    }

    /**
     * Attache un observateur à ce sujet
     * Principe GRASP: Low Coupling - Interface plutôt que classe concrète
     *
     * @param observateur L'observateur à attacher
     */
    public void attacher(ObserverIF observateur) {
        if (observateur != null && !observateurs.contains(observateur)) {
            observateurs.add(observateur);
        }
    }

    /**
     * Détache un observateur de ce sujet
     *
     * @param observateur L'observateur à détacher
     */
    public void detacher(ObserverIF observateur) {
        observateurs.remove(observateur);
    }

    /**
     * Notifie tous les observateurs d'un changement d'état
     * Appelle la méthode actualiser() de chaque observateur
     */
    protected void notifier() {
        for (ObserverIF observateur : observateurs) {
            observateur.actualiser();
        }
    }

    /**
     * Retourne le nombre d'observateurs attachés
     * Utile pour les tests et le débogage
     *
     * @return Le nombre d'observateurs
     */
    public int getNombreObservateurs() {
        return observateurs.size();
    }
}
