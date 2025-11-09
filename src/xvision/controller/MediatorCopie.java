package xvision.controller;

import xvision.model.Perspective;

/**
 * Médiateur concret chargé de gérer la coordination des opérations
 * de copie et de collage des paramètres d'affichage entre perspectives.
 */
public class MediatorCopie implements Mediator {

    private Perspective source;
    private Perspective cible;

    public void setSource(Perspective source) {
        this.source = source;
    }

    public void setCible(Perspective cible) {
        this.cible = cible;
    }

    /**
     * Réagit à un événement de copie ou de collage.
     */
    @Override
    public void notifier(Object emetteur, String evenement, Object data) {
        if ("copier".equals(evenement) && emetteur instanceof Perspective) {
            setSource((Perspective) emetteur);
        } else if ("coller".equals(evenement) && emetteur instanceof Perspective) {
            setCible((Perspective) emetteur);
            effectuerCollage();
        }
    }

    /**
     * Effectue le transfert des paramètres d'affichage.
     */
    private void effectuerCollage() {
        if (source != null && cible != null) {
            cible.setZoom(source.getZoom());
            cible.setCentre(source.getCentreX(), source.getCentreY());
        }
    }
}
