package xvision.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire central des annotations
 * Patron Observer (GoF): Sujet - notifie les vues quand annotations changent
 * Principe GRASP: Information Expert - gère la collection d'annotations
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class GestionnaireAnnotations extends Sujet {

    private List<Annotation> annotations;

    /**
     * Constructeur
     */
    public GestionnaireAnnotations() {
        this.annotations = new ArrayList<>();
    }

    /**
     * Ajoute une annotation
     * @param annotation L'annotation à ajouter
     */
    public void ajouterAnnotation(Annotation annotation) {
        if (annotation != null) {
            annotations.add(annotation);
            notifier();
            System.out.println("[GestionnaireAnnotations] Annotation ajoutée, total: " + annotations.size());
        }
    }

    /**
     * Supprime une annotation
     * @param annotation L'annotation à supprimer
     */
    public void supprimerAnnotation(Annotation annotation) {
        if (annotations.remove(annotation)) {
            notifier();
            System.out.println("[GestionnaireAnnotations] Annotation supprimée, total: " + annotations.size());
        }
    }

    /**
     * Supprime toutes les annotations
     */
    public void effacerTout() {
        annotations.clear();
        notifier();
        System.out.println("[GestionnaireAnnotations] Toutes les annotations effacées");
    }

    /**
     * Retourne la liste des annotations (copie défensive)
     * @return Liste des annotations
     */
    public List<Annotation> getAnnotations() {
        return new ArrayList<>(annotations);
    }

    /**
     * Retourne le nombre d'annotations
     * @return Nombre d'annotations
     */
    public int getNombreAnnotations() {
        return annotations.size();
    }
}
