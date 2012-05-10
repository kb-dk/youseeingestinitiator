package dk.statsbiblioteket.mediaplatform.ingest.ingestinitiatormediafiles;

import java.io.PrintWriter;
import java.util.Properties;

import org.apache.log4j.Logger;

import dk.statsbiblioteket.mediaplatform.ingest.ingestinitiatormediafiles.mock.ChannelArchveRequestServiceMock;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchiveRequestServiceIF;

public class IngestInitiatorMediaFilesTestFactory {

    /**
     * Creates instance of IngestInitiatorMediaFiles for unit test purpose
     * 
     * @return
     * @throws MissingPropertyException If property is missing
     */
    public static IngestInitiatorMediaFiles create(Properties properties, PrintWriter outputPrintWriter) {
        ChannelArchiveRequestServiceIF channelArchiveRequestDAO = new ChannelArchveRequestServiceMock();
        Logger log = Logger.getLogger(IngestInitiatorMediaFiles.class);
        IngestInitiatorMediaFiles ingestInitiatorMediaFiles = new IngestInitiatorMediaFiles(log, properties, channelArchiveRequestDAO, outputPrintWriter);
        return ingestInitiatorMediaFiles;
    }

}
