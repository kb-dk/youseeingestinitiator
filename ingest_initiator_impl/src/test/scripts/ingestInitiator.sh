#!/bin/bash

POM_FILE=/Users/henningbottger/projects/yousee/dev/ingest_component/ingest_initiator_media_file/pom.xml
CONFIGFILE=/Users/henningbottger/projects/yousee/dev/ingest_component/ingest_initiator_media_file/src/test/config/ingest_initiator_media_files.properties
INPUT=2012-03-28

VERSION=$(grep -A 1 "<artifactId>ingest_initiator_media_files</artifactId>" "$POM_FILE" | tail -n 1 | grep -o [0-9].* | grep -o "^[^<]*")
echo Starting version: $VERSION
java  -cp ingest_initiator_media_files-${VERSION}-libs/ingest_initiator_media_files-${VERSION}/ingest_initiator_media_files-*.jar:ingest_initiator_media_files-${VERSION}-libs/ingest_initiator_media_files-${VERSION}/* \
dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.IngestMediaFilesInitiatorCLI \
$CONFIGFILE $INPUT