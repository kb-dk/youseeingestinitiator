package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import dk.statsbiblioteket.mediaplatform.ingest.model.service.validator.ChannelArchivingRequesterValidator;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.validator.ValidationFailure;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.validator.ValidatorIF;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


import dk.statsbiblioteket.mediaplatform.ingest.model.ChannelArchiveRequest;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchiveRequestServiceIF;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ServiceException;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.YouSeeChannelMappingServiceIF;
import dk.statsbiblioteket.mediaplatform.workflowstatemonitor.State;

/**
 * Formålet med denne klasse er at starte et workflow til ingest af nye mediefiler i Medieplatformen.
 * I praksis betyder det at komponenterne BitMag, DOMS og DigiTV bliver beriget med data og metadata
 * om nye filer.
 * 
 * Klassen repræsenterer det første skridt i et workflow, der downloader, karakteriserer og 
 * kvalitetstjekker filerne inden de ingestes i slutsystemerne.
 * 
 * @author henningbottger
 *
 */
public class IngestMediaFilesInitiator {

    private static final String YOUSEE_RECORDINGS_DAYS_TO_KEEP_KEY = "yousee.recordings.days.to.keep";
    private static final String EXPECTED_DURATION_OF_FILE_INGEST_PROCESS_KEY = "expected.duration.of.file.ingest.process";
    private static final String FINAL_WORK_FLOW_COMPONENT_NAME_KEY = "final.work.flow.component.name";
    private static final String FINAL_WORK_FLOW_STATE_NAME_KEY = "final.work.flow.state.name";
    
    private static final Logger log = Logger.getLogger(IngestMediaFilesInitiator.class);;
    private static final DateTimeFormatter youseeFilenameDateFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter sbFilenameDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd-HH.mm.ss");

    private final ChannelArchiveRequestServiceIF channelArchiveRequestService;
    private final YouSeeChannelMappingServiceIF youSeeChannelMappingService;
    private final WorkFlowStateMonitorFacade workFlowStateMonitorFacade;
    private final OutputStream outputStream;
    private final int daysYouSeeKeepsRecordings;
    private final int expectedDurationOfFileIngestProcess;
    private final String finalWorkFlowComponentName;
    private final String finalWorkFlowStateName;

    public IngestMediaFilesInitiator(Properties properties, ChannelArchiveRequestServiceIF channelArchiveRequestDAO, YouSeeChannelMappingServiceIF youSeeChannelMappingService, WorkFlowStateMonitorFacade workFlowStateMonitorFacade, OutputStream outputStream) {
        this.channelArchiveRequestService = channelArchiveRequestDAO;
        this.youSeeChannelMappingService = youSeeChannelMappingService;
        this.workFlowStateMonitorFacade = workFlowStateMonitorFacade;
        this.outputStream = outputStream;
        this.daysYouSeeKeepsRecordings = Integer.parseInt(properties.getProperty(YOUSEE_RECORDINGS_DAYS_TO_KEEP_KEY));
        this.expectedDurationOfFileIngestProcess = Integer.parseInt(properties.getProperty(EXPECTED_DURATION_OF_FILE_INGEST_PROCESS_KEY));
        this.finalWorkFlowComponentName = properties.getProperty(FINAL_WORK_FLOW_COMPONENT_NAME_KEY);
        this.finalWorkFlowStateName = properties.getProperty(FINAL_WORK_FLOW_STATE_NAME_KEY);
    }

