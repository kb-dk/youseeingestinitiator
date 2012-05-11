package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.IngestMediaFilesInitiator;
import dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.MissingPropertyException;
import dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.mock.ChannelArchiveRequestServiceStub;
import dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.mock.YouSeeChannelMappingServiceStub;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchiveRequestServiceIF;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.YouSeeChannelMappingServiceIF;

public class IngestMediaFilesInitiatorTest {

    private Level testLogLevel = Level.DEBUG;
    private Properties properties;
    private Logger log = null;

    public IngestMediaFilesInitiatorTest() {
        properties = new Properties();
        properties.setProperty("yousee.recordings.days.to.keep", "28");

        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
        System.out.println("Log level set to: " + testLogLevel);
        Logger.getRootLogger().setLevel(testLogLevel);
        log = Logger.getLogger(IngestMediaFilesInitiator.class);
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void simpleInitiatetest() throws IOException, MissingPropertyException {
        PipedWriter pipedWriter = new PipedWriter();
        BufferedReader reader = new BufferedReader(new PipedReader(pipedWriter));
        PrintWriter outputPrintWriter = new PrintWriter(pipedWriter);
        IngestMediaFilesInitiator initiator = createIngestInitiatorMediaFilesStub(properties, outputPrintWriter);

        DateTime inputDate = new DateTime(2009, 1, 1, 0, 0, 0, 0);
        initiator.start(inputDate);

        String actual = getOutput(reader);
        String expected = 
                " {\n"
                + "     \"downloads\":[\n"
                + "         {\n"
                + "            \"fileID\" : \"DR HD_20120915_100000_20120915_110000.mux\",\n"
                + "            \"startTime\" : \"20120915100000\",\n"
                + "            \"endTime\" : \"20120915110000\",\n"
                + "            \"youseeChannelID\" : \"DR HD\",\n"
                + "            \"sbChannelID\" : \"drhd\"\n"
                + "         },\n"
                + "         {\n"
                + "            \"fileID\" : \"DR HD_20120915_110000_20120915_120000.mux\",\n"
                + "            \"startTime\" : \"20120915110000\",\n"
                + "            \"endTime\" : \"20120915120000\",\n"
                + "            \"youseeChannelID\" : \"DR HD\",\n"
                + "            \"sbChannelID\" : \"drhd\"\n"
                + "         }\n"
                + "     ]\n"
                + " }\n";
        assertEquals(expected, actual);
    }

    public String getOutput(BufferedReader reader) throws IOException {
        log.debug("Parsing");
        String actual = "";
        String line = reader.readLine();
        while (line != null) {
            log.debug("Adding line: " + line);
            actual += line + "\n";
            line = reader.readLine();
        }
        return actual;
    }

    private static IngestMediaFilesInitiator createIngestInitiatorMediaFilesStub(Properties properties, PrintWriter outputPrintWriter) {
        ChannelArchiveRequestServiceIF channelArchiveRequestService = new ChannelArchiveRequestServiceStub();
        YouSeeChannelMappingServiceIF youSeeChannelMappingService = new YouSeeChannelMappingServiceStub();
        Logger log = Logger.getLogger(IngestMediaFilesInitiator.class);
        IngestMediaFilesInitiator ingestInitiatorMediaFiles = new IngestMediaFilesInitiator(properties, channelArchiveRequestService, youSeeChannelMappingService, outputPrintWriter);
        return ingestInitiatorMediaFiles;
    }
}
