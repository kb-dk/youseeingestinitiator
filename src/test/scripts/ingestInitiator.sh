#!/bin/bash

CONFIGFILE=/Users/henningbottger/projects/yousee/dev/ingest_component/ingest_initiator_media_file/src/test/config/ingest_initiator_media_files.properties
INPUT=b

java  -cp ingest_initiator_media_files-0.0.2-SNAPSHOT-libs/ingest_initiator_media_files-0.0.2-SNAPSHOT/ingest_initiator_media_files-*.jar:ingest_initiator_media_files-0.0.2-SNAPSHOT-libs/ingest_initiator_media_files-0.0.2-SNAPSHOT/* \
dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.IngestMediaFilesInitiatorCLI \
$CONFIGFILE $INPUT