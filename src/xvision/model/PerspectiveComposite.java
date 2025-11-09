package xvision.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une perspective composite,
 * pouvant contenir plusieurs perspectives enfants.
 */
public class PerspectiveComposite implements PerspectiveComponent {
    private List<PerspectiveComponent> enfants = new ArrayList<>();

    @Override
    public void afficher() {
        System.out.println("Perspective composite contenant " + enfants.size() + " enfants :");
        for (PerspectiveComponent enfant : enfants) {
            enfant.afficher(); // Appel récursif
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
}
