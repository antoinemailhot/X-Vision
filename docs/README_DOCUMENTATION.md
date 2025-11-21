# Documentation X-Vision

Guide d'utilisation de la documentation complète du projet X-Vision.

---

## 📚 Documents Disponibles

### 1. **RAPPORT_CARTOGRAPHIE.md** (Rapport Principal)

**Description:** Rapport détaillé complet de l'analyse architecturale du projet X-Vision.

**Contenu:**
- Vue d'ensemble du projet (statistiques, technologies)
- Structure des packages détaillée
- Architecture MVC complète (Modèle, Vue, Contrôleur)
- Analyse détaillée de chaque patron GoF (9 patrons)
- Analyse des principes GRASP (5 principes)
- Conformité MVC et patrons
- Métriques de qualité
- Problèmes identifiés avec solutions
- Recommandations prioritaires
- Conclusion et apprentissages

**Utilisation:**
```bash
# Lire dans votre éditeur markdown
code docs/RAPPORT_CARTOGRAPHIE.md

# Ou convertir en PDF
pandoc docs/RAPPORT_CARTOGRAPHIE.md -o docs/RAPPORT_CARTOGRAPHIE.pdf
```

**Taille:** ~40 pages
**Format:** Markdown
**Audience:** Professeur, étudiants, évaluateurs

---

### 2. **Diagrammes UML PlantUML**

#### 2.1. **diagramme_architecture_complete.puml**

**Description:** Diagramme de classes complet montrant toute l'architecture X-Vision.

**Contenu:**
- Toutes les classes principales (model, view, controller)
- Relations entre classes
- Patrons GoF annotés
- Principes GRASP annotés
- Légende explicative

**Générer SVG:**
```bash
# Installer PlantUML si nécessaire
brew install plantuml

# Générer le diagramme
plantuml docs/diagramme_architecture_complete.puml

# Résultat: docs/diagramme_architecture_complete.svg
```

**Utilisation:**
- Ouvrir le .svg dans un navigateur
- Inclure dans rapport final
- Présentation PowerPoint

---

#### 2.2. **diagramme_packages.puml**

**Description:** Structure des packages et modules avec statistiques.

**Contenu:**
- Organisation des packages (model, view, controller, test)
- Dépendances entre packages
- Métriques (nombre de classes, lignes de code)
- Vérification règles MVC
- Légende avec statistiques globales

**Générer SVG:**
```bash
plantuml docs/diagramme_packages.puml
# Résultat: docs/diagramme_packages.svg
```

**Utilisation:**
- Comprendre organisation globale
- Valider dépendances MVC
- Présentation de haut niveau

---

#### 2.3. **diagramme_patterns_overview.puml**

**Description:** Récapitulatif visuel de tous les patrons GoF et GRASP.

**Contenu:**
- 9 patrons GoF (avec statut ✅/⚠️)
- 2 patrons bonus
- 5 principes GRASP
- Score global
- Tâches prioritaires restantes
- Références GoF et Larman

**Générer SVG:**
```bash
plantuml docs/diagramme_patterns_overview.puml
# Résultat: docs/diagramme_patterns_overview.svg
```

**Utilisation:**
- Vue d'ensemble rapide
- Présentation des patrons
- Checklist de conformité

---

### 3. **Diagrammes Existants**

Le projet contient déjà des diagrammes UML pour chaque patron:

| Diagramme | Description |
|-----------|-------------|
| `diagramme_observer.puml/.svg` | Patron Observer détaillé |
| `diagramme_command.puml/.svg` | Patron Command détaillé |
| `diagramme_memento.puml/.svg` | Patron Memento détaillé |
| `diagramme_strategy.puml/.svg` | Patron Strategy (à compléter) |
| `diagramme_mediator.puml/.svg` | Patron Mediator détaillé |
| `diagramme_singleton.puml/.svg` | Patron Singleton détaillé |
| `diagramme_factory_method.puml/.svg` | Patron Factory Method détaillé |
| `diagramme_adapter.puml/.svg` | Patron Adapter détaillé |
| `diagramme_composite.puml/.svg` | Patron Composite détaillé |
| `diagramme_grasp.puml/.svg` | Principes GRASP |
| `diagramme_sequence_commande.puml/.svg` | Séquence exécution commande |
| `diagramme_sequence_undo.puml/.svg` | Séquence Undo/Redo |

