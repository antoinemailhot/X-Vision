# 🛠️ Configuration initiale du projet X-Vision (TP2)

Ce guide explique comment initialiser le projet après avoir cloné le dépôt ou configuré Gradle pour la première fois.

---

## 📦 Étape 1: Installer Gradle (si nécessaire)

### Option A: Utiliser le Gradle Wrapper (recommandé)

Si les fichiers `gradlew` et `gradle/wrapper/` existent déjà dans le projet, **aucune installation n'est nécessaire**. Passez directement à l'Étape 2.

### Option B: Installer Gradle globalement

**macOS (Homebrew)**:
```bash
brew install gradle
```

**Linux**:
```bash
# Télécharger et extraire
wget https://services.gradle.org/distributions/gradle-8.5-bin.zip
unzip gradle-8.5-bin.zip
sudo mv gradle-8.5 /opt/gradle

# Ajouter au PATH
echo "export PATH=/opt/gradle/bin:\$PATH" >> ~/.bashrc
source ~/.bashrc
```

**Windows**:
1. Télécharger: https://gradle.org/releases/
2. Extraire dans `C:\Gradle`
3. Ajouter `C:\Gradle\bin` au PATH

**Vérifier l'installation**:
```bash
gradle --version
```

---

## ⚙️ Étape 2: Initialiser le Gradle Wrapper (première fois uniquement)

Si le wrapper n'existe pas encore, créez-le avec:

```bash
gradle wrapper --gradle-version 8.5
```

Cela crée:
- `gradlew` (script Unix/Mac)
- `gradlew.bat` (script Windows)
- `gradle/wrapper/` (JAR et propriétés)

**Ensuite, committez ces fichiers dans Git**:
```bash
git add gradlew gradle/
git commit -m "Ajout du Gradle wrapper pour portabilité"
```

---

## 🧪 Étape 3: Vérifier que tout fonctionne

### Test 1: Compilation
```bash
./gradlew compileJava
```
**Résultat attendu**: `BUILD SUCCESSFUL`

### Test 2: Exécution des tests (une fois les tests créés)
```bash
./gradlew test
```
**Résultat attendu**: Tests exécutés (pour l'instant 0 tests si aucun test créé)

### Test 3: Rapport de couverture
```bash
./gradlew jacocoTestReport
```
**Résultat attendu**: Rapport HTML généré dans `build/reports/jacoco/test/html/index.html`

---

## 🔄 Migration du code existant (si applicable)

Si le projet contient encore l'ancienne structure `src/xvision/`, migrer vers la nouvelle:

```bash
# Vérifier que la nouvelle structure existe
ls -la src/main/java/xvision/

# Supprimer les fichiers .class de l'ancienne structure
find src/xvision -name "*.class" -delete

# (Optionnel) Supprimer l'ancienne structure après vérification
# rm -rf src/xvision/
```

**⚠️ IMPORTANT**: Ne supprimez `src/xvision/` qu'après avoir vérifié que tout compile avec la nouvelle structure!

---

## 🐛 Dépannage

### Erreur: `Could not find or load main class`
**Cause**: Les packages ne correspondent pas à la nouvelle structure.

**Solution**: Vérifier que tous les fichiers Java ont la bonne déclaration de package:
```java
package xvision.model;  // Pour src/main/java/xvision/model/
```

### Erreur: `Unsupported class file major version`
**Cause**: Mauvaise version de Java.

**Solution**: Vérifier la version de Java:
```bash
java -version  # Doit être 17+
```

Si vous avez plusieurs versions installées:
```bash
# macOS
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Linux
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk

# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-17
```

### Erreur: `gradlew: Permission denied`
**Solution**:
```bash
chmod +x gradlew
```

---

## 📝 Prochaines étapes

Une fois l'infrastructure en place:
1. ✅ Créer les tests unitaires dans `src/test/java/xvision/unit/`
2. ✅ Créer les tests d'intégration dans `src/test/java/xvision/integration/`
3. ✅ Créer les tests E2E dans `src/test/java/xvision/e2e/`
4. ✅ Créer les scénarios Gherkin dans `src/test/resources/features/`
5. ✅ Générer les rapports avec `./gradlew rapportComplet`

**Voir README_EXECUTION.md pour les détails d'exécution des tests.**

---

## 📚 Ressources

- Documentation Gradle: https://docs.gradle.org/current/userguide/userguide.html
- Gradle Wrapper: https://docs.gradle.org/current/userguide/gradle_wrapper.html
- JUnit 5: https://junit.org/junit5/docs/current/user-guide/
