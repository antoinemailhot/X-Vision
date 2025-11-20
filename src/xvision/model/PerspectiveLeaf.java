package xvision.model;

import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.io.IOException;
import java.awt.Rectangle;

/**
 * Perspective individuelle (feuille) dans l'arbre Composite.
 * Gère son état interne et son propre historique Memento.
 *
 * Principes respectés :
 * - GRASP (Information Expert) : la feuille connaît son état.
 * - Memento : chaque modification crée un snapshot restaurable.
 * - Observer : notifie les vues lors de tout changement.
 */
public class PerspectiveLeaf extends Sujet implements PerspectiveComponent {

    private final int idPerspective;
    private final GestionnaireMemento gestionnaireMemento = new GestionnaireMemento();

    private double zoom;
    private double x;
    private double y;
    private String cheminImage;
    private Crop crop; // crop actuel

    public PerspectiveLeaf(double zoom, double x, double y) {
        super();
        this.idPerspective = Identifiant.generer();
        this.zoom = zoom;
        this.x = x;
        this.y = y;
        this.crop = null;
        gestionnaireMemento.sauvegarder(creerMemento());
    }

    public int getIdPerspective() { return idPerspective; }

    /**
     * Création d'un Memento avec l'état complet
     */
    public PerspectiveMemento creerMemento() {
        String etatJSON = exporterEnJSON();
        List<Crop> crops = (crop != null) ? List.of(crop) : List.of();
        return new PerspectiveMemento(
            idPerspective,
            zoom,
            x,
            y,
            etatJSON,
            crops,           // crops
            List.of(),       // annotations
            etatJSON
        );
    }



    /**
     * Exporte l'état courant en JSON, avec l'image encodée en Base64
     */
    public String exporterEnJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"idPerspective\":").append(idPerspective).append(",");
        sb.append("\"zoom\":").append(zoom).append(",");
        sb.append("\"x\":").append(x).append(",");
        sb.append("\"y\":").append(y).append(",");

        // Image encodée en Base64
        String base64 = "";
        if (cheminImage != null && !cheminImage.isEmpty()) {
            base64 = encoderImageBase64(cheminImage);
        }
        sb.append("\"imageBase64\":\"").append(base64).append("\",");

        // Crop
        if (crop != null && crop.getZone() != null) {
            Rectangle r = crop.getZone();
            sb.append("\"crop\":{");
            sb.append("\"x\":").append(r.x).append(",");
            sb.append("\"y\":").append(r.y).append(",");
            sb.append("\"width\":").append(r.width).append(",");
            sb.append("\"height\":").append(r.height);
            sb.append("}");
        } else {
            sb.append("\"crop\":null");
        }

        sb.append("}");
        return sb.toString();
    }


    /**
     * Encode le contenu de l'image en Base64
     */
    private String encoderImageBase64(String chemin) {
        try {
            byte[] bytes = Files.readAllBytes(Path.of(chemin));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Restaure l'image à partir d'un contenu Base64 dans un chemin donné
     */
    public void restaurerImageDepuisBase64(String base64, String cheminRestauré) {
        if (base64 == null || base64.isEmpty()) return;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            Files.write(Path.of(cheminRestauré), bytes);
            this.cheminImage = cheminRestauré;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restaure l'état depuis un Memento (Undo/Redo)
     */
    public void restaurerDepuisMemento(PerspectiveMemento m) {
        this.zoom = m.getZoom();
        this.x = m.getX();
        this.y = m.getY();

        // Restaurer l'image
        String base64 = m.getImageBase64();
        if (base64 != null && !base64.isEmpty()) {
            String cheminRestauré = "restored_" + idPerspective + ".png";
            restaurerImageDepuisBase64(base64, cheminRestauré);
        }

        // Restaurer le crop
        if (!m.getCrops().isEmpty() && m.getCrops().get(0) != null) {
            this.crop = new Crop(m.getCrops().get(0).getZone());
        } else {
            this.crop = null;
        }

        notifier();
    }

    // ================== Undo / Redo ==================
    public void undo() {
        PerspectiveMemento m = gestionnaireMemento.undo();
        if (m != null) restaurerDepuisMemento(m);
    }

    public void redo() {
        PerspectiveMemento m = gestionnaireMemento.redo();
        if (m != null) restaurerDepuisMemento(m);
    }

    // ================== Getters / Setters ==================
    public double getZoom() { return zoom; }
    public double getCentreX() { return x; }
    public double getCentreY() { return y; }
    public String getCheminImage() { return cheminImage; }
    public Crop getCrop() { return crop; }

    public void setZoom(double zoom) {
        gestionnaireMemento.sauvegarder(creerMemento());
        this.zoom = zoom;
        notifier();
    }

    public void setCentre(double x, double y) {
        gestionnaireMemento.sauvegarder(creerMemento());
        this.x = x;
        this.y = y;
        notifier();
    }

    public void setCentreX(double x) { setCentre(x, this.y); }
    public void setCentreY(double y) { setCentre(this.x, y); }

    public void setCheminImage(String cheminImage) {
        gestionnaireMemento.sauvegarder(creerMemento());
        this.cheminImage = cheminImage;
        notifier();
    }

    public void setCrop(Rectangle rect) {
        if (rect != null) {
            this.crop = new Crop(rect);
        } else {
            this.crop = null;
        }
        gestionnaireMemento.sauvegarder(creerMemento());
        notifier();
    }

    // ================== Méthodes Composite ==================
    @Override
    public void afficher() {
        System.out.println("Leaf [" + idPerspective + "] - Zoom: " + zoom +
                ", Pos=(" + x + "," + y + "), Crop=" + (crop != null ? crop.getZone() : "null"));
    }

    @Override
    public void ajouter(PerspectiveComponent p) {
        System.out.println("Impossible d’ajouter un enfant à une feuille.");
    }

    @Override
    public void supprimer(PerspectiveComponent p) {}

    @Override
    public List<PerspectiveComponent> getEnfants() { return List.of(); }

    public GestionnaireMemento getMementoManager() { return gestionnaireMemento; }

    public List<PerspectiveMemento> getMementos() {
        return gestionnaireMemento.getHistorique();
    }

    public void ajouterMemento(PerspectiveMemento m) {
        gestionnaireMemento.sauvegarder(m);
    }

    /**
     * Encode l'image actuelle en Base64
     * @return Chaîne Base64 de l'image, ou chaîne vide si pas d'image
     */
    public String imageEnBase64() {
        if (cheminImage == null || cheminImage.isEmpty()) return "";
        return encoderImageBase64(cheminImage);
    }

}
