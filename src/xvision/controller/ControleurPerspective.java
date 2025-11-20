package xvision.controller;

import xvision.model.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Contrôleur principal pour les perspectives
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

    /**
     * Sauvegarde la perspective dans un fichier JSON, avec copie locale de l'image
     * et sauvegarde de l'historique de mementos.
     */
    public void sauvegarderPerspectiveActuelle(String cheminFichier) {
        try {
            PerspectiveLeaf p = gestionnaire.getDernierePerspective();
            if (p == null) return;

            // Création du dossier pour images si nécessaire
            File dossierPerspectives = new File("perspectives/images");
            if (!dossierPerspectives.exists()) dossierPerspectives.mkdirs();

            // Copier l'image localement pour rendre la perspective autonome
            String cheminImageOriginal = p.getCheminImage();
            String cheminImageLocal = null;
            if (cheminImageOriginal != null && !cheminImageOriginal.isEmpty()) {
                File srcImage = new File(cheminImageOriginal);
                if (srcImage.exists()) {
                    File destImage = new File(dossierPerspectives, srcImage.getName());
                    Files.copy(srcImage.toPath(), destImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    cheminImageLocal = "perspectives/images/" + srcImage.getName();
                }
            }

            // Construction JSON
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"zoom\": ").append(p.getZoom()).append(",\n");
            sb.append("  \"centreX\": ").append(p.getCentreX()).append(",\n");
            sb.append("  \"centreY\": ").append(p.getCentreY()).append(",\n");
            sb.append("  \"cheminImage\": \"").append(cheminImageLocal != null ? cheminImageLocal : "").append("\",\n");

            // Sauvegarde mementos
            sb.append("  \"mementos\": [\n");
            List<PerspectiveMemento> mementos = p.getMementos();
            for (int i = 0; i < mementos.size(); i++) {
                PerspectiveMemento m = mementos.get(i);
                sb.append("    {\"zoom\": ").append(m.getZoom())
                  .append(", \"centreX\": ").append(m.getX())
                  .append(", \"centreY\": ").append(m.getY()).append("}");
                if (i < mementos.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ]\n");
            sb.append("}\n");

            File f = new File(cheminFichier);
            Files.writeString(f.toPath(), sb.toString());
            System.out.println("[ControleurPerspective] Perspective sauvegardée dans " + f.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Charge une perspective depuis un fichier JSON, restaurer image et mementos
     */
    public PerspectiveLeaf chargerPerspective(String cheminFichier) {
        try {
            List<String> lignes = Files.readAllLines(new File(cheminFichier).toPath());
            double zoom = 1.0, x = 0.0, y = 0.0;
            String cheminImage = null;

            StringBuilder mementosJson = new StringBuilder();
            boolean dansMementos = false;

            for (String ligne : lignes) {
                ligne = ligne.trim();
                if (ligne.startsWith("\"zoom\"") && !dansMementos) {
                    zoom = Double.parseDouble(ligne.split(":")[1].replace(",","").trim());
                } else if (ligne.startsWith("\"centreX\"") && !dansMementos) {
                    x = Double.parseDouble(ligne.split(":")[1].replace(",","").trim());
                } else if (ligne.startsWith("\"centreY\"") && !dansMementos) {
                    y = Double.parseDouble(ligne.split(":")[1].replace(",","").trim());
                } else if (ligne.startsWith("\"cheminImage\"")) {
                    cheminImage = ligne.split(":")[1].replace("\"","").replace(",","").trim();
                } else if (ligne.startsWith("\"mementos\"")) {
                    dansMementos = true;
                    mementosJson.append("[");
                } else if (dansMementos) {
                    mementosJson.append(ligne);
                    if (ligne.endsWith("]")) dansMementos = false;
                }
            }

            PerspectiveLeaf p = creerPerspective(zoom, x, y);
            p.setCheminImage(cheminImage);

            // Restaurer mementos
            if (mementosJson.length() > 0) {
                String[] mementoItems = mementosJson.toString().replace("[","").replace("]","").split("\\},");
                for (String item : mementoItems) {
                    item = item.replace("{","").replace("}","").trim();
                    if (item.isEmpty()) continue;
                    double mz = 1.0, mx = 0.0, my = 0.0;
                    String[] champs = item.split(",");
                    for (String champ : champs) {
                        champ = champ.trim();
                        if (champ.startsWith("\"zoom\"")) mz = Double.parseDouble(champ.split(":")[1].trim());
                        else if (champ.startsWith("\"centreX\"")) mx = Double.parseDouble(champ.split(":")[1].trim());
                        else if (champ.startsWith("\"centreY\"")) my = Double.parseDouble(champ.split(":")[1].trim());
                    }
                    p.ajouterMemento(new PerspectiveMemento(
                        p.getIdPerspective(), // id
                        mz,                  // zoom
                        mx,                  // x
                        my,                  // y
                        "",                  // imageBase64
                        List.of(),           // crops vides
                        List.of(),           // annotations vides
                        "{}"                 // etatJSON vide
                    ));
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