**Tous ces diagrammes sont déjà générés en SVG.**

---

## 🔧 Installation et Génération

### Prérequis

1. **PlantUML:**
```bash
# macOS
brew install plantuml

# Ubuntu/Debian
sudo apt-get install plantuml

# Ou via Java
java -jar plantuml.jar
```

2. **Graphviz (requis par PlantUML):**
```bash
# macOS
brew install graphviz

# Ubuntu/Debian
sudo apt-get install graphviz
```

3. **Pandoc (optionnel, pour conversion Markdown → PDF):**
```bash
# macOS
brew install pandoc

# Ubuntu/Debian
sudo apt-get install pandoc
```

### Générer tous les diagrammes

```bash
# Se placer dans le dossier docs/
cd /Users/mrleblanc/Documents/GitHub/X-Vision/docs

# Générer tous les diagrammes .puml → .svg
plantuml *.puml

# Ou un seul
plantuml diagramme_architecture_complete.puml
```

### Convertir le rapport en PDF

```bash
pandoc RAPPORT_CARTOGRAPHIE.md -o RAPPORT_CARTOGRAPHIE.pdf \
  --toc \
  --toc-depth=3 \
  --number-sections \
  -V geometry:margin=1in
```

---

## 📖 Guide de Lecture

### Pour une révision rapide (30 minutes)

1. Lire **RAPPORT_CARTOGRAPHIE.md** - Section "Résumé exécutif"
2. Voir **diagramme_patterns_overview.svg** (récapitulatif patrons)
3. Voir **diagramme_packages.svg** (structure globale)

### Pour une compréhension approfondie (2-3 heures)

1. Lire **RAPPORT_CARTOGRAPHIE.md** complet
2. Examiner **diagramme_architecture_complete.svg** (classes détaillées)
3. Parcourir les diagrammes individuels par patron
4. Lire les sections "Problèmes identifiés" et "Recommandations"

### Pour préparer le rapport final de cours

1. Inclure **RAPPORT_CARTOGRAPHIE.md** comme base
2. Ajouter **diagramme_architecture_complete.svg** (architecture globale)
3. Ajouter **diagramme_patterns_overview.svg** (récapitulatif patrons)
4. Sélectionner 2-3 diagrammes de patrons spécifiques (ex: Observer, Command, Memento)
5. Ajouter captures d'écran de l'application
6. Ajouter section "Implémentation de Strategy" une fois complétée

---

## 🎯 Points Clés à Retenir

### Forces du projet

✅ **Architecture MVC rigoureuse (96.7%)**
- Séparation stricte Modèle-Vue-Contrôleur
- Modèle totalement découplé
- Toutes modifications via Commandes

✅ **8/9 patrons GoF complets (88.9%)**
- Observer, Command, Memento impeccables
- Singleton, Factory, Adapter bien implémentés
- Composite pour hiérarchie perspectives
- Mediator pour coordination

✅ **5/5 principes GRASP (99%)**
- Information Expert, Creator, Controller respectés
- Low Coupling via interfaces
- High Cohesion (responsabilités claires)

