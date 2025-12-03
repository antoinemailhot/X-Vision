package xvision.model;

import java.awt.Rectangle;

/**
 * Gestionnaire responsable de la zone de recadrage (Crop).
 * <p>
 * Cette classe agit comme le modèle pour l'outil de recadrage.
 * Elle stocke le rectangle de sélection actif et notifie les observateurs
 * (comme {@link xvision.view.VueAnnotations} et {@link xvision.MainGUI}) lors de tout changement.
 * </p>
 * <h3>Patrons de conception :</h3>
 * <ul>
 *   <li><b>Observer (GoF)</b> : Hérite de {@link Sujet} pour la notification des changements.</li>
 *   <li><b>Information Expert (GRASP)</b> : Centralise la logique de gestion du crop.</li>
 * </ul>
 */
public class GestionnaireCrop extends Sujet {

    private Rectangle zoneCrop;

    /**
     * Constructeur par défaut. Aucun crop n'est défini au départ.
     */
    public GestionnaireCrop() {
        this.zoneCrop = null;
    }

    /**
     * Définit ou met à jour la zone de recadrage.
     * Notifie automatiquement tous les observateurs abonnés.
     * @param rect Le nouveau rectangle de crop (ou null pour désactiver le crop).
     */
    public void definirCrop(Rectangle rect) {
        // Vérification pour éviter les notifications inutiles si le crop ne change pas
        if ((this.zoneCrop == null && rect == null) || 
            (this.zoneCrop != null && this.zoneCrop.equals(rect))) {
            return;
        }
        
        this.zoneCrop = rect;
        notifier();
        if (rect != null) {
            System.out.println("[GestionnaireCrop] Zone définie: " + rect);
        } else {
            System.out.println("[GestionnaireCrop] Crop désactivé");
        }
    }

    /**
     * Retourne la zone de crop actuelle.
     * @return Le rectangle de crop, ou null si aucun crop n'est actif.
     */
    public Rectangle getZoneCrop() {
        return zoneCrop;
    }

    /**
     * Vérifie si un crop est actuellement défini.
     * @return true si un crop existe, false sinon.
     */
    public boolean hasCrop() {
        return zoneCrop != null;
    }

    /**
     * Supprime le crop actif (équivalent à definirCrop(null)).
     */
    public void effacerCrop() {
        definirCrop(null);
    }
}
