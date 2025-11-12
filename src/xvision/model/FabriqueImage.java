package xvision.model;

/**
 * Fabrique d'images utilisant le patron Factory Method (GoF)
 *
 * Cette classe combine deux patrons de conception:
 * 1. Factory Method : Création d'objets sans spécifier la classe concrète
 * 2. Adapter : Les objets créés sont des adaptateurs qui unifient différents formats
 *
 * Responsabilité: Créer l'adaptateur approprié selon le type de fichier image
 *
 * Principe GRASP: Creator - Cette classe crée les adaptateurs car elle possède
 * la logique pour déterminer quel type d'adaptateur créer.
 *
 * Principe GRASP: Information Expert - Cette classe sait comment déterminer
 * le format d'une image à partir de son extension.
 *
 * Avantages de cette approche:
 * - Open/Closed: Facile d'ajouter de nouveaux formats sans modifier le code client
 * - Low Coupling: Les clients n'ont besoin de connaître que ImageTarget
 * - Centralisation: Toute la logique de création est ici
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 1.0
 */
public class FabriqueImage {

    /**
     * Types de formats supportés
     * Énumération pour éviter les "magic strings"
     */
    public enum FormatImage {
        PNG,
        JPEG,
        JPG,  // Alias pour JPEG
        SVG,
        INCONNU
    }

    /**
     * Crée un adaptateur d'image approprié selon l'extension du fichier
     *
     * Pattern: Factory Method
     * Cette méthode est la "factory method" qui décide quel adaptateur créer
     *
     * @param cheminFichier Le chemin vers le fichier image
     * @return Un ImageTarget (adaptateur) approprié pour le format
     * @throws IllegalArgumentException si le format n'est pas supporté
     */
    public static ImageTarget creerImage(String cheminFichier) {
        if (cheminFichier == null || cheminFichier.isEmpty()) {
            throw new IllegalArgumentException("[FabriqueImage] Chemin de fichier invalide");
        }

        FormatImage format = detecterFormat(cheminFichier);
        System.out.println("[FabriqueImage] Format détecté: " + format);

        return creerAdaptateur(format, cheminFichier);
    }

    /**
     * Crée un adaptateur d'image selon le format spécifié
     *
     * @param format Le format d'image souhaité
     * @return Un ImageTarget (adaptateur) approprié
     * @throws IllegalArgumentException si le format n'est pas supporté
     */
    public static ImageTarget creerImage(FormatImage format) {
        return creerAdaptateur(format, null);
    }

    /**
     * Détecte le format d'une image à partir de son extension
     *
     * Principe GRASP: Information Expert - Cette méthode extrait et analyse
     * l'extension du fichier
     *
     * @param cheminFichier Le chemin vers le fichier
     * @return Le format détecté
     */
    private static FormatImage detecterFormat(String cheminFichier) {
        String extension = extraireExtension(cheminFichier);

        switch (extension.toUpperCase()) {
            case "PNG":
                return FormatImage.PNG;
            case "JPG":
            case "JPEG":
                return FormatImage.JPEG;
            case "SVG":
                return FormatImage.SVG;
            default:
                return FormatImage.INCONNU;
        }
    }

    /**
     * Extrait l'extension d'un nom de fichier
     *
     * @param cheminFichier Le chemin vers le fichier
     * @return L'extension (sans le point)
     */
    private static String extraireExtension(String cheminFichier) {
        int dernierPoint = cheminFichier.lastIndexOf('.');
        if (dernierPoint == -1 || dernierPoint == cheminFichier.length() - 1) {
            return "";
        }
        return cheminFichier.substring(dernierPoint + 1);
    }

    /**
     * Crée l'adaptateur approprié selon le format
     *
     * Pattern: Factory Method - Cœur de la fabrique
     * Principe GRASP: Creator - Crée les adaptateurs
     *
     * @param format Le format d'image
     * @param cheminFichier Le chemin (optionnel, peut être null)
     * @return L'adaptateur créé
     * @throws IllegalArgumentException si le format n'est pas supporté
     */
    private static ImageTarget creerAdaptateur(FormatImage format, String cheminFichier) {
        ImageTarget adaptateur = null;

        switch (format) {
            case PNG:
                System.out.println("[FabriqueImage] Création d'un AdapterPNG");
                adaptateur = new AdapterPNG();
                break;

            case JPEG:
            case JPG:
                System.out.println("[FabriqueImage] Création d'un AdapterJPEG");
                adaptateur = new AdapterJPEG();
                break;

            case SVG:
                System.out.println("[FabriqueImage] Création d'un AdapterSVG");
                adaptateur = new AdapterSVG();
                break;

            case INCONNU:
            default:
                String message = "[FabriqueImage] Format non supporté: " + format;
                if (cheminFichier != null) {
                    message += " (fichier: " + cheminFichier + ")";
                }
                throw new IllegalArgumentException(message);
        }

        // Si un chemin est fourni, charger l'image immédiatement
        if (cheminFichier != null && adaptateur != null) {
            System.out.println("[FabriqueImage] Chargement de l'image: " + cheminFichier);
            adaptateur.charger(cheminFichier);
        }

        return adaptateur;
    }

    /**
     * Vérifie si un format est supporté
     *
     * @param extension L'extension du fichier (avec ou sans point)
     * @return true si le format est supporté
     */
    public static boolean estFormatSupporte(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }

        // Enlever le point si présent
        if (extension.startsWith(".")) {
            extension = extension.substring(1);
        }

        FormatImage format = detecterFormat("fichier." + extension);
        return format != FormatImage.INCONNU;
    }

    /**
     * Retourne la liste des formats supportés
     *
     * @return Un tableau des extensions supportées
     */
    public static String[] getFormatsSupportes() {
        return new String[]{"png", "jpg", "jpeg", "svg"};
    }

    /**
     * Affiche les formats supportés (utile pour le débogage)
     */
    public static void afficherFormatsSupportes() {
        System.out.println("[FabriqueImage] Formats d'images supportés:");
        for (String format : getFormatsSupportes()) {
            System.out.println("  - " + format.toUpperCase());
        }
    }
}
