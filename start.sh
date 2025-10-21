#!/bin/bash
set -e  # Stop bij fouten

echo "🚀 Startscript gestart..."

cd /craftmanship/craftmanship

echo "🐳 Building and starting with Docker Compose..."
sudo docker compose up -d --build
