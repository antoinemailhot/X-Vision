package xvision.controller;

/**
 * Interface du patron Mediator pour coordonner la communication
 * entre plusieurs perspectives lors des opérations de copie/coller.
 */
public interface Mediator {
    void notifier(Object emetteur, String evenement, Object data);
}