    /**
     * Input til initiatoren er en dato. Initiatoren gennemgår derpå følgende skridt:
     * 
     * <ol>
     *   <li>Udled periode som vi ønsker at downloade filer for, eg. nu og 28 dage tilbage</li>
     *   <li>Hent planlagt optageperioder fra ChannelArchiveRequestService</li>
     *   <li>Udled hvilke filer der skal downloades ud fra planlagte optageperioder og den periode vi 
     *          ønsker at downloade filer fra</li>
     *   <li>Filtrer filer fra som vi allerede har ingested i systemet</li>
     *   <li>Output ingest job for hver fil der ønskes ingested til stdout</li>
     * </ol>
     * 
     * 
     * @param dateOfIngest date and time when the process was started
     */
    public void initiateIngest(DateTime dateOfIngest) {
        try {
            log.debug("Initiated ingest based on date: " + dateOfIngest);
            // Infer period to ingest
            DateTime toDate = dateOfIngest;
            DateTime fromDate = dateOfIngest.minusDays(daysYouSeeKeepsRecordings-1); // dateOfIngest counts as one day
            log.info("Ingestion periode: " + fromDate + " to " + toDate);
            List<ChannelArchiveRequest> caRequests;
            caRequests = channelArchiveRequestService.getValidRequests(fromDate.toDate(), toDate.toDate());
            log.debug("Found requests size: " + caRequests.size());
            List<MediaFileIngestOutputParameters> fullFileList = inferFilesToIngest(caRequests, fromDate, toDate);
            log.debug("Full file list size: " + fullFileList.size());
            List<MediaFileIngestOutputParameters> filteredFileList = filterOutFilesAlreadyIngested(dateOfIngest, new ArrayList<MediaFileIngestOutputParameters>(fullFileList));
            log.debug("Filtered file list size: " + filteredFileList.size());
            outputResult(filteredFileList, outputStream);
            log.debug("Done initiating ingest based on date: " + dateOfIngest);
        } catch (Exception e) {
            log.error("An error occured: " + e.getMessage(), e);
            throw new RuntimeException("An error occured initiating ingest.", e);
        }
    }

    protected List<MediaFileIngestOutputParameters> inferFilesToIngest(List<ChannelArchiveRequest> caRequests, DateTime fromDate, DateTime toDate) {
        log.debug("Inferring files to ingest. Request: " + caRequests + ", fromDate: " + fromDate + ", toDate: " + toDate);
        Set<MediaFileIngestOutputParameters> filesToIngest = new HashSet<MediaFileIngestOutputParameters>();
        DateTime dayToCheck = fromDate;
        while (dayToCheck.isBefore(toDate) || dayToCheck.equals(toDate)) {
            for (ChannelArchiveRequest car : caRequests) {
                if (car.isEnabled()) {
                    filesToIngest.addAll(inferFilesToIngest(car, dayToCheck));
                } else {
                    log.error("Not scheduling files from request " + car.toString() + " because of validation failure " + car.getCause());
                }
            }
            dayToCheck = dayToCheck.plusDays(1);
        }
        List<MediaFileIngestOutputParameters> fileList = new ArrayList<MediaFileIngestOutputParameters>(filesToIngest);
        Collections.sort(fileList);
        return fileList;
    }

    /**
     * Given request and a date, the method finds the hour intervals to download. The
     * 1 hour intervals are always in whole hours, ie. minutes are 0. In order to 
     * fulfill the request, the download intervals may be longer than the specific
     * requests. 
     * 
     *  - Request Channel:DR1 From:14:30 To:15:30
     * 
     *  Corresponding download intervals
     *  
     *  - Interval 1: Channel:DR1 From:14:00 To:15:00
     *  - Interval 2: Channel:DR1 From:15:00 To:16:00
     * 
     * @param caRequest
     * @param dayToCheck
     * @return
     */
    protected Set<MediaFileIngestOutputParameters> inferFilesToIngest(ChannelArchiveRequest caRequest, DateTime dayToCheck) {
       Set<MediaFileIngestOutputParameters> filesToIngest = new HashSet<MediaFileIngestOutputParameters>();
        try {
            if (isChannelArchiveRequestActive(caRequest, dayToCheck)) {
                String sbChannelID = caRequest.getsBChannelId();
                LocalTime localTimeFrom = new LocalTime(caRequest.getFromTime().getTime());
                LocalTime localTimeTo = new LocalTime(caRequest.getToTime().getTime());
                int fromHour = localTimeFrom.getHourOfDay();
                int toHour = localTimeTo.getHourOfDay();
                if (localTimeTo.getMinuteOfHour() != 0) {
                    toHour++;
                }
                int hour = fromHour;
                while (hour < toHour) {
                    DateTime startDate = new DateTime(dayToCheck.getYear(), dayToCheck.getMonthOfYear(), dayToCheck.getDayOfMonth(), hour, 0);
                    DateTime endDate = startDate.plusHours(1);
                    String youseeChannelID = youSeeChannelMappingService.getUniqueMappingFromSbChannelId(sbChannelID, startDate.toDate()).getYouSeeChannelId();
                    String filenameYouSee = getYouSeeFilename(startDate, endDate, youseeChannelID);
                    String filenameSB = getSBFileID(sbChannelID, startDate, endDate);
                    filesToIngest.add(new MediaFileIngestOutputParameters(filenameSB, filenameYouSee, sbChannelID, youseeChannelID, startDate, endDate));
                    hour++;
                }
            }
        } catch (ServiceException e) {
            throw new RuntimeException("An unexpected error occured.", e);
        }
        return filesToIngest;
    }

