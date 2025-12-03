package xvision.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.awt.Rectangle;
import java.util.*;

/**
 * Gestionnaire responsable de la persistance des perspectives.
 * Gère la sauvegarde et le chargement au format JSON personnalisé.
 * 
 * <p>
 * Cette classe contient un parseur JSON manuel (récursif) pour éviter
 * l'utilisation de bibliothèques externes lourdes (Jackson/Gson).
 * Elle gère aussi la reconstruction de l'arbre Composite et des piles Undo/Redo.
 * </p>
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
     * Sauvegarde une perspective (ou un composite) dans un fichier JSON complet.
     */
    public void sauvegarderPerspective(String id, String dossier) {
        PerspectiveComponent p = perspectives.get(id);
        if (p == null) return;
        sauvegarderPerspective(p, Path.of(dossier, id + ".json").toString());
    }

    /**
     * Sauvegarde une perspective directement dans un fichier spécifique.
     */
    public void sauvegarderPerspective(PerspectiveComponent p, String cheminFichier) {
        if (p == null) return;
        try {
            Path path = Path.of(cheminFichier);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            String json = p.exporterEnJSON();
            Files.writeString(path, json);
            System.out.println("[GestionnaireDePerspectives] Perspective sauvegardée vers " + cheminFichier);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Charge une perspective (Leaf ou Composite) depuis un fichier JSON.
     */
    public void chargerPerspective(String cheminFichier) {
        try {
            String json = Files.readString(Path.of(cheminFichier));
            PerspectiveComponent p = parserJSON(json);
            if (p != null) {
                // On utilise le nom du fichier comme ID pour simplifier
                String nomFichier = Path.of(cheminFichier).getFileName().toString().replace(".json", "");
                perspectives.put(nomFichier, p);
                System.out.println("[GestionnaireDePerspectives] Perspective " + nomFichier + " chargée.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parseur récursif pour reconstruire l'arbre Composite/Leaf depuis le JSON.
     * C'est le cœur du moteur de chargement.
     * 
     * @param json La chaîne JSON brute.
     * @return Un composant (Leaf ou Composite) reconstruit.
     */
    private PerspectiveComponent parserJSON(String json) {
        json = json.trim();
        String type = extraireValeurJSON(json, "type");

        if ("composite".equals(type)) {
            PerspectiveComposite composite = new PerspectiveComposite();
            List<String> enfantsJson = decouperTableauObjets(json, "enfants");
            for (String enfantStr : enfantsJson) {
                PerspectiveComponent enfant = parserJSON(enfantStr);
                if (enfant != null) composite.ajouter(enfant);
            }
            return composite;

        } else if ("leaf".equals(type) || type == null) { 
            // Cas d'une feuille (PerspectiveLeaf)
            String idStr = extraireValeurJSON(json, "idPerspective");
            // Si pas d'ID, on ne peut pas faire grand chose, mais on continue si possible
            
            // Extraction des primitives
            String zoomStr = extraireValeurJSON(json, "zoom");
            String xStr = extraireValeurJSON(json, "x");
            String yStr = extraireValeurJSON(json, "y");
            
            double zoom = (zoomStr != null) ? Double.parseDouble(zoomStr) : 1.0;
            double x = (xStr != null) ? Double.parseDouble(xStr) : 0.0;
            double y = (yStr != null) ? Double.parseDouble(yStr) : 0.0;
            
            String base64Image = extraireValeurJSON(json, "imageBase64");

            PerspectiveLeaf leaf = new PerspectiveLeaf(zoom, x, y);

            // Restauration de l'image depuis le Base64
            if (base64Image != null && !base64Image.isEmpty()) {
                // On utilise un chemin absolu pour garantir l'écriture correcte
                String cheminAbsolu = Path.of("restored_" + (idStr != null ? idStr : "temp") + ".png").toAbsolutePath().toString();
                leaf.restaurerImageDepuisBase64(base64Image, cheminAbsolu);
            }

            // Restauration du Crop
            String cropStr = extraireValeurJSON(json, "crop");
            if (cropStr != null && !cropStr.equals("null")) {
                double cx = Double.parseDouble(extraireValeurJSON(cropStr, "x"));
                double cy = Double.parseDouble(extraireValeurJSON(cropStr, "y"));
                double cw = Double.parseDouble(extraireValeurJSON(cropStr, "width"));
                double ch = Double.parseDouble(extraireValeurJSON(cropStr, "height"));
                leaf.setCropSansSauvegarde(new Rectangle((int)cx, (int)cy, (int)cw, (int)ch));
            } else {
                leaf.setCropSansSauvegarde(null);
            }

            // Restauration de l'historique complet (Deep State)
            int id = (idStr != null) ? Integer.parseInt(idStr) : leaf.getIdPerspective();
            Stack<PerspectiveMemento> undoStack = parserPileMementos(json, "undoStack", id, base64Image);
            Stack<PerspectiveMemento> redoStack = parserPileMementos(json, "redoStack", id, base64Image);
            
            leaf.getMementoManager().restaurerHistorique(undoStack, redoStack);

            return leaf;
        }
        return null;
    }

    private Stack<PerspectiveMemento> parserPileMementos(String json, String keyStack, int idPerspective, String referenceImage) {
        Stack<PerspectiveMemento> stack = new Stack<>();
        List<String> mementosJson = decouperTableauObjets(json, keyStack);
        for (String mStr : mementosJson) {
            stack.push(PerspectiveMemento.fromJSON(mStr, idPerspective, referenceImage));
        }
        return stack;
    }

    /**
     * Découpe un tableau JSON d'objets [...] en une liste de chaînes JSON individuelles { ... }
     * Gère l'imbrication des accolades.
     */
    private List<String> decouperTableauObjets(String json, String key) {
        List<String> result = new ArrayList<>();
        int indexKey = json.indexOf("\"" + key + "\"");
        if (indexKey == -1) return result;

        int indexStartArray = json.indexOf("[", indexKey);
        if (indexStartArray == -1) return result;

        int accoladeCount = 0;
        int startObj = -1;
        boolean inString = false;

        // On parcourt à partir du crochet ouvrant
        for (int i = indexStartArray + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (c == '"' && json.charAt(i-1) != '\\') {
                inString = !inString;
            }
            if (inString) continue;

            if (c == '{') {
                if (accoladeCount == 0) startObj = i;
                accoladeCount++;
            } else if (c == '}') {
                accoladeCount--;
                if (accoladeCount == 0 && startObj != -1) {
                    result.add(json.substring(startObj, i + 1));
                    startObj = -1;
                }
            } else if (c == ']' && accoladeCount == 0) {
                break; // Fin du tableau
            }
        }
        return result;
    }

    /**
     * Méthode robuste pour extraire une valeur JSON d'une clé.
     */
    private String extraireValeurJSON(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start == -1) return null;
        start += pattern.length();
        
        // Sauter les espaces
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }
        
        if (start >= json.length()) return null;
        
        char firstChar = json.charAt(start);
        if (firstChar == '"') {
            // C'est une chaîne
            int end = json.indexOf("\"", start + 1);
            // Gérer les quotes échappées
            while (end != -1 && json.charAt(end - 1) == '\\') {
                end = json.indexOf("\"", end + 1);
            }
            if (end == -1) return null;
            return json.substring(start + 1, end);
        } else if (firstChar == '{') {
            // C'est un objet
            int braceCount = 0;
            for (int i = start; i < json.length(); i++) {
                char c = json.charAt(i);
                if (c == '{') braceCount++;
                else if (c == '}') {
                    braceCount--;
                    if (braceCount == 0) return json.substring(start, i + 1);
                }
            }
            return null;
        } else {
            // C'est un nombre, booléen ou null
            int end = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}' && json.charAt(end) != ']') {
                end++;
            }
            return json.substring(start, end).trim();
        }
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
