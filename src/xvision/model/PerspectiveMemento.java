package xvision.model;

import java.util.List;

public class PerspectiveMemento {

    private final int idPerspective;
    private final double zoom;
    private final double x;
    private final double y;
    private final String imageBase64;         // Contenu image complet
    private final List<Crop> crops;           // Liste des crops
    private final List<Annotation> annotations; // Liste des annotations
    private final String etatJSON;            // JSON complet pour sauvegarde/restauration

    // Constructeur complet
    public PerspectiveMemento(int idPerspective, double zoom, double x, double y,
                            String imageBase64, List<Crop> crops,
                            List<Annotation> annotations, String etatJSON) {
        this.idPerspective = idPerspective;
        this.zoom = zoom;
        this.x = x;
        this.y = y;
        // Vérifie que imageBase64 n'est pas null
        this.imageBase64 = (imageBase64 != null) ? imageBase64 : "";
        this.crops = crops != null ? List.copyOf(crops) : List.of();
        this.annotations = annotations != null ? List.copyOf(annotations) : List.of();
        this.etatJSON = etatJSON != null ? etatJSON : "{}";
    }


    // Getters
    public int getIdPerspective() { return idPerspective; }
    public double getZoom() { return zoom; }
    public double getX() { return x; }
    public double getY() { return y; }
    public String getImageBase64() { return imageBase64; }
    public List<Crop> getCrops() { return crops; }
    public List<Annotation> getAnnotations() { return annotations; }
    public String getEtatJSON() { return etatJSON; }

    @Override
    public String toString() {
        return "Memento{perspective=" + idPerspective + ", zoom=" + zoom +
               ", x=" + x + ", y=" + y + ", crops=" + crops.size() +
               ", annotations=" + annotations.size() + "}";
    }
}