    /**
     * Infer the filename as expected on the YouSee server.
     * 
     * Filename format: "<YouSee_channel_id>_<start_date>_<end_date>.mux"
     * 
     * @param startDate
     * @param endDate
     * @param youseeChannelID
     * @return
     */
    protected String getYouSeeFilename(DateTime startDate, DateTime endDate,
            String youseeChannelID) {
        String filenameYouSee = 
                youseeChannelID + "_"
                + youseeFilenameDateFormatter.print(startDate) + "_"
                + youseeFilenameDateFormatter.print(endDate) + ".mux";
        return filenameYouSee;
    }

    /**
     * Infer the file id used in the archive. The file id is designed to match the format of the 
     * Digital TV-recordings that the new workflow shall replace.
     * 
     * Format <channel_id>.<seconds_since_1970-01-01>-<
     * 
     * Old digital filename: mux1.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts
     * New YouSee filename: dr1_yousee.1326114000-2012-01-09-14.00.00_1326117600-2012-01-09-15.00.00_dvb1-1.ts
     * 
     * @param sbChannelID 
     * @param startDate
     * @param endDate
     * @return
     */
    protected String getSBFileID(String sbChannelID, DateTime startDate, DateTime endDate) {
        Long startDateInSecondsSince1970 = startDate.getMillis() / 1000;
        Long endDateInSecondsSince1970 = endDate.getMillis() / 1000;
        String filenameSB = sbChannelID
                + "_yousee."
                + startDateInSecondsSince1970 + "-"
                + sbFilenameDateFormatter.print(startDate) + "_"
                + endDateInSecondsSince1970 + "-"
                + sbFilenameDateFormatter.print(endDate)
                + "_ftp.ts";
        return filenameSB;
    }

    /** Identifies whether a channel archiving request contributes to download a given day. */
    protected boolean isChannelArchiveRequestActive(ChannelArchiveRequest caRequest, DateTime dayToCheck) {
        boolean caRequestActive = false;
        DateTime caRequestFromDate = new DateTime(caRequest.getFromDate()); 
        DateTime caRequestToDate = new DateTime(caRequest.getToDate()); 
        boolean caRequestPeriodValid = (caRequestFromDate.equals(dayToCheck) || caRequestFromDate.isBefore(dayToCheck)) && 
                (caRequestToDate.equals(dayToCheck) || caRequestToDate.isAfter(dayToCheck));
        if (caRequestPeriodValid) {
            switch (caRequest.getWeekdayCoverage()) {
            case DAILY:
                caRequestActive = true;
                break;

            case MONDAY:
                if (dayToCheck.getDayOfWeek() == DateTimeConstants.MONDAY) {
                    caRequestActive = true;
                }
                break;

            case TUESDAY:
                if (dayToCheck.getDayOfWeek() == DateTimeConstants.TUESDAY) {
                    caRequestActive = true;
                }
                break;

            case WEDNESDAY:
                if (dayToCheck.getDayOfWeek() == DateTimeConstants.WEDNESDAY) {
                    caRequestActive = true;
                }
                break;

            case THURSDAY:
                if (dayToCheck.getDayOfWeek() == DateTimeConstants.THURSDAY) {
                    caRequestActive = true;
                }
                break;

            case FRIDAY:
                if (dayToCheck.getDayOfWeek() == DateTimeConstants.FRIDAY) {
                    caRequestActive = true;
                }
                break;

            case SATURDAY:
                if (dayToCheck.getDayOfWeek() == DateTimeConstants.SATURDAY) {
                    caRequestActive = true;
                }
                break;

            case SUNDAY:
                if (dayToCheck.getDayOfWeek() == DateTimeConstants.SUNDAY) {
                    caRequestActive = true;
                }
                break;

            case MONDAY_TO_THURSDAY:
                if ((dayToCheck.getDayOfWeek() == DateTimeConstants.MONDAY) ||
                        (dayToCheck.getDayOfWeek() == DateTimeConstants.TUESDAY) ||
                        (dayToCheck.getDayOfWeek() == DateTimeConstants.WEDNESDAY) ||
                        (dayToCheck.getDayOfWeek() == DateTimeConstants.THURSDAY)) {
                    caRequestActive = true;
                }
                break;

            case MONDAY_TO_FRIDAY:
                if ((dayToCheck.getDayOfWeek() == DateTimeConstants.MONDAY) ||
                        (dayToCheck.getDayOfWeek() == DateTimeConstants.TUESDAY) ||
                        (dayToCheck.getDayOfWeek() == DateTimeConstants.WEDNESDAY) ||
                        (dayToCheck.getDayOfWeek() == DateTimeConstants.THURSDAY) ||
                        (dayToCheck.getDayOfWeek() == DateTimeConstants.FRIDAY)) {
                    caRequestActive = true;
                }
                break;

            case SATURDAY_AND_SUNDAY:
                if ((dayToCheck.getDayOfWeek() == DateTimeConstants.SATURDAY) ||
                        (dayToCheck.getDayOfWeek() == DateTimeConstants.SUNDAY)) {
                    caRequestActive = true;
                }
                break;

            default:
                throw new RuntimeException("Unknown Weekday enum: " + caRequest.getWeekdayCoverage());
            }
        }
        return caRequestActive;
    }

