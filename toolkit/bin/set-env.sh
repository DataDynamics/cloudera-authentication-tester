#!/bin/sh

BASE_DIR=$(realpath ../)
LIB_DIR=${BASE_DIR}/lib
CONF_DIR=${BASE_DIR}/conf

JARS=$(find "$LIB_DIR" -maxdepth 1 -name "*.jar" -printf "%p:" | sed 's/:$//')

echo "CLASSPATH: $JARS"
