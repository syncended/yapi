#!/usr/bin/env sh
# Build and launch yapi attached to the current TTY.
#
# `./gradlew :cli:run` cannot host the raw-mode TUI because the Gradle daemon
# pipes the child's stdio. This script delegates compilation to Gradle but
# execs the install-dist start script directly so the JVM inherits the shell's
# real terminal.
set -e

cd "$(dirname "$0")"

./gradlew --quiet :cli:installDist
exec ./cli/build/install/cli/bin/cli "$@"
