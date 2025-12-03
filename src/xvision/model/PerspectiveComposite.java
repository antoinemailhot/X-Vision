package xvision.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation d'un nœud composite dans l'arbre des perspectives.
 * <p>
 * Cette classe permet de regrouper plusieurs perspectives (feuilles ou autres composites)
 * et de les manipuler comme un seul objet. C'est l'élément "Branche" du patron <b>Composite</b>.
 * </p>
 * <p>
 * Lors de l'exportation JSON, le composite délègue récursivement l'exportation à ses enfants,
 * construisant ainsi la structure hiérarchique complète.
 * </p>
 */
public class PerspectiveComposite implements PerspectiveComponent {
    
    private List<PerspectiveComponent> enfants = new ArrayList<>();

    @Override
    public void afficher() {
        System.out.println("Composite: " + enfants.size() + " enfants");
        for (PerspectiveComponent enfant : enfants) {
            enfant.afficher();
        }
    }

    @Override
    public void ajouter(PerspectiveComponent p) {
        enfants.add(p);
    }

    @Override
    public void supprimer(PerspectiveComponent p) {
        enfants.remove(p);
    }

    @Override
    public List<PerspectiveComponent> getEnfants() {
        return enfants;
    }

    /**
     * Exporte la structure composite en JSON.
     * Format : { "type": "composite", "enfants": [ ... ] }
     */
    @Override
    public String exporterEnJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"composite\",");
        sb.append("\"enfants\":[");
        for (int i = 0; i < enfants.size(); i++) {
            sb.append(enfants.get(i).exporterEnJSON());
            if (i < enfants.size() - 1) sb.append(",");
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }
}
