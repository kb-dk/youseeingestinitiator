#!/bin/bash

JAR_LOCATION=$1
INPUT=$2
#2012-03-28

CONFIGFILE=ingest-initiator/ingest_initiator_media_files.properties

java  -cp ${JAR_LOCATION}/ingest_initiator_impl-*.jar:${JAR_LOCATION}/* \
dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.IngestMediaFilesInitiatorCLI \
$CONFIGFILE $INPUT