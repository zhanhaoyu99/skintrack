#!/bin/bash
# SkinTrack Dev Setup — 安装 app-dev skill 到 ~/.claude/skills/app-dev/
# 适用于 WSL / Linux / Mac

set -e

SKILL_DIR="$HOME/.claude/skills/app-dev"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "Installing app-dev skill..."
mkdir -p "$SKILL_DIR/knowledge" "$SKILL_DIR/workflows" "$SKILL_DIR/templates"
cp -r "$SCRIPT_DIR/skill/"* "$SKILL_DIR/"

echo "✓ Skill installed to $SKILL_DIR"
echo ""
echo "File count: $(find "$SKILL_DIR" -type f | wc -l)"
echo "Ready to use /app commands."
