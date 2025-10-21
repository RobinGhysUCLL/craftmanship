#!/bin/bash
set -e  # Stop bij fouten

# === CONFIGURATIE ===
PROJECT_DIR="/craftmanship/craftmanship"
BRANCH="main"
IMAGE_NAME="craftmanship-app"
CONTAINER_NAME="craftmanship"
PORT=7100

echo "🚀 Startscript gestart..."

cd "$PROJECT_DIR"

# === Pull laatste code ===
echo "📥 Pulling latest changes from GitHub..."
git fetch origin "$BRANCH"
git reset --hard "origin/$BRANCH"

# === Maven build ===
echo "🔨 Building project with Maven..."
mvn clean package -DskipTests

# === Docker build ===
echo "🐳 Building Docker image..."
docker build -t "$IMAGE_NAME" .

# === Stop oude container (indien aanwezig) ===
if [ "$(docker ps -q -f name=$CONTAINER_NAME)" ]; then
  echo "🛑 Stopping old container..."
  docker stop "$CONTAINER_NAME"
fi

if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
  echo "🧹 Removing old container..."
  docker rm "$CONTAINER_NAME"
fi

# === Start nieuwe container ===
echo "▶️ Starting new container..."
docker run -d \
  --name "$CONTAINER_NAME" \
  -p "$PORT:$PORT" \
  "$IMAGE_NAME"

echo "✅ Nieuwe container draait op poort $PORT!"

