#!/bin/bash

set -e

cd ${TRAVIS_BUILD_DIR}

# Prepare the local keyring (requires travis to have decrypted the file
# beforehand)
gpg --fast-import .travis/secret.gpg

echo "TRAVIS_BRANCH: ${TRAVIS_BRANCH}"
echo "on a tag -> set project version to ${TRAVIS_TAG}"

newVersion=${TRAVIS_TAG}

# Print newVersion
echo "newVersion: ${newVersion}"

if [[ -z "${newVersion}" ]]; then
  echo "missing newVersion value" >&2
  exit 1
fi

# Run the gradle publish steps
./gradlew setVersion -P newVersion=${newVersion} 1>/dev/null 2>/dev/null
./gradlew publish

echo "done !"
