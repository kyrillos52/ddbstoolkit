#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
	openssl aes-256-cbc -K $encrypted_a53a042b3b62_key -iv $encrypted_a53a042b3b62_iv -in cd/codesigning.asc.enc -out cd/codesigning.asc -d
	gpg --fast-import cd/codesigning.asc
	gpg --keyserver hkp://keyserver.ubuntu.com --send-keys 534f7c1a7eb0a0e4
fi
