package xvision.model;

import java.util.Collections;
import java.util.List;

/**
 * Classe représentant une perspective simple (feuille du composite).
 */
public class PerspectiveLeaf implements PerspectiveComponent {
    private double niveauZoom;
    private double positionX;
    private double positionY;

    public PerspectiveLeaf(double zoom, double x, double y) {
        this.niveauZoom = zoom;
        this.positionX = x;
        this.positionY = y;
    }

    @Override
    public void afficher() {
        System.out.println("Perspective simple - Zoom: " + niveauZoom +
                           ", Position: (" + positionX + ", " + positionY + ")");
    }

    @Override
    public void ajouter(PerspectiveComponent p) {
        // Une feuille ne peut pas contenir d’enfant.
        System.out.println("[Avertissement] Impossible d’ajouter un enfant à une feuille.");
    }

    @Override
    public void supprimer(PerspectiveComponent p) {
        // Rien à faire pour une feuille.
    }

    @Override
    public List<PerspectiveComponent> getEnfants() {
        return Collections.emptyList();
    }
}
