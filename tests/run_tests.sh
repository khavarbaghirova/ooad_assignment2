#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_DIR="${TMPDIR:-/tmp}/ring-buffer-unit-tests"

rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"

cd "$ROOT_DIR"

javac -d "$BUILD_DIR" \
  RingBuffer.java \
  Reader.java \
  Writer.java \
  tests/RingBufferUnitTests.java

java -cp "$BUILD_DIR" RingBufferUnitTests
