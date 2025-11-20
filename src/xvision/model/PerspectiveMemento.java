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
    private final String etatJSON;

    /**
     * Constructeur du Memento.
     * @param zoom Valeur du zoom
     * @param x Position X
     * @param y Position Y
     * @param idPerspective Identifiant de la perspective
     * @param etatJSON État complet en JSON
     */    
    public PerspectiveMemento(
    int idPerspective,
    double zoom,
    double x,
    double y,
    String etatJSON
    ) {
        if (etatJSON == null) {
            throw new IllegalArgumentException("etatJSON ne peut pas être null");
        }

        this.idPerspective = idPerspective;
        this.zoom = zoom;
        this.x = x;
        this.y = y;
        this.etatJSON = etatJSON;
    }


    public double getZoom() { return zoom; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getIdPerspective() { return idPerspective; }
    public String getEtatJSON() { return etatJSON; }

    /**
     * Représentation textuelle du Memento pour le débogage.
     */
    @Override
    public String toString() {
        return "Memento{perspective=" + idPerspective + ", zoom=" + zoom + ", x=" + x + ", y=" + y + "}";
    }

}
