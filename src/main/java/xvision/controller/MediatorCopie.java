package xvision.controller;

import xvision.model.PerspectiveLeaf;

public class MediatorCopie implements Mediator {

    private PerspectiveLeaf source;
    private PerspectiveLeaf cible;

    @Override
    public void notifier(Object emetteur, String evenement, Object data) {
        if ("copier".equals(evenement) && emetteur instanceof PerspectiveLeaf)
            source = (PerspectiveLeaf) emetteur;
        else if ("coller".equals(evenement) && emetteur instanceof PerspectiveLeaf) {
            cible = (PerspectiveLeaf) emetteur;
            if (source != null && cible != null)
                cible.setCentre(source.getCentreX(), source.getCentreY());
        }
    }
}
