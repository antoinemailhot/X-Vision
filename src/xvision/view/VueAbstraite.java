package xvision.view;

import xvision.model.Sujet;
import javax.swing.*;

/**
 * Classe abstraite de base pour toutes les vues de X-Vision
 * Implémente le patron Observer et Template Method (GoF)
 * Principe GRASP: Low Coupling - Dépend de l'interface ObserverIF, pas des classes concrètes
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public abstract class VueAbstraite extends JPanel implements ObserverIF {

    /**
     * Le sujet observé par cette vue
     */
    protected Sujet sujetObserve;

    /**
     * Constructeur par défaut
     */
    public VueAbstraite() {
        super();
        this.sujetObserve = null;
    }

    /**
     * Constructeur avec sujet à observer
     * Enregistre automatiquement cette vue comme observateur
     *
     * @param sujet Le sujet à observer
     */
    public VueAbstraite(Sujet sujet) {
        super();
        this.sujetObserve = sujet;
        if (sujet != null) {
            sujet.attacher(this);
        }
    }

    /**
     * Définit le sujet à observer
     * Détache de l'ancien sujet (si existant) et attache au nouveau
     * Principe GRASP: Information Expert
     *
     * @param sujet Le nouveau sujet à observer
     */
    public void setSujet(Sujet sujet) {
        // Détacher de l'ancien sujet
        if (this.sujetObserve != null) {
            this.sujetObserve.detacher(this);
        }

        // Attacher au nouveau sujet
        this.sujetObserve = sujet;
        if (sujet != null) {
            sujet.attacher(this);
        }

        // Rafraîchir immédiatement
        rafraichir();
    }

    /**
     * Retourne le sujet observé
     * @return Le sujet observé
     */
    public Sujet getSujet() {
        return sujetObserve;
    }

    /**
     * Méthode de l'interface ObserverIF
     * Appelée automatiquement quand le sujet change
     * Template Method: appelle rafraichir() qui est spécifique à chaque vue
     */
    @Override
    public final void actualiser() {
        rafraichir();
    }

    /**
     * Méthode abstraite à implémenter par les sous-classes
     * Définit comment la vue doit se rafraîchir visuellement
     * Template Method Pattern
     */
    protected abstract void rafraichir();

    /**
     * Détache cette vue de son sujet observé
     * Utile lors de la destruction de la vue
     */
    public void detacher() {
        if (sujetObserve != null) {
            sujetObserve.detacher(this);
            sujetObserve = null;
        }
    }
}
