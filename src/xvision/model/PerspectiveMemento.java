package xvision.model;

/**
 * Memento pour sauvegarder l'état complet d'une perspective.
 * Principe Memento (GoF) : encapsule l'état sans exposer les détails internes.
 *
 */
public class PerspectiveMemento {

    // État sauvegardé
    private final double zoom;
    private final double x;
    private final double y;
    private final int idPerspective;

    /**
     * Constructeur du Memento.
     * @param zoom Valeur du zoom
     * @param x Position X
     * @param y Position Y
     * @param idPerspective Identifiant de la perspective
     */    
    public PerspectiveMemento(int idPerspective, double zoom, double x, double y) {
        this.idPerspective = idPerspective;
        this.zoom = zoom;
        this.x = x;
        this.y = y;
    }

    public double getZoom() { return zoom; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getIdPerspective() { return idPerspective; }
}
