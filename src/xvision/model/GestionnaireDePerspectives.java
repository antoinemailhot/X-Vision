package xvision.model;

import java.io.File;
import java.util.*;
import java.nio.file.Files;

/**
 * Gestionnaire des perspectives : création, stockage, sauvegarde, chargement
 * Utilisation de JSON "maison" sans librairie externe
 */
public class GestionnaireDePerspectives {

    private Map<String, PerspectiveComponent> perspectives = new HashMap<>();

    public void ajouterPerspective(String id, PerspectiveComponent p) {
        perspectives.put(id, p);
    }

    public PerspectiveComponent getPerspective(String id) {
        return perspectives.get(id);
    }

    public void supprimerPerspective(String id) {
        perspectives.remove(id);
    }

    public void sauvegarderToutes(String dossier) {
        File dir = new File(dossier);
        if (!dir.exists()) dir.mkdirs();

        for (Map.Entry<String, PerspectiveComponent> entry : perspectives.entrySet()) {
            sauvegarderPerspective(entry.getKey(), dossier);
        }
    }

    public void sauvegarderPerspective(String id, String dossier) {
        PerspectiveComponent p = perspectives.get(id);
        if (p == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"id\": \"").append(id).append("\",\n");
        sb.append("  \"nom\": \"").append(p.getClass().getSimpleName()).append("\",\n");
        sb.append("  \"zoom\": ").append(getZoom(p)).append(",\n");
        sb.append("  \"centerX\": ").append(getCentreX(p)).append(",\n");
        sb.append("  \"centerY\": ").append(getCentreY(p)).append(",\n");
        sb.append("  \"timestamp\": ").append(System.currentTimeMillis() / 1000).append("\n");
        sb.append("}\n");

        try {
            File fichier = new File(dossier + "/" + id + ".json");
            Files.writeString(fichier.toPath(), sb.toString());
            System.out.println("[GestionnaireDePerspectives] Perspective " + id + " sauvegardée.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void chargerPerspective(String cheminFichier) {
        try {
            List<String> lignes = Files.readAllLines(new File(cheminFichier).toPath());
            double zoom = 1.0, x = 0.0, y = 0.0;
            String id = "unknown";

            for (String ligne : lignes) {
                ligne = ligne.trim();
                if (ligne.startsWith("\"id\"")) {
                    id = ligne.split(":")[1].replace("\"","").replace(",","").trim();
                } else if (ligne.startsWith("\"zoom\"")) {
                    zoom = Double.parseDouble(ligne.split(":")[1].replace(",","").trim());
                } else if (ligne.startsWith("\"centerX\"")) {
                    x = Double.parseDouble(ligne.split(":")[1].replace(",","").trim());
                } else if (ligne.startsWith("\"centerY\"")) {
                    y = Double.parseDouble(ligne.split(":")[1].replace(",","").trim());
                }
            }

            PerspectiveLeaf p = new PerspectiveLeaf(zoom, x, y);
            ajouterPerspective(id, p);
            System.out.println("[GestionnaireDePerspectives] Perspective " + id + " chargée.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double getZoom(PerspectiveComponent p) {
        if (p instanceof PerspectiveLeaf) return ((PerspectiveLeaf) p).getZoom();
        return 1.0;
    }

    private double getCentreX(PerspectiveComponent p) {
        if (p instanceof PerspectiveLeaf) return ((PerspectiveLeaf) p).getCentreX();
        return 0.0;
    }

    private double getCentreY(PerspectiveComponent p) {
        if (p instanceof PerspectiveLeaf) return ((PerspectiveLeaf) p).getCentreY();
        return 0.0;
    }

    public void afficherToutes() {
        for (Map.Entry<String, PerspectiveComponent> entry : perspectives.entrySet()) {
            System.out.println("ID: " + entry.getKey());
            entry.getValue().afficher();
        }
    }
}
