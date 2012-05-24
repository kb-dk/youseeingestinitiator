package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import java.io.File;
import java.io.OutputStream;
import java.util.Properties;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import dk.statsbiblioteket.mediaplatform.ingest.model.persistence.ChannelArchivingRequesterHibernateUtil;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchiveRequestService;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchiveRequestServiceIF;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.YouSeeChannelMappingService;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.YouSeeChannelMappingServiceIF;

public class IngestMediaFilesInitiatorFactory {

    private static final String HIBERNATE_CONFIG_FILE_PATH_KEY = "hibernate.config.file.path";
    private static final String LOG4J_CONFIG_FILE_PATH_KEY = "log4j.config.file.path";

    private static final Logger log = Logger.getLogger(IngestMediaFilesInitiatorFactory.class);

    
    /**
     * Creates instance of IngestInitiatorMediaFiles with db access and an output stream
     * that is directed to stdout
     * 
     * @param properties
     * @return
     */
    public static IngestMediaFilesInitiator create(Properties properties) {
        setupLog4j(getPropertyValue(properties, LOG4J_CONFIG_FILE_PATH_KEY));
        setupHibernate(getPropertyValue(properties, HIBERNATE_CONFIG_FILE_PATH_KEY));
        OutputStream outputStream = System.out;
        ChannelArchiveRequestServiceIF channelArchiveRequestService = new ChannelArchiveRequestService();
        YouSeeChannelMappingServiceIF youSeeChannelMappingService = new YouSeeChannelMappingService();
        WorkFlowStateMonitorFacade workFlowStateMonitorFacade = new WorkFlowStateMonitorWebServiceFacade(properties);
        IngestMediaFilesInitiator ingestInitiatorMediaFiles = new IngestMediaFilesInitiator(properties, channelArchiveRequestService, youSeeChannelMappingService, workFlowStateMonitorFacade, outputStream);
        return ingestInitiatorMediaFiles;
    }


    protected static void setupLog4j(String log4jConfigFilePath) throws FactoryConfigurationError {
        DOMConfigurator.configure(log4jConfigFilePath);
        log.debug("Log4j property file: " + log4jConfigFilePath);
    }


    protected static void setupHibernate(String hibernateConfigFilePath) {
        File hibernateConfigFile = new File(hibernateConfigFilePath);
        log.debug("Hibernate config file: " + hibernateConfigFile.getAbsolutePath());
        ChannelArchivingRequesterHibernateUtil.initialiseFactory(hibernateConfigFile); 
    }


    protected static String getPropertyValue(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Missing property: " + key);
        }
        return value;
    }

}
