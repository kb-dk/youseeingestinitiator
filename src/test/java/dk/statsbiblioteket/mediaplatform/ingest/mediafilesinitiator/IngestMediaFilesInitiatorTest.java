package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.IngestMediaFilesInitiator;
import dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.mock.ChannelArchiveRequestServiceStub;
import dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.mock.YouSeeChannelMappingServiceStub;
import dk.statsbiblioteket.mediaplatform.ingest.model.ChannelArchiveRequest;
import dk.statsbiblioteket.mediaplatform.ingest.model.WeekdayCoverage;

public class IngestMediaFilesInitiatorTest {

    private Level testLogLevel = Level.DEBUG;
    private Logger log = null;

    public IngestMediaFilesInitiatorTest() {

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
    public void outputFormatTest() throws IOException {
        ByteArrayOutputStream byteArrayoutputStream = new ByteArrayOutputStream();
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(null, null, null, System.out);//outputPrintWriter);

        List<MediaFileIngestParameters> outputList = new ArrayList<MediaFileIngestParameters>();
        outputList.add(new MediaFileIngestParameters("DR HD_20120915_100000_20120915_110000.mux", "drhd", "DR HD", new DateTime(2012,9,15,10,0,0), new DateTime(2012,9,15,11,0,0)));
        outputList.add(new MediaFileIngestParameters("DR HD_20120915_110000_20120915_120000.mux", "drhd", "DR HD", new DateTime(2012,9,15,11,0,0), new DateTime(2012,9,15,12,0,0)));
        initiator.outputResult(outputList , byteArrayoutputStream);

        String actual = byteArrayoutputStream.toString();
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
        log.debug("Result: " + actual);
    }

    @Test
    public void inferFilesToIngestFilenameTest() throws IOException {
        DateTime dateToCheck = new DateTime(2010, 3, 1, 0, 0, 0, 0); // 2010-03-01 ~ monday, 2010-03-07 ~ sunday
        ChannelArchiveRequest caRequest = ChannelArchiveRequestServiceStub.createCAR(1L, "dr1", WeekdayCoverage.MONDAY, new Time(0, 0, 0), new Time(1, 0, 0), dateToCheck.minusDays(3).toDate(), dateToCheck.plusDays(3).toDate());
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                null, 
                null, 
                new YouSeeChannelMappingServiceStub(), 
                System.out);
        Set<MediaFileIngestParameters> files = initiator.inferFilesToIngest(caRequest, dateToCheck);
        MediaFileIngestParameters mediaFileIngestParameters = new ArrayList<MediaFileIngestParameters>(files).get(0);
        String actual = mediaFileIngestParameters.getYouseeFileName();
        assertEquals("DR1_20100301000000_20100301010000.mux", actual);
    }

    @Test
    public void inferTotalFilesToDownloadOneChannelOneWeekTest() throws IOException {
        List<ChannelArchiveRequest> caRequests = new ArrayList<ChannelArchiveRequest>(); 
        caRequests.add(ChannelArchiveRequestServiceStub.createCAR(1L, "dr1", WeekdayCoverage.MONDAY, new Time(8, 0, 0), new Time(20, 0, 0), new Date(0), new DateTime().plusMonths(3).toDate()));
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                null, 
                null, 
                new YouSeeChannelMappingServiceStub(), 
                System.out);