    /**
     * Filter out files that have alrady been ingested
     * 
     * @param dateOfIngest date and time of the current ingest
     * @param unFilteredOutputList List of all files that can be ingested
     * @return List of files that have not been ingested
     */
    protected List<MediaFileIngestOutputParameters> filterOutFilesAlreadyIngested(DateTime dateOfIngest, List<MediaFileIngestOutputParameters> unFilteredOutputList) {
        List<MediaFileIngestOutputParameters> filteredList = new ArrayList<MediaFileIngestOutputParameters>();
        for (MediaFileIngestOutputParameters fileIngest : unFilteredOutputList) {
            if (shouldInititateIngest(dateOfIngest, fileIngest.getFileNameSB())) {
                filteredList.add(fileIngest);
            }
        }
        return filteredList;
    }

    /**
     * Evalutates if a file should be ingested or not.
     * 
     * Ingest NOT if:
     * <ul>
     *   <li>If file has succesfully been ingested.</li>  
     *   <li>If ingest has been started within a property defined period of time, eg. 12 hours.</li> 
     * </ul>
     * 
     * @param dateOfIngest date and time of the current ingest
     * @param fileNameSB
     * @return
     */
    protected boolean shouldInititateIngest(DateTime dateOfIngest, String fileNameSB) {
        State state = workFlowStateMonitorFacade.getLastWorkFlowStateForEntity(fileNameSB);
        log.info(state);
        boolean initiateIngest = true;
        if (state != null) {
            if (state.getStateName().equals(finalWorkFlowStateName)) {
                initiateIngest = false;
            } else if (state.getDate().after(dateOfIngest.minusHours(expectedDurationOfFileIngestProcess).toDate())) {
                initiateIngest = false;
            }
        } 
        return initiateIngest;
    }

    /**
     * Converts ingest parameters to JSON format and outputs to the given PrintWriter.
     *
     * @param outputList of files that must be ingested
     * @param outputStream Where output is directed
     */
    protected void outputResult(List<MediaFileIngestOutputParameters> outputList, OutputStream outputStream) {
        boolean firstEntry = true;
        String output = " {\n"
                + "     \"downloads\":[";
        for (MediaFileIngestOutputParameters mediaFileIngestParameters : outputList) {
            String params = "\n"
                    + "         {\n"
                    + "            \"fileID\" : \"" +          mediaFileIngestParameters.getFileNameSB() + "\",\n"
                    + "            \"youSeeFilename\" : \"" +  mediaFileIngestParameters.getFileNameYouSee() + "\",\n"
                    + "            \"startTime\" : \"" +       youseeFilenameDateFormatter.print(mediaFileIngestParameters.getStartDate()) + "\",\n"
                    + "            \"endTime\" : \"" +         youseeFilenameDateFormatter.print(mediaFileIngestParameters.getEndDate()) + "\",\n"
                    + "            \"youseeChannelID\" : \"" + mediaFileIngestParameters.getChannelIDYouSee() + "\",\n"
                    + "            \"sbChannelID\" : \"" +     mediaFileIngestParameters.getChannelIDSB() + "\"\n"
                    + "         }";
            if (firstEntry) {
                output += params;
                firstEntry = false;
            } else {
                output += "," + params;
            }
        }
        output += "\n"
                + "     ]\n"
                + " }\n";
        try {
            log.debug("Writing output: " + output);
            outputStream.write(output.getBytes());
            log.debug("Closing output.");
            outputStream.close();
            log.debug("Closed output.");
        } catch (IOException e) {
            throw new RuntimeException("Unable to output to: " + outputStream, e);
        }
    }
}
