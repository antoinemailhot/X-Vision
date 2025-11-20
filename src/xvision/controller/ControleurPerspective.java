package xvision.controller;

import xvision.model.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ArrayList;
import java.awt.Rectangle;

/**
 * Contrôleur principal pour les perspectives
 * Gère création, modification, sauvegarde et restauration
 */
public class ControleurPerspective {

    private GestionnaireDePerspectives gestionnaire;
    private GestionnaireCommandes gestionnaireCommandes;

    public ControleurPerspective(GestionnaireDePerspectives gestionnaire) {
        this.gestionnaire = gestionnaire;
        this.gestionnaireCommandes = GestionnaireCommandes.getInstance();
    }

    public PerspectiveLeaf creerPerspective(double zoom, double x, double y) {
        return gestionnaire.ajouterPerspectiveSimple(zoom, x, y);
    }

    public void zoomer(PerspectiveLeaf perspective, double nouveauZoom) {
        Commande cmd = new CommandeZoom(perspective, nouveauZoom);
        gestionnaireCommandes.executerCommande(cmd);
    }

    public void zoomerAvecFacteur(PerspectiveLeaf perspective, double facteurMultiplicatif) {
        double zoomActuel = perspective.getZoom();
        double nouveauZoom = zoomActuel * facteurMultiplicatif;
        nouveauZoom = Math.max(0.1, Math.min(nouveauZoom, 10.0));
        zoomer(perspective, nouveauZoom);
    }

    public void deplacer(PerspectiveLeaf perspective, double dx, double dy) {
        double newX = perspective.getCentreX() + dx;
        double newY = perspective.getCentreY() + dy;
        Commande cmd = new CommandeTranslation(perspective, newX, newY);
        gestionnaireCommandes.executerCommande(cmd);
    }

    public void deplacerDelta(PerspectiveLeaf perspective, double dx, double dy) {
        perspective.setCentreX(perspective.getCentreX() + dx);
        perspective.setCentreY(perspective.getCentreY() + dy);
    }

    public void annulerDerniereAction() { gestionnaireCommandes.undo(); }
    public void refaireDerniereAction() { gestionnaireCommandes.redo(); }
    public GestionnaireDePerspectives getGestionnaire() { return gestionnaire; }

    // -------------------- SAUVEGARDER / CHARGER PERSPECTIVE --------------------

