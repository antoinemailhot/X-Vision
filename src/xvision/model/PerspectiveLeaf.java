package xvision.model;

import java.util.List;

/**
 * Perspective individuelle (feuille) dans l'arbre Composite.
 * Gère son état interne et son propre historique Memento.
 *
 * Principes respectés :
 * - GRASP (Information Expert) : la feuille connaît son état.
 * - Memento : chaque modification crée un snapshot restaurable.
 * - Observer : notifie les vues lors de tout changement.
 */
public class PerspectiveLeaf extends Sujet implements PerspectiveComponent {

    // Identifiant unique pour cette perspective
    private final int idPerspective;

    // Historique Memento propre à cette perspective
    private final GestionnaireMemento gestionnaireMemento = new GestionnaireMemento();

    // État interne
    private double zoom;
    private double x;
    private double y;
    private String cheminImage;

    /**
     * Constructeur
     * @param zoom niveau de zoom initial
     * @param x    position X du centre
     * @param y    position Y du centre
     */
    public PerspectiveLeaf(double zoom, double x, double y) {
        super();
        this.idPerspective = Identifiant.generer();
        this.zoom = zoom;
        this.x = x;
        this.y = y;

        // Sauvegarde initiale de l'état
        gestionnaireMemento.sauvegarder(creerMemento());
    }

    /**
     * Identifiant unique de la perspective.
     */
    public int getIdPerspective() {
        return idPerspective;
    }

    /**
     * Création d'un Memento sauvegardant l'état actuel.
     */
    public PerspectiveMemento creerMemento() {
        return new PerspectiveMemento(idPerspective, zoom, x, y);
    }

    /**
     * Restaure l'état depuis un Memento (Undo/Redo).
     */
    public void restaurerDepuisMemento(PerspectiveMemento m) {
        this.zoom = m.getZoom();
        this.x = m.getX();
        this.y = m.getY();
        notifier();
    }

    /**
     * Undo : restaure l'état précédent.
     */
    public void undo() {
        PerspectiveMemento m = gestionnaireMemento.undo();
        if (m != null) restaurerDepuisMemento(m);
    }

    /**
     * Redo : réapplique l'action annulée.
     */
    public void redo() {
        PerspectiveMemento m = gestionnaireMemento.redo();
        if (m != null) restaurerDepuisMemento(m);
    }

    // ================== GETTERS ==================

    public double getZoom() { return zoom; }
    public double getCentreX() { return x; }
    public double getCentreY() { return y; }
    public String getCheminImage() { return cheminImage; }

    // ================== SETTERS OPTIMISÉS ==================

    /**
     * Modifie le zoom avec sauvegarde automatique (Memento).
     */
    public void setZoom(double zoom) {
        gestionnaireMemento.sauvegarder(creerMemento());
        this.zoom = zoom;
        notifier();
    }

    /**
     * Définit la position complète du centre.
     * Utilisé comme méthode principale pour la cohérence.
     */
    public void setCentre(double x, double y) {
        gestionnaireMemento.sauvegarder(creerMemento());
        this.x = x;
        this.y = y;
        notifier();
    }

    /**
     * Modifie seulement X avec cohérence (passe par setCentre).
     */
    public void setCentreX(double x) {
        setCentre(x, this.y);
    }

    /**
     * Modifie seulement Y avec cohérence (passe par setCentre).
     */
    public void setCentreY(double y) {
        setCentre(this.x, y);
    }

    /**
     * Définit le chemin vers l'image associée.
     */
    public void setCheminImage(String cheminImage) {
        gestionnaireMemento.sauvegarder(creerMemento());
        this.cheminImage = cheminImage;
        notifier();
    }

    // ================== MÉTHODES COMPOSITE ==================

    @Override
    public void afficher() {
        System.out.println("Leaf [" + idPerspective + "] - Zoom: " + zoom +
                ", Pos=(" + x + "," + y + ")");
    }

    @Override
    public void ajouter(PerspectiveComponent p) {
        System.out.println("Impossible d’ajouter un enfant à une feuille.");
    }

    @Override
    public void supprimer(PerspectiveComponent p) {
        // Aucun enfant dans une feuille
    }

    @Override
    public List<PerspectiveComponent> getEnfants() {
        return List.of(); // Feuille = pas d'enfants
    }

    /**
     * Accès au gestionnaire de Memento pour les opérations externes si nécessaire.
     */
    public GestionnaireMemento getMementoManager() {
        return gestionnaireMemento;
    }
}
