set -e

echo "Building common-lib..."
docker build -t common-lib ./services/common-lib

echo "Starting all services..."
docker compose up -d --build