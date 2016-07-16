#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
	curl https://www.jpm4j.org/install/script | sh
	jpm install com.codacy:codacy-coverage-reporter:assembly
	export CODACY_PROJECT_TOKEN=$CODACY_PROJECT_TOKEN
	codacy-coverage-reporter -l Java -r coverage.xml
fi
