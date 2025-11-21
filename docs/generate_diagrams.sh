#!/bin/bash

# Script de génération des diagrammes PlantUML en SVG
# Usage: ./generate_diagrams.sh

echo "🎨 Génération des diagrammes PlantUML..."

# Vérifier si PlantUML est installé
if ! command -v plantuml &> /dev/null; then
    echo "⚠️  PlantUML n'est pas installé."
    echo ""
    echo "Installation rapide:"
    echo "  brew install plantuml"
    echo ""
    echo "Ou utiliser Docker:"
    echo "  docker run --rm -v \$(pwd):/data plantuml/plantuml *.puml"
    exit 1
fi

# Aller dans le dossier docs
cd "$(dirname "$0")"

# Générer tous les diagrammes
echo "Génération en cours..."
plantuml -tsvg *.puml

# Compter les fichiers générés
svg_count=$(ls -1 *.svg 2>/dev/null | wc -l)

echo ""
echo "✅ Terminé! $svg_count diagrammes SVG générés dans:"
echo "   $(pwd)"
echo ""
echo "Diagrammes générés:"
ls -1 *.svg | sed 's/^/  - /'

echo ""
echo "📖 Pour voir les diagrammes:"
echo "  open diagramme_architecture_complete.svg"
echo "  open diagramme_packages.svg"
echo "  open diagramme_patterns_overview.svg"
