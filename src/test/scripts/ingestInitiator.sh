#!/bin/bash

CONFIGFILE=$1
INPUT=$2

java  -cp ingest_initiator_media_files-0.0.1-SNAPSHOT-libs/ingest_initiator_media_files-0.0.1-SNAPSHOT/ingest_initiator_media_files-*.jar:ingest_initiator_media_files-0.0.1-SNAPSHOT-libs/ingest_initiator_media_files-0.0.1-SNAPSHOT/* \
dk.statsbiblioteket.mediaplatform.ingest.ingestinitiatormediafiles.IngestInitiatorMediaFilesCLI \
$CONFIGFILE $INPUT