#!/bin/bash
set -e  # Stop bij fouten

# === CONFIGURATIE ===
PROJECT_DIR="/craftmanship/craftmanship"
IMAGE_NAME="craftmanship-app"
CONTAINER_NAME="craftmanship"
PORT=7100

echo "🚀 Startscript gestart..."

cd "$PROJECT_DIR"

# === Docker multi-stage build (bevat Maven build in Dockerfile) ===
echo "🐳 Building Docker image (multi-stage build, Maven draait tijdens docker build)..."
if docker build -t "$IMAGE_NAME" .; then
  echo "Docker build geslaagd"
else
  echo "Docker build faalde zonder sudo — poging met sudo"
  sudo docker build -t "$IMAGE_NAME" .
fi

# === Stop oude container (indien aanwezig) ===
if [ -n "$(docker ps -q -f name=$CONTAINER_NAME 2>/dev/null)" ]; then
  echo "🛑 Stopping old container..."
  docker stop "$CONTAINER_NAME" 2>/dev/null || sudo docker stop "$CONTAINER_NAME"
fi

if [ -n "$(docker ps -aq -f name=$CONTAINER_NAME 2>/dev/null)" ]; then
  echo "🧹 Removing old container..."
  docker rm "$CONTAINER_NAME" 2>/dev/null || sudo docker rm "$CONTAINER_NAME"
fi

# === Start nieuwe container ===
echo "▶️ Starting new container..."
if docker run -d --name "$CONTAINER_NAME" -p "$PORT:$PORT" "$IMAGE_NAME"; then
  echo "Container gestart (zonder sudo)"
else
  echo "Proberen te starten met sudo"
  sudo docker run -d --name "$CONTAINER_NAME" -p "$PORT:$PORT" "$IMAGE_NAME"
fi

echo "✅ Nieuwe container draait op poort $PORT!"