        int youSeeKeepDuration = 7;
        DateTime toDate = new DateTime(2010, 3, youSeeKeepDuration, 0, 0, 0, 0); // 2010-03-01 ~ monday, 2010-03-07 ~ sunday
        DateTime fromDate = toDate.minusDays(youSeeKeepDuration);
        List<MediaFileIngestParameters> actual = initiator.inferFilesToIngest(caRequests, fromDate, toDate);
        int expectedNumberOfFiles = 12;
        assertEquals(expectedNumberOfFiles, actual.size());
    }

    @Test
    public void inferTotalFilesToDownloadOneChannelTwoWeeksTest() throws IOException {
        List<ChannelArchiveRequest> caRequests = new ArrayList<ChannelArchiveRequest>(); 
        caRequests.add(ChannelArchiveRequestServiceStub.createCAR(1L, "dr1", WeekdayCoverage.MONDAY, new Time(8, 0, 0), new Time(20, 0, 0), new Date(0), new DateTime().plusMonths(3).toDate()));
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                null, 
                null, 
                new YouSeeChannelMappingServiceStub(), 
                System.out);

        int youSeeKeepDuration = 14;
        DateTime toDate = new DateTime(2010, 3, youSeeKeepDuration, 0, 0, 0, 0); // 2010-03-01 ~ monday, 2010-03-07 ~ sunday
        DateTime fromDate = toDate.minusDays(youSeeKeepDuration);
        List<MediaFileIngestParameters> actual = initiator.inferFilesToIngest(caRequests, fromDate, toDate);
        int expectedNumberOfFiles = 2*12;
        assertEquals(expectedNumberOfFiles, actual.size());
    }

    @Test
    public void inferTotalFilesToDownloadOneChannelWeekDaysOneWeekTest() throws IOException {
        List<ChannelArchiveRequest> caRequests = new ArrayList<ChannelArchiveRequest>(); 
        caRequests.add(ChannelArchiveRequestServiceStub.createCAR(1L, "dr1", WeekdayCoverage.MONDAY_TO_THURSDAY, new Time(8, 0, 0), new Time(20, 0, 0), new Date(0), new DateTime().plusMonths(3).toDate()));
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                null, 
                null, 
                new YouSeeChannelMappingServiceStub(), 
                System.out);

        int youSeeKeepDuration = 7;
        DateTime toDate = new DateTime(2010, 3, youSeeKeepDuration, 0, 0, 0, 0); // 2010-03-01 ~ monday, 2010-03-07 ~ sunday
        DateTime fromDate = toDate.minusDays(youSeeKeepDuration);
        List<MediaFileIngestParameters> actual = initiator.inferFilesToIngest(caRequests, fromDate, toDate);
        int expectedNumberOfFiles = 4*12;
        assertEquals(expectedNumberOfFiles, actual.size());
    }

    @Test
    public void inferTotalFilesToDownloadOneChannelDailyOneWeekTest() throws IOException {
        List<ChannelArchiveRequest> caRequests = new ArrayList<ChannelArchiveRequest>(); 
        caRequests.add(ChannelArchiveRequestServiceStub.createCAR(1L, "dr1", WeekdayCoverage.DAILY, new Time(0, 0, 0), new Time(23, 59, 0), new Date(0), new DateTime().plusMonths(3).toDate()));
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                null, 
                null, 
                new YouSeeChannelMappingServiceStub(), 
                System.out);

        int youSeeKeepDuration = 7;
        DateTime toDate = new DateTime(2010, 3, youSeeKeepDuration, 0, 0, 0, 0); // 2010-03-01 ~ monday, 2010-03-07 ~ sunday
        DateTime fromDate = toDate.minusDays(youSeeKeepDuration-1);
        List<MediaFileIngestParameters> actual = initiator.inferFilesToIngest(caRequests, fromDate, toDate);
        int expectedNumberOfFiles = 7*24;
        assertEquals(expectedNumberOfFiles, actual.size());
    }

    @Test
    public void inferTotalFilesToDownloadOneChannelOverlappingRequestOneWeekTest() throws IOException {
        List<ChannelArchiveRequest> caRequests = new ArrayList<ChannelArchiveRequest>(); 
        caRequests.add(ChannelArchiveRequestServiceStub.createCAR(1L, "dr1", WeekdayCoverage.DAILY, new Time(0, 0, 0), new Time(23, 59, 0), new Date(0), new DateTime().plusMonths(3).toDate()));
        caRequests.add(ChannelArchiveRequestServiceStub.createCAR(1L, "dr1", WeekdayCoverage.THURSDAY, new Time(0, 0, 0), new Time(23, 59, 0), new Date(0), new DateTime().plusMonths(3).toDate()));
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                null, 
                null, 
                new YouSeeChannelMappingServiceStub(), 
                System.out);

        int youSeeKeepDuration = 7;
        DateTime toDate = new DateTime(2010, 3, youSeeKeepDuration, 0, 0, 0, 0); // 2010-03-01 ~ monday, 2010-03-07 ~ sunday
        DateTime fromDate = toDate.minusDays(youSeeKeepDuration-1);
        List<MediaFileIngestParameters> actual = initiator.inferFilesToIngest(caRequests, fromDate, toDate);
        for (MediaFileIngestParameters mediaFileIngestParameters : actual) {
            log.debug(mediaFileIngestParameters);
        }
        int expectedNumberOfFiles = 7*24;
        assertEquals(expectedNumberOfFiles, actual.size());
    }
    
}
