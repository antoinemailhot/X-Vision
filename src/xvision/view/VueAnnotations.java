package xvision.view;

import javax.swing.*;
import xvision.model.GestionnaireAnnotations;
import xvision.model.GestionnaireCrop;
import xvision.model.Annotation;
import xvision.model.PerspectiveLeaf;
import xvision.controller.GestionnaireOutils;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * Vue secondaire affichant un aperçu du résultat final (Image complète ou Zone recadrée).
 * <p>
 * Cette vue implémente le patron <b>Observer</b> pour réagir aux changements de :
 * <ul>
 *   <li>La perspective active (changement d'image).</li>
 *   <li>Les annotations (ajout/suppression de lignes).</li>
 *   <li>Le crop (définition/modification de la zone de découpe).</li>
 * </ul>
 * </p>
 * <p>
 * Elle gère le redimensionnement intelligent (mise à l'échelle) pour afficher l'aperçu
 * dans un cadre fixe, tout en conservant le ratio d'aspect.
 * </p>
 */
public class VueAnnotations extends JPanel implements ObserverIF {

    private GestionnaireAnnotations gestionnaireAnnotations;
    private GestionnaireCrop gestionnaireCrop;
    private PerspectiveLeaf perspective;
    private GestionnaireOutils gestionnaireOutils;
    private BufferedImage imageOriginale;
    private String cheminImageCache;

    private static final int PREVIEW_WIDTH = 300;
    private static final int PREVIEW_HEIGHT = 250;

    /**
     * Constructeur de la vue d'annotations.
     * @param perspective La perspective initiale (peut être null).
     * @param gestionnaireAnnotations Le gestionnaire des annotations à observer.
     * @param gestionnaireCrop Le gestionnaire de crop à observer.
     */
    public VueAnnotations(PerspectiveLeaf perspective,
                          GestionnaireAnnotations gestionnaireAnnotations,
                          GestionnaireCrop gestionnaireCrop) {
        this.perspective = perspective;
        this.gestionnaireAnnotations = gestionnaireAnnotations;
        this.gestionnaireCrop = gestionnaireCrop;

        // Abonnement aux sujets observés
        if (perspective != null) {
            perspective.attacher(this);
        }
        if (gestionnaireAnnotations != null) {
            gestionnaireAnnotations.attacher(this);
        }
        if (gestionnaireCrop != null) {
            gestionnaireCrop.attacher(this);
        }

        setOpaque(false);
        setPreferredSize(new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT));
        setBackground(new Color(255, 255, 255));
        setBorder(BorderFactory.createTitledBorder("Preview (Résultat final)"));
    }

    /**
     * Associe le gestionnaire d'outils pour afficher les prévisualisations en temps réel (ex: ligne en cours de tracé).
     * @param gestionnaireOutils Le gestionnaire d'outils.
     */
    public void setGestionnaireOutils(GestionnaireOutils gestionnaireOutils) {
        this.gestionnaireOutils = gestionnaireOutils;
    }

    /**
     * Change la perspective observée (ex: lors du chargement d'un nouveau fichier).
     * Gère le désabonnement de l'ancienne et l'abonnement à la nouvelle.
     * @param perspective La nouvelle perspective.
     */
    public void setPerspective(PerspectiveLeaf perspective) {
        if (this.perspective != null) {
            this.perspective.detacher(this);
        }
        this.perspective = perspective;
        if (this.perspective != null) {
            this.perspective.attacher(this);
            // Force le rechargement de l'image si le chemin a changé
            cheminImageCache = null; 
        }
        actualiser();
    }

    /**
     * Méthode de l'interface ObserverIF.
     * Appelée automatiquement lorsque l'un des modèles observés change.
     * Déclenche le rechargement de l'image (si nécessaire) et le redessin de la vue.
     */
    @Override
    public void actualiser() {
        // ... (log de debug) ...
        if (perspective != null) {
            String chemin = perspective.getCheminImage();

            if (chemin != null && !chemin.isEmpty() && !chemin.equals(cheminImageCache)) {
                try {
                    imageOriginale = ImageIO.read(new File(chemin));
                    cheminImageCache = chemin;
                    System.out.println("[VueAnnotations] Image chargée: " + new File(chemin).getName());
                } catch (IOException e) {
                    imageOriginale = null;
                    cheminImageCache = null;
                    System.err.println("[VueAnnotations] Erreur chargement image");
                }
            }
        }

        repaint();
    }

    /**
     * Dessine le composant. C'est ici que la logique de rendu de l'aperçu est implémentée.
     * @param g Le contexte graphique.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // Activation de l'antialiasing pour un rendu plus net
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (imageOriginale == null) {
            g2d.setColor(Color.GRAY);
            g2d.drawString("Aucune image", getWidth() / 2 - 40, getHeight() / 2);
            return;
        }

        // Détermine si on doit afficher seulement le crop ou l'image entière
        Rectangle zoneCrop = (gestionnaireCrop != null && gestionnaireCrop.hasCrop())
                ? gestionnaireCrop.getZoneCrop()
                : null;

        if (zoneCrop != null) {
            dessinerPreviewCrop(g2d, zoneCrop);
        } else {
            dessinerPreviewComplete(g2d);
        }
    }

    /**
     * Dessine l'aperçu de la zone recadrée (Crop).
     * Calcule l'échelle pour que la zone sélectionnée remplisse au mieux la vue.
     * @param g2d Contexte graphique.
     * @param zoneCrop Rectangle définissant la zone à afficher (coordonnées image originale).
     */
    private void dessinerPreviewCrop(Graphics2D g2d, Rectangle zoneCrop) {
        int srcX = zoneCrop.x;
        int srcY = zoneCrop.y;
        int srcW = zoneCrop.width;
        int srcH = zoneCrop.height;

        // Validation des bornes pour éviter les exceptions
        if (srcX < 0) srcX = 0;
        if (srcY < 0) srcY = 0;
        if (srcX + srcW > imageOriginale.getWidth()) srcW = imageOriginale.getWidth() - srcX;
        if (srcY + srcH > imageOriginale.getHeight()) srcH = imageOriginale.getHeight() - srcY;

        // Calcul du facteur d'échelle pour "fit" dans la vue
        double scaleW = (double) (getWidth() - 20) / srcW;
        double scaleH = (double) (getHeight() - 50) / srcH;
        double scale = Math.min(scaleW, scaleH);

        int destW = (int) (srcW * scale);
        int destH = (int) (srcH * scale);

        // Centrage dans la vue
        int destX = (getWidth() - destW) / 2;
        int destY = (getHeight() - destH) / 2 + 15;

        // Fond blanc
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Dessin de la portion d'image
        g2d.drawImage(imageOriginale,
                destX, destY, destX + destW, destY + destH,
                srcX, srcY, srcX + srcW, srcY + srcH,
                this);

        // Dessin des annotations par-dessus
        if (gestionnaireAnnotations != null) {
            // Créer une copie du Graphics2D pour appliquer les transformations (Translation + Scale)
            // Cela permet de dessiner les annotations dans leur référentiel d'origine
            Graphics2D g2dTransformed = (Graphics2D) g2d.create();
            g2dTransformed.translate(destX - srcX * scale, destY - srcY * scale); // Ajustement complexe pour le crop
            g2dTransformed.scale(scale, scale);
            
            // Note: La translation ci-dessus est une approximation simplifiée. 
            // Pour être exact avec le crop, on devrait translater de (destX - srcX*scale, destY - srcY*scale)
            // Mais ici on simplifie : on translate pour que (0,0) de l'image soit au bon endroit virtuel
            // Correction : g2dTransformed.translate(destX - (srcX * scale), destY - (srcY * scale));

            // On utilise un clip pour ne pas dessiner les annotations hors du crop
            g2dTransformed.setClip(srcX, srcY, srcW, srcH);

            for (Annotation annotation : gestionnaireAnnotations.getAnnotations()) {
                annotation.dessiner(g2dTransformed);
            }

            // Dessiner les previews des outils actifs
            dessinerPreviewsOutils(g2dTransformed);

            g2dTransformed.dispose();
        }
    }

    /**
     * Dessine l'aperçu de l'image complète (sans crop).
     * @param g2d Contexte graphique.
     */
    private void dessinerPreviewComplete(Graphics2D g2d) {
        int imgW = imageOriginale.getWidth();
        int imgH = imageOriginale.getHeight();

        // Calcul de l'échelle "fit to view"
        double scaleW = (double) (getWidth() - 20) / imgW;
        double scaleH = (double) (getHeight() - 50) / imgH;
        double scale = Math.min(scaleW, scaleH);

        int scaledW = (int) (imgW * scale);
        int scaledH = (int) (imgH * scale);

        int x = (getWidth() - scaledW) / 2;
        int y = (getHeight() - scaledH) / 2 + 15;

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.drawImage(imageOriginale, x, y, scaledW, scaledH, this);

        if (gestionnaireAnnotations != null) {
            Graphics2D g2dTransformed = (Graphics2D) g2d.create();
            g2dTransformed.translate(x, y);
            g2dTransformed.scale(scale, scale);

            for (Annotation annotation : gestionnaireAnnotations.getAnnotations()) {
                annotation.dessiner(g2dTransformed);
            }

            dessinerPreviewsOutils(g2dTransformed);

            g2dTransformed.dispose();
        }
    }

    /**
     * Dessine les prévisualisations temporaires des outils (ex: ligne élastique).
     * @param g2d Contexte graphique déjà transformé (dans le référentiel image).
     */
    private void dessinerPreviewsOutils(Graphics2D g2d) {
        if (gestionnaireOutils == null) return;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Preview de l'outil ligne
        if (gestionnaireOutils.getModeLigne() != null) {
            Annotation preview = gestionnaireOutils.getModeLigne().getOutil().getAnnotationPreview();
            if (preview != null) {
                preview.dessiner(g2d);
            }
        }

        // Preview de l'outil crop (rectangle de sélection)
        if (gestionnaireOutils.getModeCrop() != null) {
            Rectangle previewRect = gestionnaireOutils.getModeCrop().getOutil().getRectanglePreview();
            if (previewRect != null) {
                g2d.setColor(new Color(255, 0, 0, 150));
                g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawRect(previewRect.x, previewRect.y, previewRect.width, previewRect.height);
            }
        }
    }
}
