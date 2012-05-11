package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import dk.statsbiblioteket.mediaplatform.ingest.model.ChannelArchiveRequest;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchiveRequestServiceIF;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.YouSeeChannelMappingServiceIF;

/**
 * Formålet med denne klasse er at starte et workflow til ingest af nye mediefiler i Medieplatformen.
 * I praksis betyder det at komponenterne BitMag, DOMS og DigiTV bliver beriget med data og metadata
 * om nye filer.
 * 
 * Klassen repræsenterer det første skridt i et workflow, der downloader, karakteriserer og 
 * kvalitetstjekker filerne inden de ingestes i slutsystemerne.
 * 
 * Input til initiatoren er en dato. Initiatoren gennegår derpå følgende skridt:
 * 
 * <ol>
 *   <li>Udled periode som vi ønsker at downloade filer for, eg. nu og 28 dage tilbage</li>
 *   
 *   <li>Hent planlagt optageperioder fra ChannelArchiveRequestService</li>
 *   
 *   <li>Udled hvilke filer der skal downloades ud fra planlagte optageperioder og den periode vi 
 *          ønsker at downloade filer fra</li>
 *          
 *   <li>Udled navne på filer som skal downloades</li>
 *   
 *   <li>Filtrer filer fra som vi allerede har ingested i systemet</li>
 *   
 *   <li>Output ingest job for hver fil der ønskes ingested til stdout</li>
 *   
 * </ol>
 * 
 * @author henningbottger
 *
 */
public class IngestMediaFilesInitiator {

    private static final Logger log = Logger.getLogger(IngestMediaFilesInitiator.class);;
    private static final String YOUSEE_RECORDINGS_DAYS_TO_KEEP_KEY = "yousee.recordings.days.to.keep";
    private Properties properties;
    private ChannelArchiveRequestServiceIF channelArchiveRequestService;
    private YouSeeChannelMappingServiceIF youSeeChannelMappingService;
    private PrintWriter outputPrintWriter;

    public IngestMediaFilesInitiator(Properties properties, ChannelArchiveRequestServiceIF channelArchiveRequestDAO, YouSeeChannelMappingServiceIF youSeeChannelMappingService, PrintWriter outputPrintWriter) {
        super();
        log.debug("Constructing initiator");
        if (properties == null) {
            throw new RuntimeException("No properties supplied to constructor.");
        }
        /*
        if (channelArchiveRequestDAO == null) {
            throw new RuntimeException("No ChannelArchiveRequestDAOIF supplied to constructor.");
        }
        if (outputPrintWriter == null) {
            throw new RuntimeException("No output writer supplied to constructor.");
        }
        */
        this.properties = properties;
        this.channelArchiveRequestService = channelArchiveRequestDAO;
        this.youSeeChannelMappingService = youSeeChannelMappingService;
        this.outputPrintWriter = outputPrintWriter;
        log.debug("Done constructing initiator");
    }

    /**
     * 
     * @param dateOfIngest Last date in download period.
     * @throws NullPointerException if dateOfIngest is null
     */
    public void start(DateTime dateOfIngest) throws MissingPropertyException {
        log.info("Initiated ingest based on date: " + dateOfIngest);
        // Infer period to ingest
        int daysYouSeeKeepsRecordings = extractPropertyInt(YOUSEE_RECORDINGS_DAYS_TO_KEEP_KEY);
        DateTime toDate = dateOfIngest;
        DateTime fromDate = dateOfIngest.minusDays(daysYouSeeKeepsRecordings);
        // Lookup channel recording requests
        //List<ChannelArchiveRequest> requests = channelArchiveRequestService.getValidRequests(fromDate.toDate(), toDate.toDate());
        // Infer files to download
        List<IngestMediaFileJobParameters> unFilteredOutputList = inferFilesToIngest();
        // Filter out already ingested files    
        List<IngestMediaFileJobParameters> outputList = filter(unFilteredOutputList);
        // Output to stdout
        outputResult(outputList);
        log.info("Done initiating ingest based on date: " + dateOfIngest);
    }

    protected List<IngestMediaFileJobParameters> filter(List<IngestMediaFileJobParameters> unFilteredOutputList) {
        // TODO Do real stuff...
        return null;
    }

    protected List<IngestMediaFileJobParameters> inferFilesToIngest() {
        List<IngestMediaFileJobParameters> outputList = new ArrayList<IngestMediaFileJobParameters>();
        // TODO: Do real stuff...
        return outputList;
    }

    private int extractPropertyInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    protected void outputResult(List<IngestMediaFileJobParameters> outputList) {
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
