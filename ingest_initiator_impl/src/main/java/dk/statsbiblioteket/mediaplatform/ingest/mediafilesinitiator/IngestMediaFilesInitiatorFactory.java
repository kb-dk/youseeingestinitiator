package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import java.io.File;
import java.io.OutputStream;
import java.util.Properties;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.stub.ChannelArchiveRequestServiceTmpStub;
import dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.stub.YouSeeChannelMappingServiceTmpStub;
import dk.statsbiblioteket.mediaplatform.ingest.model.persistence.ChannelArchivingRequesterHibernateUtil;
import dk.statsbiblioteket.mediaplatform.ingest.model.persistence.HibernateUtil;
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
        setupLog4j(properties);
        setupHibernate(properties);
        OutputStream outputStream = System.out;
        ChannelArchiveRequestServiceIF channelArchiveRequestService = new ChannelArchiveRequestService();
        YouSeeChannelMappingServiceIF youSeeChannelMappingService = new YouSeeChannelMappingService();
        //ChannelArchiveRequestServiceIF channelArchiveRequestService = new ChannelArchiveRequestServiceTmpStub();
        //YouSeeChannelMappingServiceIF youSeeChannelMappingService = new YouSeeChannelMappingServiceTmpStub();
        WorkFlowStateMonitorFacade workFlowStateMonitorFacade = new WorkFlowStateMonitorWebServiceFacade(properties);
        IngestMediaFilesInitiator ingestInitiatorMediaFiles = new IngestMediaFilesInitiator(properties, channelArchiveRequestService, youSeeChannelMappingService, workFlowStateMonitorFacade, outputStream);
        return ingestInitiatorMediaFiles;
    }


    protected static void setupLog4j(Properties properties)
            throws FactoryConfigurationError {
        String log4jConfigFilePath = properties.getProperty(LOG4J_CONFIG_FILE_PATH_KEY);
        if (log4jConfigFilePath == null) {
            throw new RuntimeException("Missing property: " + LOG4J_CONFIG_FILE_PATH_KEY);
        }
        DOMConfigurator.configure(log4jConfigFilePath);
        log.debug("Log4j property file: " + log4jConfigFilePath);
    }


    protected static void setupHibernate(Properties properties) {
        String hibernateConfigFilePath = properties.getProperty(HIBERNATE_CONFIG_FILE_PATH_KEY);
        if (hibernateConfigFilePath == null) {
            throw new RuntimeException("Missing property: " + HIBERNATE_CONFIG_FILE_PATH_KEY);
        }
        File hibernateConfigFile = new File(hibernateConfigFilePath);
        log.debug("Hibernate config file: " + hibernateConfigFile.getAbsolutePath());
        ChannelArchivingRequesterHibernateUtil.initialiseFactory(hibernateConfigFile); 
        //HibernateUtil.initialiseFactory(hibernateConfigFile);
    }

}
