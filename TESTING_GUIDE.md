# 🧪 Guide des bonnes pratiques de tests — X-Vision (TP2)

Ce document rassemble les bonnes pratiques de tests pour le projet X-Vision, basées sur les tests modèles créés.

---

## 📚 Table des matières

1. [Pyramide de tests](#pyramide-de-tests)
2. [Style AAA (Arrange-Act-Assert)](#style-aaa)
3. [Nommage des tests](#nommage-des-tests)
4. [Tags JUnit](#tags-junit)
5. [Utilisation de Mockito](#utilisation-de-mockito)
6. [Setup/Teardown](#setupteardown)
7. [Assertions](#assertions)
8. [Tests paramétrés](#tests-paramétrés)
9. [Anti-patterns à éviter](#anti-patterns-à-éviter)
10. [Checklist avant commit](#checklist-avant-commit)

---

## 🔺 Pyramide de tests

### Répartition cible (TP2)

```
        ╱╲
       ╱E2E╲          10% - Tests end-to-end
      ╱──────╲
     ╱Integration╲     20% - Tests d'intégration
    ╱────────────╲
   ╱  Unitaires   ╲    70% - Tests unitaires
  ╱────────────────╲
```

### Règles de répartition

**Tests unitaires (~70%)**:
- ✅ **Testent**: UNE classe isolément
- ✅ **Utilisent**: Mocks pour les dépendances
- ✅ **Sont**: Rapides (<100ms par test)
- ✅ **Tag**: `@Tag("unit")`
- ✅ **Emplacement**: `src/test/java/xvision/unit/`
- ✅ **Exemples**: `PerspectiveLeafTest`, `CommandeZoomTest`

**Tests d'intégration (~20%)**:
- ✅ **Testent**: Interaction entre plusieurs classes
- ✅ **Utilisent**: Vraies instances (pas de mocks, ou mocks limités)
- ✅ **Sont**: Moyennement rapides (100-500ms par test)
- ✅ **Tag**: `@Tag("integration")`
- ✅ **Emplacement**: `src/test/java/xvision/integration/`
- ✅ **Exemples**: `GestionnaireCommandesIntegrationTest`

**Tests E2E (~10%)**:
- ✅ **Testent**: Parcours utilisateur complet (bout en bout)
- ✅ **Utilisent**: Système complet (GUI, persistance, etc.)
- ✅ **Sont**: Lents (>500ms par test)
- ✅ **Tag**: `@Tag("e2e")`
- ✅ **Emplacement**: `src/test/java/xvision/e2e/`
- ✅ **Exemples**: À créer (Undo vue active, Copier/coller inter-vues)

**Tests d'acceptation (BDD/Gherkin)**:
- ✅ **Testent**: Scénarios métiers lisibles
- ✅ **Utilisent**: Fichiers `.feature` + step definitions
- ✅ **Tag**: `@Tag("acceptance")`
- ✅ **Emplacement**: `src/test/resources/features/` + `src/test/java/xvision/acceptance/`

---

## 🎨 Style AAA (Arrange-Act-Assert)

### Principe

Chaque test doit suivre 3 sections claires:

```java
@Test
void shouldDoSomething_whenCondition() {
    // ========== ARRANGE (Préparer) ==========
    // Initialiser les données, mocks, état initial
    double nouveauZoom = 2.5;
    PerspectiveLeaf perspective = new PerspectiveLeaf(1.0, 0, 0);

    // ========== ACT (Agir) ==========
    // Exécuter l'action à tester
    perspective.setZoom(nouveauZoom);

    // ========== ASSERT (Vérifier) ==========
    // Vérifier le résultat
    assertEquals(nouveauZoom, perspective.getZoom(), 0.001,
        "Le zoom devrait être mis à jour");
}
```

### Règles AAA

✅ **DO**: Séparer visuellement les 3 sections (commentaires ou lignes vides)
✅ **DO**: Une seule action dans Act (un seul appel de méthode testé)
✅ **DO**: Assertions claires avec messages explicatifs
❌ **DON'T**: Mélanger les sections (ex: assertion au milieu de l'arrange)
❌ **DON'T**: Plusieurs Act dans un même test (créer plusieurs tests)

---

## 🏷️ Nommage des tests

### Convention: `should...When...`

**Format**:
```java
@Test
void should<RésultatAttendu>_when<Condition>() {
    // ...
}
```

**Exemples**:
```java
// ✅ BON
void shouldUpdateZoom_whenSetZoomCalled()
void shouldNotifyObservers_whenZoomChanges()
void shouldRestorePreviousState_whenUndoCalled()
void shouldBeIdempotent_whenExecuteUndoExecuteCalled()

// ❌ MAUVAIS (pas assez explicite)
void testZoom()
void test1()
void zoomFonctionne()
```

### Règles de nommage

✅ **DO**: Commencer par `should`
✅ **DO**: Utiliser des verbes d'action (update, notify, restore, return, throw)
✅ **DO**: Inclure la condition (`when...`) pour clarifier le contexte
✅ **DO**: Utiliser camelCase (convention Java)
❌ **DON'T**: Noms génériques (`test1`, `testA`)
❌ **DON'T**: Abréviations obscures

### DisplayName (optionnel, pour lisibilité dans rapports)

```java
@Test
@DisplayName("Devrait modifier le zoom quand setZoom() est appelé")
void shouldUpdateZoom_whenSetZoomCalled() {
    // ...
}
```

---

## 🏷️ Tags JUnit

### Tags disponibles

```java
@Tag("unit")        // Tests unitaires
@Tag("integration") // Tests d'intégration
@Tag("e2e")         // Tests end-to-end
@Tag("acceptance")  // Tests d'acceptation (BDD)
```

### Exécution sélective

```bash
# Tous les tests
./gradlew test

# Seulement unitaires (rapide)
./gradlew testUnit

# Seulement intégration
./gradlew testIntegration

# Seulement E2E (lent)
./gradlew testE2E

# Pyramide complète dans l'ordre
./gradlew testPyramid
```

### Règle

**TOUJOURS** taguer vos tests avec un et un seul tag parmi: `unit`, `integration`, `e2e`, `acceptance`.

---

## 🎭 Utilisation de Mockito

### Pourquoi des mocks?

**Problème**: Tester `PerspectiveLeaf` avec de vraies vues couplé le test à l'implémentation des vues.

**Solution**: Mocker les observateurs (vues) pour tester seulement le mécanisme de notification.

### Créer un mock

```java
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

class MonTest {
    @Mock
    private ObserverIF observateurMock;

    @BeforeEach
    void setUp() {
        // Initialiser les mocks
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldNotifyObserver_whenModelChanges() {
        // Arrange
        perspective.attacher(observateurMock);

        // Act
        perspective.setZoom(2.0);

        // Assert: Vérifier que mettreAJour() a été appelé
        verify(observateurMock, times(1)).mettreAJour();
    }
}
```

### Vérifications courantes

```java
// Appelé exactement 1 fois
verify(mock, times(1)).methode();

// Appelé au moins 1 fois
verify(mock, atLeastOnce()).methode();

// JAMAIS appelé
verify(mock, never()).methode();

// Appelé avec un argument spécifique
verify(mock).methode(eq(2.5));

// Appelé avec n'importe quel argument
verify(mock).methode(any());
```

### Quand utiliser des mocks?

✅ **Utiliser mocks pour**:
- Vues (ObserverIF) dans tests du modèle
- IO (fichiers, réseau) dans tests de persistance
- Dépendances lentes ou complexes

❌ **NE PAS mocker**:
- Classes du modèle dans tests unitaires du modèle (utiliser vraies instances)
- Commandes dans tests d'intégration (but = tester interaction réelle)
- Tout dans tests E2E (but = tester système complet)

---

## ⚙️ Setup/Teardown

### Annotations

```java
@BeforeEach  // Exécuté AVANT chaque @Test
@AfterEach   // Exécuté APRÈS chaque @Test
@BeforeAll   // Exécuté UNE FOIS avant tous les tests (méthode static)
@AfterAll    // Exécuté UNE FOIS après tous les tests (méthode static)
```

### Exemple

```java
class MonTest {
    private PerspectiveLeaf perspective;

    @BeforeEach
    void setUp() {
        // Créer une perspective fraîche pour CHAQUE test
        perspective = new PerspectiveLeaf(1.0, 0, 0);
    }

    @AfterEach
    void tearDown() {
        // Nettoyer (optionnel)
        perspective = null;
    }

    @Test
    void test1() {
        // Utiliser perspective
    }

    @Test
    void test2() {
        // Utiliser une NOUVELLE perspective (indépendante de test1)
    }
}
```

### Règles

✅ **DO**: Utiliser `@BeforeEach` pour isoler les tests (état initial identique)
✅ **DO**: Nettoyer les singletons dans `@AfterEach` (ex: `GestionnaireCommandes.clear()`)
✅ **DO**: Nettoyer fichiers temporaires dans `@AfterEach`
❌ **DON'T**: Partager l'état entre tests (réutiliser la même instance)

---

## ✔️ Assertions

### Assertions courantes (JUnit 5)

```java
import static org.junit.jupiter.api.Assertions.*;

// Égalité
assertEquals(expected, actual, "Message si échec");
assertEquals(2.5, zoom, 0.001, "Tolérance pour doubles");

// Booléens
assertTrue(condition, "Message");
assertFalse(condition, "Message");

// Null
assertNull(objet, "Message");
assertNotNull(objet, "Message");

// Même instance (identité)
assertSame(obj1, obj2, "Message");
assertNotSame(obj1, obj2, "Message");

// Exceptions
assertThrows(IllegalArgumentException.class, () -> {
    methode();
}, "Devrait lancer IllegalArgumentException");

// Timeout
assertTimeout(Duration.ofMillis(100), () -> {
    methode(); // Doit finir en <100ms
});
```

### Assertions AssertJ (optionnel, plus fluide)

```java
import static org.assertj.core.api.Assertions.*;

// Chaînage fluide
assertThat(zoom)
    .isEqualTo(2.5)
    .isGreaterThan(0)
    .isLessThan(10);

// Collections
assertThat(list)
    .hasSize(3)
    .contains("element1", "element2")
    .doesNotContain("element3");
```

### Règles

✅ **DO**: Toujours inclure un message explicatif
✅ **DO**: Utiliser `DELTA` pour comparaison de doubles
✅ **DO**: Une assertion par test (idéalement), ou assertions liées
❌ **DON'T**: Assertions sans message (difficulté de diagnostic)
❌ **DON'T**: Trop d'assertions dans un test (diviser en plusieurs tests)

---

## 🔢 Tests paramétrés

### Cas d'usage

Tester la même logique avec plusieurs valeurs d'entrée.

### Exemple

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ParameterizedTest
@ValueSource(doubles = {0.5, 1.0, 2.0, 5.0, 10.0})
@DisplayName("Devrait accepter différentes valeurs de zoom valides")
void shouldAcceptValidZoomValues(double zoomValide) {
    // Act
    perspective.setZoom(zoomValide);

    // Assert
    assertEquals(zoomValide, perspective.getZoom(), 0.001);
}
```

**Résultat**: 5 tests exécutés (un par valeur).

### Autres sources de données

```java
// CSV
@ParameterizedTest
@CsvSource({
    "1.0, 0, 0",
    "2.5, 100, 50",
    "5.0, 200, 100"
})
void shouldCreatePerspective(double zoom, double x, double y) {
    // ...
}

// Méthode
@ParameterizedTest
@MethodSource("zoomProvider")
void shouldAcceptZoom(double zoom) {
    // ...
}

static Stream<Double> zoomProvider() {
    return Stream.of(0.5, 1.0, 2.0, 5.0, 10.0);
}
```

---

## ❌ Anti-patterns à éviter

### 1. Tests dépendants

**❌ MAUVAIS**:
```java
class MauvaisTest {
    private static double zoom; // État partagé!

    @Test
    void test1() {
        zoom = 2.0; // Modifie état global
    }

    @Test
    void test2() {
        assertEquals(2.0, zoom); // Dépend de test1!
    }
}
```

**✅ BON**:
```java
class BonTest {
    @Test
    void test1() {
        double zoom = 2.0; // État local
        assertEquals(2.0, zoom);
    }

    @Test
    void test2() {
        double zoom = 2.0; // Indépendant de test1
        assertEquals(2.0, zoom);
    }
}
```

### 2. Tests qui testent la bibliothèque

**❌ MAUVAIS**:
```java
@Test
void shouldSerializeToJSON() {
    String json = new Gson().toJson(perspective);
    assertNotNull(json); // Teste Gson, pas notre code!
}
```

**✅ BON**:
```java
@Test
void shouldExportPerspectiveWithCorrectFormat() {
    String json = perspective.exporterEnJSON();
    assertTrue(json.contains("\"zoom\":"));
    assertTrue(json.contains("\"x\":"));
    // Teste NOTRE logique d'export
}
```

### 3. Tests trop longs

**❌ MAUVAIS**:
```java
@Test
void testEverything() { // 50 lignes, 20 assertions
    // Teste création, modification, undo, redo, persistance...
}
```

**✅ BON**:
```java
@Test
void shouldCreate() { /* ... */ }

@Test
void shouldModify() { /* ... */ }

@Test
void shouldUndo() { /* ... */ }
// etc.
```

### 4. Assertions sur détails d'implémentation

**❌ MAUVAIS**:
```java
@Test
void shouldCallNotifierInternally() {
    // Teste qu'une méthode privée est appelée (boîte blanche)
    verify(perspective, times(1)).notifier(); // notifier() est privée!
}
```

**✅ BON**:
```java
@Test
void shouldNotifyObservers_whenZoomChanges() {
    // Teste le comportement observable (boîte noire)
    verify(observateur).mettreAJour();
}
```

---

## ✅ Checklist avant commit

Avant de committer des tests, vérifier:

- [ ] Tous les tests passent (`./gradlew test` = SUCCESS)
- [ ] Les tests sont taggués (`@Tag("unit")`, `@Tag("integration")`, etc.)
- [ ] Style AAA respecté (Arrange-Act-Assert)
- [ ] Nommage explicite (`should...When...`)
- [ ] Messages d'assertions clairs
- [ ] Pas de dépendances entre tests (indépendance)
- [ ] Setup/Teardown utilisé pour isolation
- [ ] Mocks utilisés uniquement pour dépendances externes
- [ ] Pas de tests de bibliothèques tierces
- [ ] Couverture ≥80% modèle, ≥70% contrôleurs (`./gradlew jacocoTestReport`)

---

## 📚 Ressources

- **JUnit 5 User Guide**: https://junit.org/junit5/docs/current/user-guide/
- **Mockito Documentation**: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- **AssertJ**: https://assertj.github.io/doc/
- **Martin Fowler - Test Pyramid**: https://martinfowler.com/bliki/TestPyramid.html
- **Martin Fowler - Mocks Aren't Stubs**: https://martinfowler.com/articles/mocksArentStubs.html

---

## 👥 Auteurs

**Équipe X-Vision** — TP2 INF1163
- Antoine Mailhot
- Micaël Racine-Leblanc
