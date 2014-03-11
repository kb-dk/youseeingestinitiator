package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.mock.ChannelArchiveRequestServiceTestStub;
import dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.mock.WorkFlowStateMonitorFacadeStub;
import dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.mock.YouSeeChannelMappingServiceTestStub;
import dk.statsbiblioteket.mediaplatform.ingest.model.ChannelArchiveRequest;
import dk.statsbiblioteket.mediaplatform.ingest.model.WeekdayCoverage;

public class IngestMediaFilesInitiatorTest {

    private Logger log = null;
    private Properties defaultProperties;

    public IngestMediaFilesInitiatorTest() throws IOException {
        File propertyFile = new File(getClass().getClassLoader().getResource(
                "ingest_initiator_media_files_unittest.properties").getPath());
        FileInputStream in = new FileInputStream(propertyFile);
        defaultProperties = new Properties();
        defaultProperties.load(in);
        in.close();
        System.getProperties().put("log4j.defaultInitOverride", "true");
        DOMConfigurator.configure(getClass().getClassLoader().getResource(defaultProperties.getProperty("log4j.config.file.path")));
        log = Logger.getLogger(IngestMediaFilesInitiatorTest.class);
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
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties, 
                null,
                null, 
                null, 
                System.out);

        List<MediaFileIngestOutputParameters> outputList = new ArrayList<MediaFileIngestOutputParameters>();
        outputList.add(new MediaFileIngestOutputParameters("dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts", "DR HD_20120915_100000_20120915_110000.mux", "drhd", "DR HD", new DateTime(2012,9,15,10,0,0), new DateTime(2012,9,15,11,0,0)));
        outputList.add(new MediaFileIngestOutputParameters("dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts", "DR HD_20120915_110000_20120915_120000.mux", "drhd", "DR HD", new DateTime(2012,9,15,11,0,0), new DateTime(2012,9,15,12,0,0)));
        initiator.outputResult(outputList , byteArrayoutputStream);

