package xvision.model;

import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.io.IOException;
import java.awt.Rectangle;
import java.util.stream.Collectors;

/**
 * Perspective individuelle (feuille) dans l'arbre Composite.
 * Gère son état interne (zoom, position, image, crop) et son propre historique Memento.
 * 
 * <p>
 * Cette classe est le cœur du modèle. Elle implémente plusieurs patrons :
 * <ul>
 *   <li><b>Composite (Leaf)</b> : C'est un élément terminal de l'arbre de perspectives.</li>
 *   <li><b>Memento</b> : Elle crée et restaure des instantanés de son état pour le Undo/Redo.</li>
 *   <li><b>Observer</b> : Elle notifie les vues (MainGUI, VueAnnotations) lors de changements.</li>
 * </ul>
 * </p>
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 4.0 (Support Deep State & Base64 Caching)
 */
public class PerspectiveLeaf extends Sujet implements PerspectiveComponent {

    private final int idPerspective;
    // Gestionnaire d'historique dédié à cette perspective (Undo/Redo local)
    private final GestionnaireMemento gestionnaireMemento = new GestionnaireMemento();

    // État de la perspective
    private double zoom;
    private double x;
    private double y;
    private String cheminImage;
    private Crop crop; // Zone de rognage actuelle (null si image complète)
    
    // --- Optimisation Mémoire ---
    // Cache pour l'image Base64 afin d'éviter de recharger le fichier disque 
    // et de réencoder l'image à chaque création de Memento (ce qui serait très lent).
    private String cachedBase64 = null;
    private String cachedCheminImage = null;

    /**
     * Constructeur principal.
     * Initialise une perspective et sauvegarde immédiatement son état initial.
     * 
     * @param zoom Niveau de zoom initial (1.0 = 100%)
     * @param x Position X du centre
     * @param y Position Y du centre
     */
    public PerspectiveLeaf(double zoom, double x, double y) {
        super();
        this.idPerspective = Identifiant.generer();
        this.zoom = zoom;
        this.x = x;
        this.y = y;
        this.crop = null;
        
        // Sauvegarde de l'état initial dans l'historique.
        // Cela permet de faire "Undo" pour revenir à l'état de création.
        gestionnaireMemento.sauvegarder(creerMemento());
    }

    public int getIdPerspective() { return idPerspective; }

    /**
     * Crée un Memento (instantané) de l'état actuel.
     * Utilise le cache Base64 pour optimiser la performance.
     * 
     * @return Un objet PerspectiveMemento immuable contenant tout l'état.
     */
    public PerspectiveMemento creerMemento() {
        List<Crop> crops = (crop != null) ? List.of(crop) : List.of();
        // Récupère l'image encodée (depuis le cache si possible)
        String base64 = imageEnBase64();
        return new PerspectiveMemento(
            idPerspective,
            zoom, x, y, base64, crops, null 
        );
    }

    /**
     * Exporte l'état courant et TOUT l'historique en JSON.
     * C'est ici que la magie du "Deep State" opère : on sauvegarde non seulement
     * l'état actuel, mais aussi toutes les piles Undo/Redo.
     * 
     * @return Une chaîne JSON complète.
     */
    public String exporterEnJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"leaf\",");
        sb.append("\"idPerspective\":").append(idPerspective).append(",");
        sb.append("\"zoom\":").append(zoom).append(",");
        sb.append("\"x\":").append(x).append(",");
        sb.append("\"y\":").append(y).append(",");

        // Image encodée en Base64 (lourde, mais nécessaire pour la portabilité)
        String base64 = imageEnBase64();
        sb.append("\"imageBase64\":\"").append(base64).append("\",");

        // Sérialisation du Crop actuel
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

        // --- Sauvegarde de l'historique (Deep History) ---
        // On passe 'base64' aux mementos pour qu'ils puissent écrire "SAME"
        // au lieu de dupliquer l'image si elle n'a pas changé.
        
        sb.append(",\"undoStack\":[");
        List<PerspectiveMemento> undoList = gestionnaireMemento.getPileUndoSnapshot();
        for (int i = 0; i < undoList.size(); i++) {
            sb.append(undoList.get(i).exporterEnJSON(base64));
            if (i < undoList.size() - 1) sb.append(",");
        }
        sb.append("]");

        sb.append(",\"redoStack\":[");
        List<PerspectiveMemento> redoList = gestionnaireMemento.getPileRedoSnapshot();
        for (int i = 0; i < redoList.size(); i++) {
            sb.append(redoList.get(i).exporterEnJSON(base64));
            if (i < redoList.size() - 1) sb.append(",");
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }

    /**
     * Exporte tous les mementos en JSON, incluant le crop de chacun
     */
    public String exporterMementosEnJSON() {
        List<PerspectiveMemento> mementos = gestionnaireMemento.getHistorique();
        String mementosJSON = mementos.stream()
            .map(m -> {
                StringBuilder sb = new StringBuilder();
                sb.append("{");
                sb.append("\"zoom\":").append(m.getZoom()).append(",");
                sb.append("\"centreX\":").append(m.getX()).append(",");
                sb.append("\"centreY\":").append(m.getY()).append(",");
                if (!m.getCrops().isEmpty() && m.getCrops().get(0) != null) {
                    Rectangle r = m.getCrops().get(0).getZone();
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
            })
            .collect(Collectors.joining(","));
        return "[" + mementosJSON + "]";
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
        PerspectiveMemento m = gestionnaireMemento.undo(creerMemento());
        if (m != null) restaurerDepuisMemento(m);
    }

    public void redo() {
        PerspectiveMemento m = gestionnaireMemento.redo(creerMemento());
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

    /**
     * Définit le crop sans créer de memento (utilisé lors du chargement)
     */
    public void setCropSansSauvegarde(Rectangle rect) {
        if (rect != null) {
            this.crop = new Crop(rect);
        } else {
            this.crop = null;
        }
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
        
        // Utilisation du cache si le chemin n'a pas changé
        if (cachedBase64 != null && cheminImage.equals(cachedCheminImage)) {
            return cachedBase64;
        }
        
        cachedBase64 = encoderImageBase64(cheminImage);
        cachedCheminImage = cheminImage;
        return cachedBase64;
    }

}
