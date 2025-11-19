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
 * Vue preview du résultat final (zone crop ou image complète)
 * Patron Observer (GoF): Observe GestionnaireAnnotations + GestionnaireCrop + Perspective
 * Principe GRASP: High Cohesion - responsabilité = preview du résultat
 *
 * @author Antoine Mailhot & Micaël Racine-Leblanc
 * @version 3.0
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
     * Constructeur
     * @param perspective La perspective pour le chemin de l'image
     * @param gestionnaireAnnotations Le gestionnaire d'annotations
     * @param gestionnaireCrop Le gestionnaire de crop
     */
    public VueAnnotations(PerspectiveLeaf perspective,
                          GestionnaireAnnotations gestionnaireAnnotations,
                          GestionnaireCrop gestionnaireCrop) {
        this.perspective = perspective;
        this.gestionnaireAnnotations = gestionnaireAnnotations;
        this.gestionnaireCrop = gestionnaireCrop;

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
     * Définit le gestionnaire d'outils pour afficher les previews
     * @param gestionnaireOutils Le gestionnaire d'outils
     */
    public void setGestionnaireOutils(GestionnaireOutils gestionnaireOutils) {
        this.gestionnaireOutils = gestionnaireOutils;
    }

    /**
     * Mise à jour Observer
     */
    @Override
    public void actualiser() {
        System.out.println("[VueAnnotations] actualiser() appelée - Annotations: " +
            (gestionnaireAnnotations != null ? gestionnaireAnnotations.getNombreAnnotations() : 0) +
            ", Crop: " + (gestionnaireCrop != null && gestionnaireCrop.hasCrop() ? "OUI" : "NON"));

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
     * Dessine la preview du résultat final
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        System.out.println("[VueAnnotations] paintComponent appelée - Image: " + (imageOriginale != null ? "OUI" : "NON"));

        if (imageOriginale == null) {
            g2d.setColor(Color.GRAY);
            g2d.drawString("Aucune image", getWidth() / 2 - 40, getHeight() / 2);
            return;
        }

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
     * Dessine la preview de la zone croppée
     */
    private void dessinerPreviewCrop(Graphics2D g2d, Rectangle zoneCrop) {
        int srcX = zoneCrop.x;
        int srcY = zoneCrop.y;
        int srcW = zoneCrop.width;
        int srcH = zoneCrop.height;

        if (srcX < 0) srcX = 0;
        if (srcY < 0) srcY = 0;
        if (srcX + srcW > imageOriginale.getWidth()) srcW = imageOriginale.getWidth() - srcX;
        if (srcY + srcH > imageOriginale.getHeight()) srcH = imageOriginale.getHeight() - srcY;

        double scaleW = (double) (getWidth() - 20) / srcW;
        double scaleH = (double) (getHeight() - 50) / srcH;
        double scale = Math.min(scaleW, scaleH);

        int destW = (int) (srcW * scale);
        int destH = (int) (srcH * scale);

        int destX = (getWidth() - destW) / 2;
        int destY = (getHeight() - destH) / 2 + 15;

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.drawImage(imageOriginale,
                destX, destY, destX + destW, destY + destH,
                srcX, srcY, srcX + srcW, srcY + srcH,
                this);

        if (gestionnaireAnnotations != null) {
            // Créer une copie du Graphics2D pour les transformations
            Graphics2D g2dTransformed = (Graphics2D) g2d.create();
            g2dTransformed.translate(destX - srcX, destY - srcY);
            g2dTransformed.scale(scale, scale);

            for (Annotation annotation : gestionnaireAnnotations.getAnnotations()) {
                annotation.dessiner(g2dTransformed);
            }

            // Dessiner les previews des outils actifs
            dessinerPreviewsOutils(g2dTransformed);

            g2dTransformed.dispose();
        }
    }

    /**
     * Dessine la preview de l'image complète
     */
    private void dessinerPreviewComplete(Graphics2D g2d) {
        int imgW = imageOriginale.getWidth();
        int imgH = imageOriginale.getHeight();

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
            // Créer une copie du Graphics2D pour les transformations
            Graphics2D g2dTransformed = (Graphics2D) g2d.create();
            g2dTransformed.translate(x, y);
            g2dTransformed.scale(scale, scale);

            for (Annotation annotation : gestionnaireAnnotations.getAnnotations()) {
                annotation.dessiner(g2dTransformed);
            }

            // Dessiner les previews des outils actifs
            dessinerPreviewsOutils(g2dTransformed);

            g2dTransformed.dispose();
        }
    }

    /**
     * Dessine les previews des outils actifs (ligne, crop)
     * @param g2d Contexte graphique déjà transformé (translate + scale)
     */
    private void dessinerPreviewsOutils(Graphics2D g2d) {
        if (gestionnaireOutils == null) {
            System.out.println("[VueAnnotations] gestionnaireOutils est NULL");
            return;
        }

        // Activer l'antialiasing pour un meilleur rendu
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Preview de l'outil ligne
        if (gestionnaireOutils.getModeLigne() != null) {
            Annotation preview = gestionnaireOutils.getModeLigne().getOutil().getAnnotationPreview();
            System.out.println("[VueAnnotations] Preview ligne: " + (preview != null ? "OUI" : "NON"));
            if (preview != null) {
                preview.dessiner(g2d);
            }
        }

        // Preview de l'outil crop
        if (gestionnaireOutils.getModeCrop() != null) {
            Rectangle previewRect = gestionnaireOutils.getModeCrop().getOutil().getRectanglePreview();
            System.out.println("[VueAnnotations] Preview crop: " + (previewRect != null ? "OUI" : "NON"));
            if (previewRect != null) {
                g2d.setColor(new Color(255, 0, 0, 150));
                g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawRect(previewRect.x, previewRect.y, previewRect.width, previewRect.height);
            }
        }
    }
}
