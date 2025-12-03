package xvision.model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Memento représentant l'état complet d'une PerspectiveLeaf à un instant T.
 * <p>
 * Cette classe est immuable et stocke :
 * <ul>
 *   <li>L'état géométrique (zoom, position).</li>
 *   <li>L'image (encodée en Base64).</li>
 *   <li>Les annotations et crops associés.</li>
 * </ul>
 * </p>
 * <h3>Optimisation "Deep State" :</h3>
 * Pour éviter l'explosion mémoire lors de la sauvegarde de l'historique, cette classe supporte
 * le token spécial "SAME" pour l'image Base64. Si l'image n'a pas changé par rapport à l'état précédent,
 * elle n'est pas dupliquée dans le JSON.
 */
public class PerspectiveMemento {

    private final int idPerspective;
    private final double zoom;
    private final double x;
    private final double y;
    private final String imageBase64;
    private final List<Crop> crops;
    private final List<String> annotations;

    /**
     * Constructeur du Memento.
     * @param idPerspective Identifiant de la perspective source.
     * @param zoom Niveau de zoom.
     * @param x Position X du centre.
     * @param y Position Y du centre.
     * @param imageBase64 Chaîne Base64 de l'image (ou "SAME").
     * @param crops Liste des zones de recadrage.
     * @param annotations Liste des annotations sérialisées.
     */
    public PerspectiveMemento(int idPerspective,
                              double zoom,
                              double x,
                              double y,
                              String imageBase64,
                              List<Crop> crops,
                              List<String> annotations) {
        this.idPerspective = idPerspective;
        this.zoom = zoom;
        this.x = x;
        this.y = y;
        this.imageBase64 = imageBase64;
        this.crops = crops != null ? new ArrayList<>(crops) : new ArrayList<>();
        this.annotations = annotations != null ? new ArrayList<>(annotations) : new ArrayList<>();
    }

    // ================== Getters ==================
    public int getIdPerspective() { return idPerspective; }
    public double getZoom() { return zoom; }
    public double getX() { return x; }
    public double getY() { return y; }
    public String getImageBase64() { return imageBase64; }
    public List<Crop> getCrops() { return new ArrayList<>(crops); }
    public List<String> getAnnotations() { return new ArrayList<>(annotations); }