    public void sauvegarderPerspectiveActuelle(String cheminFichier) {
        try {
            PerspectiveLeaf p = gestionnaire.getDernierePerspective();
            if (p == null) return;

            File dossierPerspectives = new File("perspectives/images");
            if (!dossierPerspectives.exists()) dossierPerspectives.mkdirs();

            String cheminImageOriginal = p.getCheminImage();
            String cheminImageLocal = "";
            if (cheminImageOriginal != null && !cheminImageOriginal.isEmpty()) {
                File srcImage = new File(cheminImageOriginal);
                if (srcImage.exists()) {
                    File destImage = new File(dossierPerspectives, srcImage.getName());
                    Files.copy(srcImage.toPath(), destImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    cheminImageLocal = "perspectives/images/" + srcImage.getName();
                }
            }

            if (!cheminFichier.endsWith(".json")) cheminFichier += ".json";

            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"zoom\": ").append(p.getZoom()).append(",\n");
            sb.append("  \"centreX\": ").append(p.getCentreX()).append(",\n");
            sb.append("  \"centreY\": ").append(p.getCentreY()).append(",\n");
            sb.append("  \"cheminImage\": \"").append(cheminImageLocal).append("\",\n");
            sb.append("  \"mementos\": [\n");

            List<PerspectiveMemento> mementos = p.getMementos();
            for (int i = 0; i < mementos.size(); i++) {
                PerspectiveMemento m = mementos.get(i);
                sb.append("    {\n");
                sb.append("      \"zoom\": ").append(m.getZoom()).append(",\n");
                sb.append("      \"centreX\": ").append(m.getX()).append(",\n");
                sb.append("      \"centreY\": ").append(m.getY()).append(",\n");

                if (!m.getCrops().isEmpty()) {
                    Crop c = m.getCrops().get(0); // On prend le premier crop
                    Rectangle r = c.getZone();     // <-- correction ici
                    sb.append("      \"crop\": {\n");
                    sb.append("        \"x\": ").append(r.x).append(",\n");
                    sb.append("        \"y\": ").append(r.y).append(",\n");
                    sb.append("        \"width\": ").append(r.width).append(",\n");
                    sb.append("        \"height\": ").append(r.height).append("\n");
                    sb.append("      }\n");
                } else {
                    sb.append("      \"crop\": null\n");
                }

                sb.append("    }");
                if (i < mementos.size() - 1) sb.append(",");
                sb.append("\n");
            }

            sb.append("  ]\n");
            sb.append("}\n");

            Files.writeString(new File(cheminFichier).toPath(), sb.toString());
            System.out.println("[ControleurPerspective] Perspective sauvegardée dans " + cheminFichier);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PerspectiveLeaf chargerPerspective(String cheminFichier) {
        try {
            List<String> lignes = Files.readAllLines(new File(cheminFichier).toPath());
            double zoom = 1.0, x = 0.0, y = 0.0;
            String cheminImage = "";

            StringBuilder mementosJson = new StringBuilder();
            boolean dansMementos = false;

            for (String ligne : lignes) {
                ligne = ligne.trim();
                if (ligne.startsWith("\"zoom\"") && !dansMementos) {
                    zoom = Double.parseDouble(ligne.split(":")[1].replace(",", "").trim());
                } else if (ligne.startsWith("\"centreX\"") && !dansMementos) {
                    x = Double.parseDouble(ligne.split(":")[1].replace(",", "").trim());
                } else if (ligne.startsWith("\"centreY\"") && !dansMementos) {
                    y = Double.parseDouble(ligne.split(":")[1].replace(",", "").trim());
                } else if (ligne.startsWith("\"cheminImage\"")) {
                    cheminImage = ligne.split(":")[1].replace("\"", "").replace(",", "").trim();
                } else if (ligne.startsWith("\"mementos\"")) {
                    dansMementos = true;
                    mementosJson.append(ligne.substring(ligne.indexOf("["))).append("\n");
                } else if (dansMementos) {
                    mementosJson.append(ligne).append("\n");
                    if (ligne.endsWith("]")) dansMementos = false;
                }
            }

            PerspectiveLeaf p = creerPerspective(zoom, x, y);
            if (!cheminImage.isEmpty()) p.setCheminImage(cheminImage);

            if (mementosJson.length() > 0) {
                String json = mementosJson.toString();
                json = json.substring(1, json.length() - 1).trim(); // Supprime [ ]
                String[] items = json.split("\\},");
                for (String item : items) {
                    item = item.replace("{", "").replace("}", "").trim();
                    if (item.isEmpty()) continue;

                    double mz = 1.0, mx = 0.0, my = 0.0;
                    Rectangle cropRect = null;

                    String[] champs = item.split(",");
                    for (String champ : champs) {
                        champ = champ.trim();
                        if (champ.startsWith("\"zoom\"")) mz = Double.parseDouble(champ.split(":")[1].trim());
                        else if (champ.startsWith("\"centreX\"")) mx = Double.parseDouble(champ.split(":")[1].trim());
                        else if (champ.startsWith("\"centreY\"")) my = Double.parseDouble(champ.split(":")[1].trim());
                        else if (champ.startsWith("\"x\"")) {
                            int cx = Integer.parseInt(champ.split(":")[1].trim());
                            int cy = Integer.parseInt(champs[1].split(":")[1].trim());
                            int width = Integer.parseInt(champs[2].split(":")[1].trim());
                            int height = Integer.parseInt(champs[3].split(":")[1].trim());
                            cropRect = new Rectangle(cx, cy, width, height);
                        }
                    }

                    List<Crop> crops = cropRect != null ? List.of(new Crop(cropRect)) : new ArrayList<>();
                    PerspectiveMemento m = new PerspectiveMemento(
                        p.getIdPerspective(),
                        mz,
                        mx,
                        my,
                        "",
                        crops,
                        new ArrayList<>(),
                        "{}"
                    );
                    p.ajouterMemento(m);
                }
            }

            System.out.println("[ControleurPerspective] Perspective chargée depuis " + cheminFichier);
            return p;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
