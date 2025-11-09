package xvision.controller;

import xvision.model.Perspective;

/**
 * Gestionnaire de copie/coller utilisant le Mediator pour orchestrer
 * les transferts entre perspectives selon les stratégies choisies.
 */
public class GestionnaireCopie {
    private MediatorCopie mediator;

    public GestionnaireCopie(MediatorCopie mediator) {
        this.mediator = mediator;
    }

    public void copier(Perspective source) {
        mediator.notifier(source, "copier", null);
    }

    public void coller(Perspective cible) {
        mediator.notifier(cible, "coller", null);
    }
}