        String actual = byteArrayoutputStream.toString();
        String expected = 
                " {\n"
                + "     \"downloads\":[\n"
                + "         {\n"
                + "            \"fileID\" : \"dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts\",\n"
                + "            \"youSeeFilename\" : \"DR HD_20120915_100000_20120915_110000.mux\",\n"
                + "            \"startTime\" : \"20120915T100000+0200\",\n"
                + "            \"endTime\" : \"20120915T110000+0200\",\n"
                + "            \"youseeChannelID\" : \"DR HD\",\n"
                + "            \"sbChannelID\" : \"drhd\"\n"
                + "         },\n"
                + "         {\n"
                + "            \"fileID\" : \"dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts\",\n"
                + "            \"youSeeFilename\" : \"DR HD_20120915_110000_20120915_120000.mux\",\n"
                + "            \"startTime\" : \"20120915T110000+0200\",\n"
                + "            \"endTime\" : \"20120915T120000+0200\",\n"
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
        ChannelArchiveRequest caRequest = ChannelArchiveRequestServiceTestStub.createRequest(1L, "dr1", WeekdayCoverage.MONDAY, new Time(0, 0, 0), new Time(1, 0, 0), dateToCheck.minusDays(3).toDate(), dateToCheck.plusDays(3).toDate());
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties, 
                null, 
                new YouSeeChannelMappingServiceTestStub(), 
                null,
                System.out);
        Set<MediaFileIngestOutputParameters> files = initiator.inferFilesToIngest(caRequest, dateToCheck);
        MediaFileIngestOutputParameters mediaFileIngestParameters = new ArrayList<MediaFileIngestOutputParameters>(files).get(0);
        String actual = mediaFileIngestParameters.getFileNameYouSee();
        // Note: File dates are in UTC.
        assertEquals("DR1_20100228_230000_20100301_000000.mux", actual);
    }

    @Test
    public void inferFilesToIngestSummerTimeTest() throws IOException {
        DateTime dateToCheck = new DateTime(2013, 3, 31, 0, 0, 0, 0); // 2013-03-31 ~ sunday, 2013-04-01 ~ monday
        ChannelArchiveRequest caRequest = ChannelArchiveRequestServiceTestStub.createRequest(1L, "dr1", WeekdayCoverage.SUNDAY, new Time(0, 0, 0), new Time(2, 0, 0), dateToCheck.minusDays(3).toDate(), dateToCheck.plusDays(3).toDate());
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties,
                null,
                new YouSeeChannelMappingServiceTestStub(),
                null,
                System.out);
        Set<MediaFileIngestOutputParameters> files = initiator.inferFilesToIngest(caRequest, dateToCheck);
        int expectedNumberOfFiles = 2;
        assertEquals(expectedNumberOfFiles, files.size());
    }

    @Test
    public void inferTotalFilesToDownloadOneChannelOneWeekTest() throws IOException {
        List<ChannelArchiveRequest> caRequests = new ArrayList<ChannelArchiveRequest>(); 
        caRequests.add(ChannelArchiveRequestServiceTestStub.createRequest(1L, "dr1", WeekdayCoverage.MONDAY, new Time(8, 0, 0), new Time(20, 0, 0), new Date(0), new DateTime().plusMonths(3).toDate()));
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties, 
                null, 
                new YouSeeChannelMappingServiceTestStub(), 
                new WorkFlowStateMonitorFacadeStub(),
                System.out);

        int youSeeKeepDuration = 7;
        DateTime toDate = new DateTime(2010, 3, youSeeKeepDuration, 0, 0, 0, 0); // 2010-03-01 ~ monday, 2010-03-07 ~ sunday
        DateTime fromDate = toDate.minusDays(youSeeKeepDuration);
        List<MediaFileIngestOutputParameters> actual = initiator.inferFilesToIngest(caRequests, fromDate, toDate);
        int expectedNumberOfFiles = 12;
        assertEquals(expectedNumberOfFiles, actual.size());
    }

    @Test
    public void inferTotalFilesToDownloadOneChannelTwoWeeksTest() throws IOException {
        List<ChannelArchiveRequest> caRequests = new ArrayList<ChannelArchiveRequest>(); 
        caRequests.add(ChannelArchiveRequestServiceTestStub.createRequest(1L, "dr1", WeekdayCoverage.MONDAY, new Time(8, 0, 0), new Time(20, 0, 0), new Date(0), new DateTime().plusMonths(3).toDate()));
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties, 
                null, 
                new YouSeeChannelMappingServiceTestStub(), 
                new WorkFlowStateMonitorFacadeStub(),
                System.out);

        int youSeeKeepDuration = 14;
        DateTime toDate = new DateTime(2010, 3, youSeeKeepDuration, 0, 0, 0, 0); // 2010-03-01 ~ monday, 2010-03-07 ~ sunday
        DateTime fromDate = toDate.minusDays(youSeeKeepDuration);
        List<MediaFileIngestOutputParameters> actual = initiator.inferFilesToIngest(caRequests, fromDate, toDate);
        int expectedNumberOfFiles = 2*12;
        assertEquals(expectedNumberOfFiles, actual.size());
    }

    @Test
    public void inferTotalFilesToDownloadOneChannelTwoWeeksTestDisabled() throws IOException {
        List<ChannelArchiveRequest> caRequests = new ArrayList<ChannelArchiveRequest>();
        final ChannelArchiveRequest dr1Request = ChannelArchiveRequestServiceTestStub.createRequest(1L, "dr1", WeekdayCoverage.MONDAY, new Time(8, 0, 0), new Time(20, 0, 0), new Date(0), new DateTime().plusMonths(3).toDate());
        dr1Request.setEnabled(false);
        caRequests.add(dr1Request);
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties,
                null,
                new YouSeeChannelMappingServiceTestStub(),
                new WorkFlowStateMonitorFacadeStub(),
                System.out);

        int youSeeKeepDuration = 14;
        DateTime toDate = new DateTime(2010, 3, youSeeKeepDuration, 0, 0, 0, 0); // 2010-03-01 ~ monday, 2010-03-07 ~ sunday
        DateTime fromDate = toDate.minusDays(youSeeKeepDuration);
        List<MediaFileIngestOutputParameters> actual = initiator.inferFilesToIngest(caRequests, fromDate, toDate);
        int expectedNumberOfFiles = 0;
        assertEquals(expectedNumberOfFiles, actual.size());
    }

    @Test
    public void inferTotalFilesToDownloadOneChannelWeekDaysOneWeekTest() throws IOException {
        List<ChannelArchiveRequest> caRequests = new ArrayList<ChannelArchiveRequest>(); 
        caRequests.add(ChannelArchiveRequestServiceTestStub.createRequest(1L, "dr1", WeekdayCoverage.MONDAY_TO_THURSDAY, new Time(8, 0, 0), new Time(20, 0, 0), new Date(0), new DateTime().plusMonths(3).toDate()));
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties, 
                null, 
                new YouSeeChannelMappingServiceTestStub(), 
                new WorkFlowStateMonitorFacadeStub(),
                System.out);

        int youSeeKeepDuration = 7;
        DateTime toDate = new DateTime(2010, 3, youSeeKeepDuration, 0, 0, 0, 0); // 2010-03-01 ~ monday, 2010-03-07 ~ sunday
        DateTime fromDate = toDate.minusDays(youSeeKeepDuration);
        List<MediaFileIngestOutputParameters> actual = initiator.inferFilesToIngest(caRequests, fromDate, toDate);
        int expectedNumberOfFiles = 4*12;
        assertEquals(expectedNumberOfFiles, actual.size());
    }

    @Test
    public void inferTotalFilesToDownloadOneChannelDailyOneWeekTest() throws IOException {
        List<ChannelArchiveRequest> caRequests = new ArrayList<ChannelArchiveRequest>(); 
        caRequests.add(ChannelArchiveRequestServiceTestStub.createRequest(1L, "dr1", WeekdayCoverage.DAILY, new Time(0, 0, 0), new Time(0, 0, 0), new Date(0), new DateTime().plusMonths(3).toDate()));
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties, 
                null, 
                new YouSeeChannelMappingServiceTestStub(), 
                new WorkFlowStateMonitorFacadeStub(),
                System.out);

        int youSeeKeepDuration = 7;
        DateTime toDate = new DateTime(2010, 3, youSeeKeepDuration, 0, 0, 0, 0); // 2010-03-01 ~ monday, 2010-03-07 ~ sunday
        DateTime fromDate = toDate.minusDays(youSeeKeepDuration-1);
        List<MediaFileIngestOutputParameters> actual = initiator.inferFilesToIngest(caRequests, fromDate, toDate);
        int expectedNumberOfFiles = 7*24;
        assertEquals(expectedNumberOfFiles, actual.size());
    }

    @Test
    public void inferTotalFilesToDownloadOneChannelInverseTimes() throws IOException {
        List<ChannelArchiveRequest> caRequests = new ArrayList<ChannelArchiveRequest>();
        caRequests.add(ChannelArchiveRequestServiceTestStub.createRequest(1L, "dr1", WeekdayCoverage.DAILY, new Time(23, 0, 0), new Time(1, 0, 0), new Date(0), new DateTime().plusMonths(3).toDate()));
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties,
                null,
                new YouSeeChannelMappingServiceTestStub(),
                new WorkFlowStateMonitorFacadeStub(),
                System.out);

        int youSeeKeepDuration = 1;
        DateTime toDate = new DateTime(2010, 3, youSeeKeepDuration, 0, 0, 0, 0); // 2010-03-01 ~ monday, 2010-03-07 ~ sunday
        DateTime fromDate = toDate.minusDays(youSeeKeepDuration-1);
        List<MediaFileIngestOutputParameters> actual = initiator.inferFilesToIngest(caRequests, fromDate, toDate);
        int expectedNumberOfFiles = 2;
        assertEquals(expectedNumberOfFiles, actual.size());
    }

    @Test
    public void inferTotalFilesToDownloadOneChannelOverlappingRequestOneWeekTest() throws IOException {
        List<ChannelArchiveRequest> caRequests = new ArrayList<ChannelArchiveRequest>(); 
        caRequests.add(ChannelArchiveRequestServiceTestStub.createRequest(1L, "dr1", WeekdayCoverage.DAILY, new Time(0, 0, 0), new Time(23, 59, 0), new Date(0), new DateTime().plusMonths(3).toDate()));
        caRequests.add(ChannelArchiveRequestServiceTestStub.createRequest(1L, "dr1", WeekdayCoverage.THURSDAY, new Time(0, 0, 0), new Time(23, 59, 0), new Date(0), new DateTime().plusMonths(3).toDate()));
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties, 
                null, 
                new YouSeeChannelMappingServiceTestStub(), 
                new WorkFlowStateMonitorFacadeStub(),
                System.out);

        int youSeeKeepDuration = 7;
        DateTime toDate = new DateTime(2010, 3, youSeeKeepDuration, 0, 0, 0, 0); // 2010-03-01 ~ monday, 2010-03-07 ~ sunday
        DateTime fromDate = toDate.minusDays(youSeeKeepDuration-1);
        List<MediaFileIngestOutputParameters> actual = initiator.inferFilesToIngest(caRequests, fromDate, toDate);
        for (MediaFileIngestOutputParameters mediaFileIngestParameters : actual) {
            log.debug(mediaFileIngestParameters);
        }
        int expectedNumberOfFiles = 7*24;
        assertEquals(expectedNumberOfFiles, actual.size());
    }

    /** 
     * Test that the new sb filenames of yousee downloaded files are similiar to old format
     * 
     * Old: mux1.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts
     * New: dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_yousee.ts
     * 
     */
    @Test
    public void testGetSBFileID_checkSBFilename() {
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties, 
                null, 
                new YouSeeChannelMappingServiceTestStub(),
                null,
                System.out);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd-HH.mm.ss");
        String actualFilename = initiator.getSBFileID("dr1", fmt.parseDateTime("2012-01-09-14.00.00"), fmt.parseDateTime("2012-01-09-15.00.00"));
        String expectedFilename = "dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_yousee.ts";
        assertEquals(expectedFilename, actualFilename);
    }

    @Test
    public void testShouldInititateIngest_notIngested() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd-HH.mm.ss");
        WorkFlowStateMonitorFacadeStub workFlowStateMonitorFacade = new WorkFlowStateMonitorFacadeStub();
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties, 
                null, 
                new YouSeeChannelMappingServiceTestStub(),
                workFlowStateMonitorFacade,
                System.out);
        DateTime dateOfIngest = dateTimeFormatter.parseDateTime("2012-01-28-03.00.00");
        String fileNameSB = "dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts";
        boolean result = initiator.shouldInititateIngest(dateOfIngest, fileNameSB);
        assertEquals(true, result);
    }    


    @Test
    public void testShouldInititateIngest_failedIngest() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd-HH.mm.ss");
        Date stateUpdatedDate = new DateTime().minusHours(13).toDate();
        String component = "yousee Downloader";
        String sbFilenameId = "dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts";
        String stateName = "Starting";
        WorkFlowStateMonitorFacadeStub workFlowStateMonitorFacade = new WorkFlowStateMonitorFacadeStub(component, stateUpdatedDate, sbFilenameId, stateName);
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties, 
                null, 
                new YouSeeChannelMappingServiceTestStub(),
                workFlowStateMonitorFacade,
                System.out);
        DateTime dateOfIngest = dateTimeFormatter.parseDateTime("2012-01-28-03.00.00");
        String fileNameSB = "dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts";
        boolean result = initiator.shouldInititateIngest(dateOfIngest, fileNameSB);
        assertEquals(true, result);
    }    

    @Test
    public void testShouldInititateIngest_completedIngest() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd-HH.mm.ss");
        Date stateUpdatedDate = new DateTime().minusHours(13).toDate();
        String component = "Yousee complete workflow final step";
        String sbFilenameId = "dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts";
        String stateName = "Done";
        WorkFlowStateMonitorFacadeStub workFlowStateMonitorFacade = new WorkFlowStateMonitorFacadeStub(component, stateUpdatedDate, sbFilenameId, stateName);
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties, 
                null, 
                new YouSeeChannelMappingServiceTestStub(),
                workFlowStateMonitorFacade,
                System.out);
        DateTime dateOfIngest = dateTimeFormatter.parseDateTime("2012-01-28-03.00.00");
        String fileNameSB = "dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts";
        boolean result = initiator.shouldInititateIngest(dateOfIngest, fileNameSB);
        assertEquals(false, result);
    }    

    @Test
    public void testShouldInititateIngest_recentlyStartedIngest() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd-HH.mm.ss");
        Date stateUpdatedDate = new DateTime().minusHours(1).toDate();
        String component = "Yousee complete workflow final step";
        String sbFilenameId = "dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts";
        String stateName = "Completed";
        WorkFlowStateMonitorFacadeStub workFlowStateMonitorFacade = new WorkFlowStateMonitorFacadeStub(component, stateUpdatedDate, sbFilenameId, stateName);
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties, 
                null, 
                new YouSeeChannelMappingServiceTestStub(),
                workFlowStateMonitorFacade,
                System.out);
        DateTime dateOfIngest = dateTimeFormatter.parseDateTime("2012-01-28-03.00.00");
        String fileNameSB = "dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts";
        boolean result = initiator.shouldInititateIngest(dateOfIngest, fileNameSB);
        assertEquals(false, result);
    }    

    @Test
    public void testShouldInititateIngest_stopped() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd-HH.mm.ss");
        Date stateUpdatedDate = new DateTime().minusHours(1).toDate();
        String component = "Yousee complete workflow final step";
        String sbFilenameId = "dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts";
        String stateName = "Stopped";
        WorkFlowStateMonitorFacadeStub workFlowStateMonitorFacade = new WorkFlowStateMonitorFacadeStub(component, stateUpdatedDate, sbFilenameId, stateName);
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties, 
                null, 
                new YouSeeChannelMappingServiceTestStub(),
                workFlowStateMonitorFacade,
                System.out);
        DateTime dateOfIngest = dateTimeFormatter.parseDateTime("2012-01-28-03.00.00");
        String fileNameSB = "dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts";
        boolean result = initiator.shouldInititateIngest(dateOfIngest, fileNameSB);
        assertEquals(false, result);
    }    

    @Test
    public void testShouldInititateIngest_restarted() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd-HH.mm.ss");
        Date stateUpdatedDate = new DateTime().minusHours(1).toDate();
        String component = "Yousee complete workflow final step";
        String sbFilenameId = "dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts";
        String stateName = "Restarted";
        WorkFlowStateMonitorFacadeStub workFlowStateMonitorFacade = new WorkFlowStateMonitorFacadeStub(component, stateUpdatedDate, sbFilenameId, stateName);
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties, 
                null, 
                new YouSeeChannelMappingServiceTestStub(),
                workFlowStateMonitorFacade,
                System.out);
        DateTime dateOfIngest = dateTimeFormatter.parseDateTime("2012-01-28-03.00.00");
        String fileNameSB = "dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts";
        boolean result = initiator.shouldInititateIngest(dateOfIngest, fileNameSB);
        assertEquals(true, result);
    }    

    @Test
    public void testShouldInititateIngest_recentlyFailed() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd-HH.mm.ss");
        Date stateUpdatedDate = new DateTime().minusHours(1).toDate();
        String component = "Yousee complete workflow final step";
        String sbFilenameId = "dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts";
        String stateName = "Failed";
        WorkFlowStateMonitorFacadeStub workFlowStateMonitorFacade = new WorkFlowStateMonitorFacadeStub(component, stateUpdatedDate, sbFilenameId, stateName);
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties, 
                null, 
                new YouSeeChannelMappingServiceTestStub(),
                workFlowStateMonitorFacade,
                System.out);
        DateTime dateOfIngest = dateTimeFormatter.parseDateTime("2012-01-28-03.00.00");
        String fileNameSB = "dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts";
        boolean result = initiator.shouldInititateIngest(dateOfIngest, fileNameSB);
        assertEquals(false, result);
    }    

    @Test
    public void testShouldInititateIngest_notRecentlyFailed() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd-HH.mm.ss");
        Date stateUpdatedDate = new DateTime().minusHours(13).toDate();
        String component = "Yousee complete workflow final step";
        String sbFilenameId = "dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts";
        String stateName = "Failed";
        WorkFlowStateMonitorFacadeStub workFlowStateMonitorFacade = new WorkFlowStateMonitorFacadeStub(component, stateUpdatedDate, sbFilenameId, stateName);
        IngestMediaFilesInitiator initiator = new IngestMediaFilesInitiator(
                defaultProperties, 
                null, 
                new YouSeeChannelMappingServiceTestStub(),
                workFlowStateMonitorFacade,
                System.out);
        DateTime dateOfIngest = dateTimeFormatter.parseDateTime("2012-01-28-03.00.00");
        String fileNameSB = "dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts";
        boolean result = initiator.shouldInititateIngest(dateOfIngest, fileNameSB);
        assertEquals(true, result);
    }    
}