✅ **2 patrons bonus**
- State (modes d'interaction)
- Template Method (rafraîchissement vues)

### Point critique

⚠️ **Patron Strategy incomplet (30%)**
- Actuellement: copie seulement position (x, y)
- Manque: stratégies interchangeables (CopieEchelle, CopieTranslation, CopieComplete)
- Impact: -11% sur score patrons GoF
- **Solution:** Voir section "Problèmes identifiés" du rapport
- **Effort:** 3-4 heures

### Améliorations suggérées

1. **Priorité HAUTE:** Implémenter Strategy (critique pour la note)
2. **Priorité MOYENNE:** Refactorer VuePrincipale (cohérence architecture)
3. **Priorité BASSE:** Tests JUnit 5, nouveaux outils de dessin

---

## 📊 Utilisation des Diagrammes

### Dans un rapport Word/Google Docs

1. Générer les SVG avec PlantUML
2. Convertir SVG → PNG si nécessaire:
```bash
# macOS avec rsvg-convert
brew install librsvg
rsvg-convert -h 1200 diagramme_architecture_complete.svg -o architecture.png

# Ou avec Inkscape
inkscape diagramme_architecture_complete.svg --export-png=architecture.png
```
3. Insérer les images dans le document

### Dans une présentation PowerPoint

1. Utiliser les SVG directement (PowerPoint supporte SVG)
2. Ou convertir en PNG haute résolution
3. Un diagramme par slide avec explications

### Dans LaTeX

```latex
\begin{figure}[h]
  \centering
  \includegraphics[width=\textwidth]{docs/diagramme_architecture_complete.svg}
  \caption{Architecture complète X-Vision}
  \label{fig:architecture}
\end{figure}
```

---

## 🔍 Vérification de Conformité

Utiliser le rapport pour vérifier:

### ✅ Checklist MVC

- [ ] Modèle ne connaît pas les vues → **OUI** (aucun import view/ dans model/)
- [ ] Vues implémentent ObserverIF → **OUI** (sauf VuePrincipale qui l'implémente directement)
- [ ] Modifications via Commandes → **OUI** (aucune modification directe)
- [ ] Contrôleur délègue au modèle → **OUI** (via GestionnaireCommandes)

### ✅ Checklist Patrons GoF

- [ ] Observer → **✅ 100%**
- [ ] Command → **✅ 100%**
- [ ] Memento → **✅ 100%**
- [ ] Strategy → **⚠️ 30%** (à compléter)
- [ ] Mediator → **✅ 100%**
- [ ] Singleton → **✅ 100%**
- [ ] Factory Method → **✅ 100%**
- [ ] Adapter → **✅ 100%**
- [ ] Composite → **✅ 100%**

### ✅ Checklist GRASP

- [ ] Information Expert → **✅ 100%**
- [ ] Creator → **✅ 100%**
- [ ] Controller → **✅ 100%**
- [ ] Low Coupling → **✅ 95%** (VuePrincipale pénalité)
- [ ] High Cohesion → **✅ 100%**

---

## 📧 Support

Pour toute question sur la documentation:
- Consulter le rapport complet: `RAPPORT_CARTOGRAPHIE.md`
- Examiner les diagrammes UML
- Vérifier les références GoF (Design Patterns book)
- Vérifier les références GRASP (Larman - Applying UML and Patterns)

---

## 📅 Historique

| Date | Version | Modifications |
|------|---------|---------------|
| 2025-11-20 | 1.0 | Création rapport cartographie complet |
| 2025-11-20 | 1.0 | Création 3 nouveaux diagrammes UML |
| 2025-11-20 | 1.0 | Création guide documentation |

---

## 📚 Références

### Livres
- **GoF:** Design Patterns - Elements of Reusable Object-Oriented Software (Gamma, Helm, Johnson, Vlissides)
- **GRASP:** Applying UML and Patterns (Craig Larman)

### Outils
- **PlantUML:** https://plantuml.com/
- **Graphviz:** https://graphviz.org/
- **Pandoc:** https://pandoc.org/

---

**Excellent travail sur X-Vision!**
**Avec l'implémentation de Strategy, le projet atteindra 100% de conformité GoF.**

---

**Auteurs:** Antoine Mailhot & Micaël Racine-Leblanc
**Cours:** INF1163 - Conception détaillée & modélisation
**Projet:** X-Vision
**Date:** 20 novembre 2025
