package dk.statsbiblioteket.mediaplatform.ingest.ingestinitiatormediafiles;

import java.io.PrintWriter;
import java.util.Properties;

import org.apache.log4j.Logger;

import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchiveRequestService;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchveRequestServiceIF;

public class IngestInitiatorMediaFilesFactory {

    private static final String HIBERNATE_CONFIG_FILE_PATH_KEY = "hibernate.config.file.path";

    /**
     * Creates instance of IngestInitiatorMediaFiles with db access
     * 
     * @param properties
     * @return
     * @throws MissingPropertyException If property is missing
     */
    public static IngestInitiatorMediaFiles create(Properties properties) throws MissingPropertyException {
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
        
        Logger log = Logger.getLogger(IngestInitiatorMediaFiles.class);
        PrintWriter outputPrintWriter = new PrintWriter(System.out);
        ChannelArchveRequestServiceIF channelArchiveRequestService = new ChannelArchiveRequestService();
        IngestInitiatorMediaFiles ingestInitiatorMediaFiles = new IngestInitiatorMediaFiles(log, properties, channelArchiveRequestService, outputPrintWriter);
        return ingestInitiatorMediaFiles;
    }

}
