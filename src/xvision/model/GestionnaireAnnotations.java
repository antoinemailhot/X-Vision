package xvision.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire central des annotations.
 * <p>
 * Cette classe agit comme le modèle pour les annotations vectorielles.
 * Elle maintient une liste d'objets {@link Annotation} et notifie les observateurs
 * (comme {@link xvision.view.VueAnnotations} et {@link xvision.MainGUI}) lors de tout changement.
 * </p>
 * <h3>Patrons de conception :</h3>
 * <ul>
 *   <li><b>Observer (GoF)</b> : Hérite de {@link Sujet} pour la notification des changements.</li>
 *   <li><b>Information Expert (GRASP)</b> : C'est l'expert de l'information pour tout ce qui concerne les annotations.</li>
 * </ul>
 */
public class GestionnaireAnnotations extends Sujet {

    private List<Annotation> annotations;

    /**
     * Constructeur par défaut. Initialise une liste vide.
     */
    public GestionnaireAnnotations() {
        this.annotations = new ArrayList<>();
    }

    /**
     * Ajoute une nouvelle annotation à la collection et notifie les observateurs.
     * @param annotation L'annotation à ajouter (ne doit pas être null).
     */
    public void ajouterAnnotation(Annotation annotation) {
        if (annotation != null) {
            annotations.add(annotation);
            notifier();
            System.out.println("[GestionnaireAnnotations] Annotation ajoutée, total: " + annotations.size());
        }
    }

    /**
     * Supprime une annotation spécifique de la collection et notifie les observateurs.
     * @param annotation L'annotation à supprimer.
     */
    public void supprimerAnnotation(Annotation annotation) {
        if (annotations.remove(annotation)) {
            notifier();
            System.out.println("[GestionnaireAnnotations] Annotation supprimée, total: " + annotations.size());
        }
    }

    /**
     * Supprime toutes les annotations de la collection (Reset).
     * Notifie les observateurs.
     */
    public void effacerTout() {
        annotations.clear();
        notifier();
        System.out.println("[GestionnaireAnnotations] Toutes les annotations effacées");
    }

    /**
     * Retourne une copie défensive de la liste des annotations.
     * Cela empêche la modification directe de la liste interne par les appelants.
     * @return Une nouvelle liste contenant toutes les annotations.
     */
    public List<Annotation> getAnnotations() {
        return new ArrayList<>(annotations);
    }

    /**
     * Retourne le nombre total d'annotations gérées.
     * @return Le nombre d'annotations.
     */
    public int getNombreAnnotations() {
        return annotations.size();
    }
}
