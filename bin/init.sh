#!/bin/bash

export MREPO="${HOME}/.m2/repository"
java -jar \
  "${MREPO}/org/liquibase/liquibase-core/3.4.2/liquibase-core-3.4.2.jar" \
  --changeLogFile="uk/org/kano/insuranceportal/dbchangelog.xml" \
  --classpath="facebook-login-ear/target/facebook-login-ear-1.0.ear:${MREPO}/com/h2database/h2/1.4.192/h2-1.4.192.jar" \
  --driver="org.h2.Driver" \
  --url="jdbc:h2:tcp://localhost/social" \
  --username="admin" --password="admin" \
  update
