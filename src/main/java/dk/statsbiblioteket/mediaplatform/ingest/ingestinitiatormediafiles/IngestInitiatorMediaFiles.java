package dk.statsbiblioteket.mediaplatform.ingest.ingestinitiatormediafiles;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import dk.statsbiblioteket.mediaplatform.ingest.model.ChannelArchiveRequest;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchiveRequestServiceIF;

public class IngestInitiatorMediaFiles {

    private static final String YOUSEE_RECORDINGS_DAYS_TO_KEEP_KEY = "yousee.recordings.days.to.keep";
    private Logger log;
    private Properties properties;
    private ChannelArchiveRequestServiceIF channelArchiveRequestService;
    private PrintWriter outputPrintWriter;

    public IngestInitiatorMediaFiles(Logger log, Properties properties, ChannelArchiveRequestServiceIF channelArchiveRequestDAO, PrintWriter outputPrintWriter) {
        super();
        if (log == null) {
            throw new RuntimeException("No logger supplied to constructor.");
        }
        if (properties == null) {
            throw new RuntimeException("No properties supplied to constructor.");
        }
        if (channelArchiveRequestDAO == null) {
            throw new RuntimeException("No ChannelArchiveRequestDAOIF supplied to constructor.");
        }
        if (outputPrintWriter == null) {
            throw new RuntimeException("No output writer supplied to constructor.");
        }
        this.log = log;
        this.properties = properties;
        this.channelArchiveRequestService = channelArchiveRequestDAO;
        this.outputPrintWriter = outputPrintWriter;
    }

    public void initiateMediaFileIngest(DateTime dateOfIngest) throws MissingPropertyException {
        log.info("Initiated ingest based on date: " + dateOfIngest);
        /*
        if (dateOfIngest == null) {
            log.error("Input date was null: " + dateOfIngest);
            throw new IllegalArgumentException("Input date cannot be null.");
        }
        String daysYouSeeKeepsRecordingsString = properties.getProperty(YOUSEE_RECORDINGS_DAYS_TO_KEEP_KEY);
        if (daysYouSeeKeepsRecordingsString == null) {
            throw new MissingPropertyException("Missing property with key: " + YOUSEE_RECORDINGS_DAYS_TO_KEEP_KEY);
        }
        int daysYouSeeKeepsRecordings = Integer.parseInt(daysYouSeeKeepsRecordingsString);
        DateTime toDate = dateOfIngest;
        DateTime fromDate = dateOfIngest.minusDays(daysYouSeeKeepsRecordings);
        List<ChannelArchiveRequest> requests = channelArchiveRequestService.getValidRequests(fromDate.toDate(), toDate.toDate());
        */
        
        List<IngestMediaFileJobParameters> outputList = new ArrayList<IngestMediaFileJobParameters>();
        
        outputResult(outputList);
        log.info("Done initiating ingest based on date: " + dateOfIngest);
    }

    public void outputResult(List<IngestMediaFileJobParameters> outputList) {
        String testOutput = 
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
        log.debug("Writing output: " + testOutput);
        outputPrintWriter.write(testOutput);
        outputPrintWriter.close();
    }
}
