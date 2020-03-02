#!/usr/bin/env bash

# Usage: ./set_version.sh
#
# Replaces instances of `<version>LOCAL-SNAPSHOT</version>` in pom.xml files with version number from commit tag. The
# commit tag is sent as an environment variable from CircleCI (CIRCLE_TAG).
#
# If no valid commit tag is set, the script will do nothing.

set -o errexit

RELEASE_TAG_REGEX='^release-(.+)$'
if [[ $CIRCLE_TAG =~ $RELEASE_TAG_REGEX ]]; then
    RELEASE_NUM="${BASH_REMATCH[1]}"
    echo "Setting project version to $RELEASE_NUM"
    if [[ `uname` == "Linux" ]]; then
      find . -name pom.xml -not -path "*/target/*" -exec sed -i "s#<version>LOCAL-SNAPSHOT</version>#<version>${RELEASE_NUM}</version>#g" {} \;
    else
      find . -name pom.xml -not -path "*/target/*" -exec sed -i '' "s#<version>LOCAL-SNAPSHOT</version>#<version>${RELEASE_NUM}</version>#g" {} \;
    fi
else
    echo "Commit tag '$CIRCLE_TAG' did not match expected format. LOCAL-SNAPSHOT will be used."
fi
