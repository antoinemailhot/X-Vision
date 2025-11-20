package xvision.model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Memento représentant l'état complet d'une PerspectiveLeaf
 */
public class PerspectiveMemento {

    private final int idPerspective;
    private final double zoom;
    private final double x;
    private final double y;
    private final String imageBase64;
    private final List<Crop> crops;
    private final List<String> annotations;
    private final String etatJSON; // sauvegarde complète pour restore rapide

    public PerspectiveMemento(int idPerspective,
                              double zoom,
                              double x,
                              double y,
                              String imageBase64,
                              List<Crop> crops,
                              List<String> annotations,
                              String etatJSON) {
        this.idPerspective = idPerspective;
        this.zoom = zoom;
        this.x = x;
        this.y = y;
        this.imageBase64 = imageBase64;
        this.crops = crops != null ? crops : new ArrayList<>();
        this.annotations = annotations != null ? annotations : new ArrayList<>();
        this.etatJSON = etatJSON;
    }

    // ================== Getters ==================
    public int getIdPerspective() { return idPerspective; }
    public double getZoom() { return zoom; }
    public double getX() { return x; }
    public double getY() { return y; }
    public String getImageBase64() { return imageBase64; }
    public List<Crop> getCrops() { return crops; }
    public List<String> getAnnotations() { return annotations; }
    public String getEtatJSON() { return etatJSON; }

    /**
     * Convertit ce memento en JSON complet (chaîne) pour sauvegarde.
     */
    public String exporterEnJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"zoom\":").append(zoom).append(",");
        sb.append("\"centreX\":").append(x).append(",");
        sb.append("\"centreY\":").append(y).append(",");
        sb.append("\"imageBase64\":\"").append(imageBase64 != null ? imageBase64 : "").append("\",");

        // Crops
        sb.append("\"crops\":[");
        for (int i = 0; i < crops.size(); i++) {
            Crop c = crops.get(i);
            Rectangle r = c.getZone();
            sb.append("{")
              .append("\"x\":").append(r.x).append(",")
              .append("\"y\":").append(r.y).append(",")
              .append("\"width\":").append(r.width).append(",")
              .append("\"height\":").append(r.height)
              .append("}");
            if (i < crops.size() - 1) sb.append(",");
        }
        sb.append("],");

        // Annotations
        sb.append("\"annotations\":[");
        for (int i = 0; i < annotations.size(); i++) {
            sb.append("\"").append(annotations.get(i).replace("\"","\\\"")).append("\"");
            if (i < annotations.size() - 1) sb.append(",");
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }

    /**
     * Crée un memento à partir d'une chaîne JSON (simplifiée) et idPerspective
     */
    public static PerspectiveMemento fromJSON(String json, int idPerspective) {
        double zoom = 1.0, x = 0.0, y = 0.0;
        String imageBase64 = "";
        List<Crop> crops = new ArrayList<>();
        List<String> annotations = new ArrayList<>();

        json = json.trim();

        // Nettoyage des accolades
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        String[] champs = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // split en ignorant les virgules dans les chaînes

        for (String champ : champs) {
            champ = champ.trim();
            if (champ.startsWith("\"zoom\"")) {
                zoom = Double.parseDouble(champ.split(":")[1].trim());
            } else if (champ.startsWith("\"centreX\"")) {
                x = Double.parseDouble(champ.split(":")[1].trim());
            } else if (champ.startsWith("\"centreY\"")) {
                y = Double.parseDouble(champ.split(":")[1].trim());
            } else if (champ.startsWith("\"imageBase64\"")) {
                imageBase64 = champ.split(":",2)[1].trim().replaceAll("^\"|\"$","");
            } else if (champ.startsWith("\"crops\"")) {
                // parse crops rudimentaire
                String cropsStr = champ.substring(champ.indexOf("[")+1, champ.lastIndexOf("]")).trim();
                if (!cropsStr.isEmpty()) {
                    String[] cropItems = cropsStr.split("\\},\\{");
                    for (String c : cropItems) {
                        c = c.replace("{","").replace("}","");
                        String[] props = c.split(",");
                        int cx=0, cy=0, w=0, h=0;
                        for (String p : props) {
                            p = p.trim();
                            if (p.startsWith("\"x\"")) cx = Integer.parseInt(p.split(":")[1].trim());
                            else if (p.startsWith("\"y\"")) cy = Integer.parseInt(p.split(":")[1].trim());
                            else if (p.startsWith("\"width\"")) w = Integer.parseInt(p.split(":")[1].trim());
                            else if (p.startsWith("\"height\"")) h = Integer.parseInt(p.split(":")[1].trim());
                        }
                        crops.add(new Crop(new Rectangle(cx,cy,w,h)));
                    }
                }
            } else if (champ.startsWith("\"annotations\"")) {
                String annStr = champ.substring(champ.indexOf("[")+1, champ.lastIndexOf("]")).trim();
                if (!annStr.isEmpty()) {
                    String[] annItems = annStr.split(",");
                    for (String a : annItems) {
                        annotations.add(a.trim().replaceAll("^\"|\"$","").replace("\\\"","\""));
                    }
                }
            }
        }

        return new PerspectiveMemento(idPerspective, zoom, x, y, imageBase64, crops, annotations, json);
    }
}
