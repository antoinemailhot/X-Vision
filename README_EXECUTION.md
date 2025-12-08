# 🚀 X-Vision — Guide d'exécution des tests (TP2)

Ce document explique comment exécuter la suite de tests complète pour le **TP2 INF1163 - Stratégie & mise en œuvre des tests**.

---

## 📋 Prérequis

### 1. Java Development Kit (JDK)
- **Version requise**: JDK 17 ou supérieur
- Vérifier l'installation:
  ```bash
  java -version
  javac -version
  ```
- Si non installé: [Télécharger OpenJDK 17+](https://adoptium.net/)

### 2. Gradle (optionnel, le wrapper est inclus)
Le projet inclut le **Gradle Wrapper** (`gradlew`), donc Gradle n'a pas besoin d'être installé globalement.

---

## 🏗️ Structure du projet

```
X-Vision/
├── build.gradle              # Configuration Gradle (dépendances, plugins, tâches)
├── settings.gradle           # Métadonnées du projet
├── src/
│   ├── main/
│   │   └── java/xvision/     # Code production (Modèle, Vue, Contrôleur)
│   └── test/
│       ├── java/xvision/
│       │   ├── unit/         # Tests unitaires (~70% de la pyramide)
│       │   ├── integration/  # Tests d'intégration (~20%)
│       │   ├── e2e/          # Tests end-to-end (~10%)
│       │   └── acceptance/   # Tests BDD/Gherkin (Cucumber)
│       └── resources/
│           ├── features/     # Fichiers .feature (scenarios Gherkin)
│           └── test-images/  # Images pour les tests
└── docs/
    ├── Plan_de_Test_TP2.pdf  # Plan de test (à créer)
    └── rapports/             # Rapports générés (JaCoCo, JUnit, PIT)
```

---

## ⚡ Commandes Gradle essentielles

### 🧪 Exécution des tests

#### Tous les tests
```bash
./gradlew test
```

#### Tests par catégorie (pyramide de tests)
```bash
# Tests unitaires uniquement (rapides)
./gradlew testUnit

# Tests d'intégration uniquement
./gradlew testIntegration

# Tests end-to-end uniquement (lents)
./gradlew testE2E

# Tests d'acceptation (BDD/Gherkin)
./gradlew testAcceptance

# Pyramide complète dans l'ordre (unit → integration → e2e → acceptance)
./gradlew testPyramid
```

### 📊 Rapports de couverture (JaCoCo)

#### Générer le rapport de couverture
```bash
./gradlew jacocoTestReport
```
**Résultat**: `build/reports/jacoco/test/html/index.html`

#### Vérifier les seuils de couverture (conformité TP2)
```bash
./gradlew jacocoTestCoverageVerification
```
**Cibles**:
- Modèle (`xvision.model.*`): ≥ 80%
- Contrôleurs (`xvision.controller.*`): ≥ 70%

### 🧬 Mutation testing (PIT) — Optionnel

```bash
./gradlew pitest
```
**Résultat**: `build/reports/pitest/index.html`

### 📦 Rapport complet (tests + couverture + mutation)

```bash
./gradlew rapportComplet
```
Génère tous les rapports en une seule commande.

### 🧹 Nettoyage

```bash
# Nettoyage standard
./gradlew clean

# Nettoyage complet (+ fichiers temporaires de tests)
./gradlew cleanAll
```

---

## 🔧 Workflow de développement recommandé

### 1️⃣ Développement local (TDD)
```bash
# Pendant le développement: exécuter seulement les tests unitaires (rapides)
./gradlew testUnit --continuous
```
L'option `--continuous` relance automatiquement les tests à chaque modification.

### 2️⃣ Avant un commit
```bash
# Exécuter tous les tests + vérifier la couverture
./gradlew test jacocoTestReport
```

### 3️⃣ Avant la remise finale
```bash
# Build complet + rapports
./gradlew clean build rapportComplet

# Vérifier les artefacts:
open build/reports/jacoco/test/html/index.html   # Couverture
open build/reports/tests/test/index.html         # Résultats tests
```

---

## 🐛 Dépannage

### Problème: `Permission denied: ./gradlew`
**Solution**:
```bash
chmod +x gradlew
```

### Problème: `JAVA_HOME not set`
**Solution macOS/Linux**:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

**Solution Windows**:
```cmd
set JAVA_HOME=C:\Program Files\Java\jdk-17
```

### Problème: Tests échouent avec `OutOfMemoryError`
**Solution**: Augmenter la mémoire dans `build.gradle` (section `test { maxHeapSize = '2G' }`)

### Problème: Gradle télécharge lentement les dépendances
**Solution**: Première exécution uniquement (télécharge JUnit, Mockito, etc.). Utiliser un miroir Maven si nécessaire.

---

## 📈 Interprétation des rapports

### Rapport JaCoCo (Couverture)
- **Vert**: Ligne couverte par au moins un test
- **Rouge**: Ligne jamais exécutée
- **Jaune**: Branche partiellement couverte (if/else)

**Objectif TP2**:
- Modèle: 80%+ vert
- Contrôleurs: 70%+ vert
- Vues: Pragmatique (non testé, acceptable)

### Rapport JUnit (Résultats)
- **Tests**: Nombre total de tests exécutés
- **Failures**: Assertions échouées (bugs à corriger)
- **Errors**: Exceptions inattendues (bugs critiques)
- **Skipped**: Tests ignorés (@Disabled)

### Rapport PIT (Mutation)
- **Mutation Coverage**: % de mutants tués par les tests
- **Mutants killed**: Tests détectent les modifications (bon signe)
- **Mutants survived**: Tests ne détectent pas les modifications (améliorer tests)

---

## 🎯 Checklist avant remise TP2

- [ ] Tous les tests passent (`./gradlew test` = SUCCESS)
- [ ] Couverture ≥80% modèle, ≥70% contrôleurs (`jacocoTestCoverageVerification` = SUCCESS)
- [ ] Pyramide respectée (70% unit / 20% integration / 10% e2e)
- [ ] Rapports générés et placés dans `docs/rapports/`
- [ ] Plan de test PDF complété (`docs/Plan_de_Test_TP2.pdf`)
- [ ] Capsule vidéo ≤20s enregistrée (CI verte + rapport couverture)
- [ ] README_EXECUTION.md à jour (ce fichier)

---

## 📚 Ressources

- **JUnit 5**: https://junit.org/junit5/docs/current/user-guide/
- **Mockito**: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- **JaCoCo**: https://www.jacoco.org/jacoco/trunk/doc/
- **Cucumber**: https://cucumber.io/docs/cucumber/
- **Gradle**: https://docs.gradle.org/current/userguide/userguide.html

---

## 👥 Auteurs

**Équipe X-Vision**
- Antoine Mailhot
- Micaël Racine-Leblanc

**Cours**: INF1163 — Conception détaillée & modélisation
**Institution**: UQAM
**Session**: Automne 2025
