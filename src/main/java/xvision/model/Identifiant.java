package xvision.model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Générateur d'identifiants uniques pour les perspectives.
 * Basé sur un compteur atomique (fait réel, thread-safe).
 */
public class Identifiant {

    private static final AtomicInteger compteur = new AtomicInteger(0);

    public static int generer() {
        return compteur.incrementAndGet();
    }
}
