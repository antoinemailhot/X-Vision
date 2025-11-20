package xvision.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.awt.Rectangle;
import java.util.*;

/**
 * Gestionnaire des perspectives : création, stockage, sauvegarde et chargement
 */
public class GestionnaireDePerspectives {

    private final Map<String, PerspectiveComponent> perspectives = new LinkedHashMap<>();

    /**
     * Ajoute une perspective avec un ID.
     */
    public void ajouterPerspective(String id, PerspectiveComponent p) {
        perspectives.put(id, p);
    }

    /**
     * Ajoute une perspective simple avec ID généré automatiquement.
     */
    public PerspectiveLeaf ajouterPerspectiveSimple(double zoom, double x, double y) {
        String id = "perspective_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
        PerspectiveLeaf leaf = new PerspectiveLeaf(zoom, x, y);
        perspectives.put(id, leaf);
        System.out.println("[GestionnaireDePerspectives] Nouvelle perspective ajoutée : " + id);
        return leaf;
    }

    /**
     * Récupère la dernière perspective ajoutée.
     */
    public PerspectiveLeaf getDernierePerspective() {
        if (perspectives.isEmpty()) return null;
        String lastKey = null;
        for (String key : perspectives.keySet()) lastKey = key;
        PerspectiveComponent p = perspectives.get(lastKey);
        return (p instanceof PerspectiveLeaf) ? (PerspectiveLeaf) p : null;
    }

    /**
     * Sauvegarde une perspective dans un fichier JSON complet.
     */
    public void sauvegarderPerspective(String id, String dossier) {
        PerspectiveComponent p = perspectives.get(id);
        if (!(p instanceof PerspectiveLeaf)) return;

        PerspectiveLeaf leaf = (PerspectiveLeaf) p;

        try {
            Files.createDirectories(Path.of(dossier));
            String json = leaf.exporterEnJSON();
            Path fichier = Path.of(dossier, id + ".json");
            Files.writeString(fichier, json);
            System.out.println("[GestionnaireDePerspectives] Perspective " + id + " sauvegardée.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Charge une perspective depuis un fichier JSON et restaure mementos, image et crop.
     */
    public void chargerPerspective(String cheminFichier) {
        try {
            String json = Files.readString(Path.of(cheminFichier));

            String id = extraireValeurJSON(json, "idPerspective");
            double zoom = Double.parseDouble(extraireValeurJSON(json, "zoom"));
            double x = Double.parseDouble(extraireValeurJSON(json, "x"));
            double y = Double.parseDouble(extraireValeurJSON(json, "y"));
            String base64Image = extraireValeurJSON(json, "imageBase64");

            PerspectiveLeaf leaf = new PerspectiveLeaf(zoom, x, y);

            // Restaurer l'image
            if (base64Image != null && !base64Image.isEmpty()) {
                leaf.restaurerImageDepuisBase64(base64Image, "restored_" + id + ".png");
            }

            // Restaurer le crop
            String cropStr = extraireValeurJSON(json, "crop");
            if (cropStr != null && !cropStr.equals("null")) {
                double cx = Double.parseDouble(extraireValeurJSON(cropStr, "x"));
                double cy = Double.parseDouble(extraireValeurJSON(cropStr, "y"));
                double cw = Double.parseDouble(extraireValeurJSON(cropStr, "width"));
                double ch = Double.parseDouble(extraireValeurJSON(cropStr, "height"));
                leaf.setCrop(new Rectangle((int)cx, (int)cy, (int)cw, (int)ch));
            } else {
                leaf.setCrop(null);
            }

            // Restaurer les mementos
            if (json.contains("\"mementos\"")) {
                String mementosStr = json.substring(json.indexOf("\"mementos\"") + 10, json.lastIndexOf("]"));
                String[] mementoItems = mementosStr.split("\\},");
                for (String item : mementoItems) {
                    item = item.replace("{", "").replace("}", "").trim();
                    if (item.isEmpty()) continue;
                    double mz = 1.0, mx = 0.0, my = 0.0;
                    String[] champs = item.split(",");
                    for (String champ : champs) {
                        champ = champ.trim();
                        if (champ.startsWith("\"zoom\"")) mz = Double.parseDouble(champ.split(":")[1].trim());
                        else if (champ.startsWith("\"centreX\"")) mx = Double.parseDouble(champ.split(":")[1].trim());
                        else if (champ.startsWith("\"centreY\"")) my = Double.parseDouble(champ.split(":")[1].trim());
                    }
                    leaf.ajouterMemento(new PerspectiveMemento(
                        leaf.getIdPerspective(),
                        mz,
                        mx,
                        my,
                        "",           // imageBase64 vide
                        List.of(),    // crops vides
                        List.of(),    // annotations vides
                        "{}"          // JSON vide
                    ));
                }
            }

            perspectives.put(id, leaf);
            System.out.println("[GestionnaireDePerspectives] Perspective " + id + " chargée.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode simple pour extraire une valeur JSON d'une clé.
     * Supporte les objets JSON imbriqués (ex: crop)
     */
    private String extraireValeurJSON(String json, String key) {
        String pattern = "\"" + key + "\":\\s*(\\{.*?\\}|\"[^\"]*\"|[^,\\}]+)";
        return json.replaceAll("(?s).*" + pattern + ".*", "$1").replaceAll("^\"|\"$", "");
    }

    /**
     * Affiche toutes les perspectives.
     */
    public void afficherToutes() {
        for (Map.Entry<String, PerspectiveComponent> entry : perspectives.entrySet()) {
            System.out.println("ID: " + entry.getKey());
            entry.getValue().afficher();
        }
    }
}
