#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR"

echo "=== SkinTrack Deployment ==="
echo "Working directory: $PROJECT_DIR"
echo ""

# Pre-flight checks
command -v docker >/dev/null 2>&1 || { echo "ERROR: docker not found"; exit 1; }
command -v docker compose version >/dev/null 2>&1 || { echo "ERROR: docker compose not found"; exit 1; }

# Check .env file
if [ ! -f .env ]; then
    echo "ERROR: .env file not found. Copy .env.example to .env and fill in values."
    exit 1
fi

# Source .env for shell variable access
set -a
source .env
set +a

# Tag current image for rollback
PREV_IMAGE=$(docker compose images app -q 2>/dev/null || true)
if [ -n "$PREV_IMAGE" ]; then
    echo "Tagging current image for rollback: $PREV_IMAGE"
    docker tag "$PREV_IMAGE" skintrack-app:rollback 2>/dev/null || true
fi

# Pull latest code
echo "[1/5] Pulling latest code..."
git pull --ff-only

# Build images
echo "[2/5] Building Docker images..."
docker compose build --no-cache app

# Stop and restart
echo "[3/5] Restarting services..."
docker compose up -d

# Wait for database to be ready
echo "[4/5] Waiting for database..."
for i in $(seq 1 30); do
    if docker compose exec -T db pg_isready -U "${POSTGRES_USER:-skintrack}" > /dev/null 2>&1; then
        echo "  Database is ready."
        break
    fi
    if [ "$i" -eq 30 ]; then
        echo "ERROR: Database did not become ready in time."
        docker compose logs db
        exit 1
    fi
    sleep 2
done

# Health check
echo "[5/5] Running health check..."
HEALTHY=false
for i in $(seq 1 20); do
    STATUS=$(curl -sf http://localhost/health 2>/dev/null | grep -o '"ok"' || true)
    if [ "$STATUS" = '"ok"' ]; then
        HEALTHY=true
        echo "  Health check passed!"
        echo ""
        echo "=== Deployment complete ==="
        docker compose ps
        break
    fi
    sleep 3
done

if [ "$HEALTHY" = false ]; then
    echo "ERROR: Health check failed after 60 seconds."
    echo "Logs:"
    docker compose logs --tail=50 app

    # Rollback if previous image exists
    if docker image inspect skintrack-app:rollback > /dev/null 2>&1; then
        echo ""
        echo "=== Rolling back to previous version ==="
        docker tag skintrack-app:rollback "$(docker compose config --images | grep app | head -1)" 2>/dev/null || true
        docker compose up -d app
        echo "Rollback initiated. Check health manually."
    fi
    exit 1
fi
