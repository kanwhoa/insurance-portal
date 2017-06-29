#!/bin/bash

export WILDFLY_HOME="/Users/timh/Apps/wildfly-10.1.0.Final"
export H2VERSION="1.3.173"

java -cp "${WILDFLY_HOME}/modules/system/layers/base/com/h2database/h2/main/h2-${H2VERSION}.jar" \
  org.h2.tools.Server \
  -baseDir /tmp/h2
