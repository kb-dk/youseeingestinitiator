package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import java.io.File;
import java.io.OutputStream;
import java.util.Properties;

import dk.statsbiblioteket.mediaplatform.ingest.model.persistence.HibernateUtil;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchiveRequestService;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchiveRequestServiceIF;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.YouSeeChannelMappingService;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.YouSeeChannelMappingServiceIF;

public class IngestMediaFilesInitiatorFactory {

    private static final String HIBERNATE_CONFIG_FILE_PATH_KEY = "hibernate.config.file.path";

    /**
     * Creates instance of IngestInitiatorMediaFiles with db access and an output stream
     * that is directed to stdout
     * 
     * @param properties
     * @return
     */
    public static IngestMediaFilesInitiator create(Properties properties) {
        String hibernateConfigFilePath = properties.getProperty(HIBERNATE_CONFIG_FILE_PATH_KEY);
        if (hibernateConfigFilePath == null) {
            throw new RuntimeException("No entry found for property with key: " + HIBERNATE_CONFIG_FILE_PATH_KEY);
        }
        File cfgFile = new File(hibernateConfigFilePath);
        HibernateUtil.initialiseFactory(cfgFile);
        OutputStream outputStream = System.out;
        ChannelArchiveRequestServiceIF channelArchiveRequestService = new ChannelArchiveRequestService();
        YouSeeChannelMappingServiceIF youSeeChannelMappingService = new YouSeeChannelMappingService();
        IngestMediaFilesInitiator ingestInitiatorMediaFiles = new IngestMediaFilesInitiator(properties, channelArchiveRequestService, youSeeChannelMappingService, outputStream);
        return ingestInitiatorMediaFiles;
    }

}
