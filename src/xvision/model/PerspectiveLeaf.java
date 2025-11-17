package xvision.model;

import java.util.List;

/**
 * Perspective individuelle (feuille) dans l'arbre Composite.
 * Gère son état interne et son propre historique Memento.
 *
 * Principe GRASP: Information Expert — la feuille gère elle-même
 * son zoom, sa position et ses sauvegardes internes.
 */
public class PerspectiveLeaf extends Sujet implements PerspectiveComponent {

    // Identifiant unique pour cette perspective
    private final int idPerspective;

    // Historique Memento indépendant pour chaque Leaf
    private GestionnaireMemento gestionnaireMemento = new GestionnaireMemento();

    private double zoom;
    private double x;
    private double y;
    private String cheminImage;

    /**
     * Constructeur d'une perspective feuille.
     *
     * @param zoom Niveau de zoom initial
     * @param x Position X du centre
     * @param y Position Y du centre
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
     * Retourne l'identifiant unique de cette perspective.
     */
    public int getIdPerspective() {
        return idPerspective;
    }

    /**
     * Crée un Memento de l'état courant.
     */
    public PerspectiveMemento creerMemento() {
        return new PerspectiveMemento(idPerspective, zoom, x, y);
    }

    /**
     * Restaure l'état depuis un Memento.
     */
    public void restaurerDepuisMemento(PerspectiveMemento m) {
        this.zoom = m.getZoom();
        this.x = m.getX();
        this.y = m.getY();
        notifier(); // Mise à jour des vues (Observer)
    }

    /**
     * Annule la dernière modification (Undo).
     */
    public void undo() {
        PerspectiveMemento m = gestionnaireMemento.undo();
        if (m != null) restaurerDepuisMemento(m);
    }

    /**
     * Réapplique la modification annulée (Redo).
     */
    public void redo() {
        PerspectiveMemento m = gestionnaireMemento.redo();
        if (m != null) restaurerDepuisMemento(m);
    }

    // ----------- Getters / Setters avec sauvegarde automatique -------------

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
        gestionnaireMemento.sauvegarder(creerMemento()); // Auto-save
        notifier();
    }

    public double getCentreX() {
        return x;
    }

    public double getCentreY() {
        return y;
    }

    public void setCentre(double x, double y) {
        gestionnaireMemento.sauvegarder(creerMemento()); // Auto-save
        this.x = x;
        this.y = y;
        notifier();
    }

    public String getCheminImage() {
        return cheminImage;
    }

    public void setCheminImage(String cheminImage) {
        gestionnaireMemento.sauvegarder(creerMemento()); // Auto-save
        this.cheminImage = cheminImage;
        notifier();
    }

    @Override
    public void afficher() {
        System.out.println("Leaf [" + idPerspective + "] - Zoom: " + zoom +
                ", Pos=(" + x + "," + y + ")");
    }

    @Override
    public void ajouter(PerspectiveComponent p) {
        System.out.println("Impossible d’ajouter à une feuille.");
    }

    @Override
    public void supprimer(PerspectiveComponent p) {
        // Rien à faire pour une leaf
    }

    @Override
    public List<PerspectiveComponent> getEnfants() {
        return List.of();
    }

    public GestionnaireMemento getMementoManager() {
        return gestionnaireMemento;
    }
}