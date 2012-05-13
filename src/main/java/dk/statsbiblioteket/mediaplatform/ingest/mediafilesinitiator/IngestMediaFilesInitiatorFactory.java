package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import java.io.OutputStream;
import java.util.Properties;

import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchiveRequestServiceIF;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.YouSeeChannelMappingServiceIF;

public class IngestMediaFilesInitiatorFactory {

    //private static final String HIBERNATE_CONFIG_FILE_PATH_KEY = "hibernate.config.file.path";

    /**
     * Creates instance of IngestInitiatorMediaFiles with db access
     * 
     * @param properties
     * @return
     * @throws MissingPropertyException If property is missing
     */
    public static IngestMediaFilesInitiator create(Properties properties) {
        /*
        String hibernateConfigFilePath = properties.getProperty(HIBERNATE_CONFIG_FILE_PATH_KEY);
        if (hibernateConfigFilePath == null) {
            throw new MissingPropertyException("No entry found for property with key: " + HIBERNATE_CONFIG_FILE_PATH_KEY);
        }
        File cfgFile = new File(hibernateConfigFilePath);
        HibernateUtil.initialiseFactory(cfgFile);
        ChannelArchiveRequestDAOIF channelArchiveRequestDAO = new ChannelArchiveRequestDAO();
        Logger log = Logger.getLogger(IngestInitiatorMediaFiles.class);
        */
        
        OutputStream outputStream = System.out;
        ChannelArchiveRequestServiceIF channelArchiveRequestService = null;//new ChannelArchiveRequestService();
        YouSeeChannelMappingServiceIF youSeeChannelMappingService = null;//new YouSeeChannelMappingService();
        IngestMediaFilesInitiator ingestInitiatorMediaFiles = new IngestMediaFilesInitiator(properties, channelArchiveRequestService, youSeeChannelMappingService, outputStream);
        return ingestInitiatorMediaFiles;
    }

}
