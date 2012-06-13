#!/bin/bash
#
# Script to trigger the ingester on the local devel machine.
#


POM_FILE=/Users/henningbottger/projects/yousee/dev/ingest_component/ingest_initiator_media_file/pom.xml
CONFIGFILE=/Users/henningbottger/projects/yousee/dev/ingest_component/ingest_initiator_media_file/ingest_initiator_impl/src/test/config/ingest_initiator_media_files_CLI.properties
INPUT=2012-03-28

VERSION=$(grep -A 1 "<artifactId>ingest_initiator_media_files</artifactId>" "$POM_FILE" | tail -n 1 | grep -o [0-9].* | grep -o "^[^<]*")
echo Starting version: $VERSION

unzip ingest_initiator_bundle/target/ingest_initiator_bundle-${VERSION}-bundle.zip -d ingest_initiator_bundle/target/ingest_initiator_bundle_unzipped

java  -cp ingest_initiator_bundle/target/ingest_initiator_bundle_unzipped/ingest_initiator_impl-*.jar:ingest_initiator_bundle/target/ingest_initiator_bundle_unzipped/* \
dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.IngestMediaFilesInitiatorCLI \
$CONFIGFILE $INPUT