package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import java.io.File;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.stub.ChannelArchiveRequestServiceStub;
import dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.stub.YouSeeChannelMappingServiceStub;
import dk.statsbiblioteket.mediaplatform.ingest.model.persistence.HibernateUtil;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchiveRequestService;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchiveRequestServiceIF;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.YouSeeChannelMappingService;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.YouSeeChannelMappingServiceIF;

public class IngestMediaFilesInitiatorFactory {

    private static final String HIBERNATE_CONFIG_FILE_PATH_KEY = "hibernate.config.file.path";
    private static final String LOG4J_CONFIG_FILE_PATH_KEY = "log4j.config.file.path";

    
    /**
     * Creates instance of IngestInitiatorMediaFiles with db access and an output stream
     * that is directed to stdout
     * 
     * @param properties
     * @return
     */
    public static IngestMediaFilesInitiator create(Properties properties) {
        // Setup log4J
        String log4jConfigFilePath = properties.getProperty(LOG4J_CONFIG_FILE_PATH_KEY);
        if (log4jConfigFilePath == null) {
            throw new RuntimeException("No entry found for property with key: " + LOG4J_CONFIG_FILE_PATH_KEY);
        }
        DOMConfigurator.configure(log4jConfigFilePath);
        Logger log = Logger.getLogger(IngestMediaFilesInitiatorFactory.class);
        log.debug("Property file: " + log4jConfigFilePath);
        
        // Setup Hibernate
        String hibernateConfigFilePath = properties.getProperty(HIBERNATE_CONFIG_FILE_PATH_KEY);
        if (hibernateConfigFilePath == null) {
            throw new RuntimeException("No entry found for property with key: " + HIBERNATE_CONFIG_FILE_PATH_KEY);
        }
        File hibernateConfigFile = new File(hibernateConfigFilePath);
        log.debug("Hibernate config file: " + hibernateConfigFile.getAbsolutePath());
        HibernateUtil.initialiseFactory(hibernateConfigFile);
        
        // Setup rest
        OutputStream outputStream = System.out;

        ChannelArchiveRequestServiceIF channelArchiveRequestService = new ChannelArchiveRequestService();
        YouSeeChannelMappingServiceIF youSeeChannelMappingService = new YouSeeChannelMappingService();
        //ChannelArchiveRequestServiceIF channelArchiveRequestService = new ChannelArchiveRequestServiceStub();
        //((YouSeeChannelMappingServiceIF youSeeChannelMappingService = new YouSeeChannelMappingServiceStub();
        IngestMediaFilesInitiator ingestInitiatorMediaFiles = new IngestMediaFilesInitiator(properties, channelArchiveRequestService, youSeeChannelMappingService, outputStream);
        return ingestInitiatorMediaFiles;
    }

}
