#!/bin/sh
set -ev

COMMIT_HASH="$(git log --pretty=format:%H -n 1)"
if [ -z "${COMMIT_HASH}" ]; then
  echo "Could not get current commit hash from git?"
  exit 1
fi

# Setup the submodule properly.
rm -r build/dokka
git submodule update --init
pushd build/dokka

# Reattach head onto a branch
git checkout gh-pages

# Remove all existing files - dokka will rebuild!
git rm -r .
popd

# Build documentation
./gradlew :dokkaHtml

# Commit the change
pushd build/dokka
git add .
# This step will fail if there is no data change
git commit -m "Update documentation to ${COMMIT_HASH}"

# Push it up.
git push
popd

# Stage a commit for the submodule reference
git add -f build/dokka
