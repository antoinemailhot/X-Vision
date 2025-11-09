package xvision.model;

import xvision.view.ObserverIF;
import java.util.ArrayList;
import java.util.List;

public class PerspectiveLeaf implements PerspectiveComponent {

    private double niveauZoom;
    private double positionX;
    private double positionY;
    private List<ObserverIF> observateurs = new ArrayList<>();

    public PerspectiveLeaf(double zoom, double x, double y) {
        this.niveauZoom = zoom;
        this.positionX = x;
        this.positionY = y;
    }

    public double getZoom() { return niveauZoom; }
    public void setZoom(double zoom) { this.niveauZoom = zoom; }
    public double getCentreX() { return positionX; }
    public double getCentreY() { return positionY; }
    public void setCentre(double x, double y) { this.positionX = x; this.positionY = y; }

    public void ajouterObservateur(ObserverIF obs) { observateurs.add(obs); }
    public void notifierObservateurs() { for (ObserverIF obs : observateurs) obs.actualiser(); }

    @Override
    public void afficher() {
        System.out.println("Leaf - Zoom: " + niveauZoom + ", Pos=(" + positionX + "," + positionY + ")");
    }

    @Override
    public void ajouter(PerspectiveComponent p) { System.out.println("Impossible d’ajouter à une feuille."); }

    @Override
    public void supprimer(PerspectiveComponent p) { }

    @Override
    public List<PerspectiveComponent> getEnfants() { return List.of(); }
}