    /**
     * Convertit ce memento en JSON complet (chaîne) pour sauvegarde.
     * @param referenceImageBase64 L'image de référence pour optimiser le stockage (si identique, on stocke "SAME")
     * @return La chaîne JSON représentant ce memento.
     */
    public String exporterEnJSON(String referenceImageBase64) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"zoom\":").append(zoom).append(",");
        sb.append("\"centreX\":").append(x).append(",");
        sb.append("\"centreY\":").append(y).append(",");
        
        // Optimisation : si l'image est identique à la référence, on met "SAME"
        if (imageBase64 != null && imageBase64.equals(referenceImageBase64)) {
            sb.append("\"imageBase64\":\"SAME\",");
        } else {
            sb.append("\"imageBase64\":\"").append(imageBase64 != null ? imageBase64 : "").append("\",");
        }

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
     * Crée un memento à partir d'une chaîne JSON (simplifiée) et idPerspective.
     * Utilise un parser manuel robuste pour éviter les erreurs sur les grosses chaînes Base64.
     * 
     * @param json La chaîne JSON brute d'un objet memento.
     * @param idPerspective L'ID de la perspective associée.
     * @param referenceImageBase64 L'image de référence à utiliser si le JSON contient "SAME".
     * @return Le nouvel objet PerspectiveMemento.
     */
    public static PerspectiveMemento fromJSON(String json, int idPerspective, String referenceImageBase64) {
        double zoom = 1.0, x = 0.0, y = 0.0;
        String imageBase64 = "";
        List<Crop> crops = new ArrayList<>();
        List<String> annotations = new ArrayList<>();

        json = json.trim();
        // Nettoyage des accolades extérieures
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        // Extraction via regex pour éviter les problèmes de split sur les virgules internes
        zoom = parseDouble(extraireValeur(json, "zoom"), 1.0);
        x = parseDouble(extraireValeur(json, "centreX"), 0.0);
        y = parseDouble(extraireValeur(json, "centreY"), 0.0);
        imageBase64 = extraireValeur(json, "imageBase64");
        
        // Restauration de l'image optimisée
        if ("SAME".equals(imageBase64)) {
            imageBase64 = referenceImageBase64;
        }

        // Parsing des crops (Tableau d'objets)
        String cropsJson = extraireTableau(json, "crops");
        if (!cropsJson.isEmpty()) {
            List<String> cropItems = decouperObjets(cropsJson);
            for (String c : cropItems) {
                double cx = parseDouble(extraireValeur(c, "x"), 0);
                double cy = parseDouble(extraireValeur(c, "y"), 0);
                double cw = parseDouble(extraireValeur(c, "width"), 0);
                double ch = parseDouble(extraireValeur(c, "height"), 0);
                crops.add(new Crop(new Rectangle((int)cx, (int)cy, (int)cw, (int)ch)));
            }
        }

        // Parsing des annotations (Tableau de strings)
        String annJson = extraireTableau(json, "annotations");
        if (!annJson.isEmpty()) {
            // Retire les crochets
            if (annJson.startsWith("[")) annJson = annJson.substring(1);
            if (annJson.endsWith("]")) annJson = annJson.substring(0, annJson.length() - 1);
            
            if (!annJson.trim().isEmpty()) {
                String[] items = annJson.split("\",\"");
                for (String item : items) {
                    annotations.add(item.replace("\"", "").replace("\\\"", "\""));
                }
            }
        }

        return new PerspectiveMemento(idPerspective, zoom, x, y, imageBase64, crops, annotations);
    }

    private static String extraireValeur(String json, String key) {
        // Cherche "key":valeur ou "key":"valeur"
        // Attention: cette regex est simplifiée et suppose que la clé est unique et bien formée
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start == -1) return "";
        start += pattern.length();
        
        char firstChar = json.charAt(start);
        if (firstChar == '"') {
            // C'est une chaîne
            int end = json.indexOf("\"", start + 1);
            // Gérer les quotes échappées serait mieux, mais ici on fait simple
            while (end != -1 && json.charAt(end - 1) == '\\') {
                end = json.indexOf("\"", end + 1);
            }
            if (end == -1) return "";
            return json.substring(start + 1, end);
        } else {
            // C'est un nombre ou un booléen (jusqu'à la virgule ou fin)
            int end = json.indexOf(",", start);
            if (end == -1) end = json.length(); // Cas du dernier élément
            // Attention si c'est la fin de l'objet parent '}'
            int endBrace = json.indexOf("}", start);
            if (endBrace != -1 && endBrace < end) end = endBrace;
            
            return json.substring(start, end).trim();
        }
    }

    private static String extraireTableau(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start == -1) return "";
        start += pattern.length();
        
        int bracketStart = json.indexOf("[", start);
        if (bracketStart == -1) return "";
        
        int count = 0;
        for (int i = bracketStart; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') count++;
            else if (c == ']') {
                count--;
                if (count == 0) {
                    return json.substring(bracketStart, i + 1);
                }
            }
        }
        return "";
    }

    private static List<String> decouperObjets(String jsonArray) {
        List<String> result = new ArrayList<>();
        // Retire les crochets extérieurs
        if (jsonArray.startsWith("[")) jsonArray = jsonArray.substring(1);
        if (jsonArray.endsWith("]")) jsonArray = jsonArray.substring(0, jsonArray.length() - 1);
        
        int braceCount = 0;
        int start = -1;
        for (int i = 0; i < jsonArray.length(); i++) {
            char c = jsonArray.charAt(i);
            if (c == '{') {
                if (braceCount == 0) start = i;
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0 && start != -1) {
                    result.add(jsonArray.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return result;
    }

    private static double parseDouble(String val, double def) {
        try { return Double.parseDouble(val); } catch (Exception e) { return def; }
    }
}
