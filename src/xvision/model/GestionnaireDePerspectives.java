package xvision.model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Gestionnaire des perspectives : création, stockage, sauvegarde et chargement
 * Utilisation de JSON "maison" sans librairie externe
 */
public class GestionnaireDePerspectives {

    private Map<String, PerspectiveComponent> perspectives = new HashMap<>();

    /**
     * Ajoute une perspective au gestionnaire avec un ID.
     */
    public void ajouterPerspective(String id, PerspectiveComponent p) {
        perspectives.put(id, p);
    }

    /**
     * Récupère une perspective par son ID.
     */
    public PerspectiveComponent getPerspective(String id) {
        return perspectives.get(id);
    }

    /**
     * Supprime une perspective par son ID.
     */
    public void supprimerPerspective(String id) {
        perspectives.remove(id);
    }

    /**
     * Sauvegarde toutes les perspectives dans un dossier.
     */
    public void sauvegarderToutes(String dossier) {
        File dir = new File(dossier);
        if (!dir.exists()) dir.mkdirs();

        for (Map.Entry<String, PerspectiveComponent> entry : perspectives.entrySet()) {
            sauvegarderPerspective(entry.getKey(), dossier);
        }
    }

    /**
     * Sauvegarde une seule perspective dans un fichier JSON.
     */
    public void sauvegarderPerspective(String id, String dossier) {
        PerspectiveComponent p = perspectives.get(id);
        if (p == null) return;

        String etatJSON;
        if (p instanceof PerspectiveLeaf) {
            etatJSON = ((PerspectiveLeaf) p).creerMemento().getEtatJSON();
        } else {
            etatJSON = "{}";
        }

        try {
            Path fichier = Path.of(dossier, id + ".json");
            Files.writeString(fichier, etatJSON);
            System.out.println("[GestionnaireDePerspectives] Perspective " + id + " sauvegardée.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Charge une perspective depuis un fichier JSON.
     */
    public void chargerPerspective(String cheminFichier) {
        try {
            String json = Files.readString(Path.of(cheminFichier));
            String id = extraireValeurJSON(json, "idPerspective");
            double zoom = Double.parseDouble(extraireValeurJSON(json, "zoom"));
            double x = Double.parseDouble(extraireValeurJSON(json, "x"));
            double y = Double.parseDouble(extraireValeurJSON(json, "y"));
            String imageBase64 = extraireValeurJSON(json, "imageBase64");

            PerspectiveLeaf p = new PerspectiveLeaf(zoom, x, y);

            if (imageBase64 != null && !imageBase64.isEmpty()) {
                String cheminRestauré = "restored_" + id + ".png";
                p.restaurerImageDepuisBase64(imageBase64, cheminRestauré);
            }

            perspectives.put(id, p);
            System.out.println("[GestionnaireDePerspectives] Perspective " + id + " chargée.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Extrait la valeur d'une clé dans un JSON maison simple
     */
    private String extraireValeurJSON(String json, String key) {
        String pattern = "\"" + key + "\":\"?([^\"]*)\"?";
        return json.replaceAll(".*" + pattern + ".*", "$1");
    }

    /**
     * Affiche toutes les perspectives
     */
    public void afficherToutes() {
        for (Map.Entry<String, PerspectiveComponent> entry : perspectives.entrySet()) {
            System.out.println("ID: " + entry.getKey());
            entry.getValue().afficher();
        }
    }

    /**
     * Ajoute une perspective simple avec génération automatique d'ID
     */
    public PerspectiveLeaf ajouterPerspectiveSimple(double zoom, double x, double y) {
        String id = "perspective_" + System.currentTimeMillis() + "_" + new Random().nextInt(1000);
        PerspectiveLeaf nouvellePerspective = new PerspectiveLeaf(zoom, x, y);
        perspectives.put(id, nouvellePerspective);
        System.out.println("[GestionnaireDePerspectives] Nouvelle perspective ajoutée : " + id);
        return nouvellePerspective;
    }
}
