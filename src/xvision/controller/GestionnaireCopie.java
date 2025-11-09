package xvision.controller;

import xvision.model.PerspectiveLeaf;

public class GestionnaireCopie {
    private MediatorCopie mediator;

    public GestionnaireCopie(MediatorCopie mediator) {
        this.mediator = mediator;
    }

    public void copier(PerspectiveLeaf source) {
        mediator.notifier(source, "copier", null);
    }

    public void coller(PerspectiveLeaf cible) {
        mediator.notifier(cible, "coller", null);
    }
}
