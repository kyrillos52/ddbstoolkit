#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_3222f085f910_key -iv $encrypted_3222f085f910_iv -in cd/codesigning.asc.enc -out cd/codesigning.asc.asc -d
    gpg --fast-import cd/codesigning.asc
fi
