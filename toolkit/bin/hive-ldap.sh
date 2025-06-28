#!/bin/sh

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

source "$SCRIPT_DIR/set-env.sh"

java -classpath $JARS -Dconf.dir=${CONF_DIR} io.datadynamics.hive.ldap.LdapTester $@