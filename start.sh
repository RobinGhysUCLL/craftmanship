#!/bin/bash
set -e

echo "🚀 Startscript gestart..."

cd /craftmanship/craftmanship
echo "📁 Huidige map: $(pwd)"
ls -a | grep env || echo "⚠️ Geen .env gevonden in $(pwd)"

echo "🐳 Building and starting with Docker Compose..."
docker compose --env-file .env down
docker compose --env-file .env up -d --build
