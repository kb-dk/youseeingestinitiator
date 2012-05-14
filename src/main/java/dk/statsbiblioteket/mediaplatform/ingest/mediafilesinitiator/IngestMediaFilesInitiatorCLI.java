package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.jfree.util.Log;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class IngestMediaFilesInitiatorCLI {

    /**
     * Starts media file ingest initiator with the following setup:
     * 
     * <ol>
     *   <li>Database access to DigiTV database</li>
     *   <li>Log4J logger</li>
     *   <li>Output to stdout</li>
     *   <li>Properties found in property file specified as first argument</li>
     *   <li>Date for which the ingest is initiated given as second argument</li>
     * </ol>
     * 
     * @param args
     * <ol>
     *   <li>path_to_property_file - full filename and path to property file</li>
     *   <li>date_to_initiate - date for which the ingest is based</li>
     * </ol>
     */
    public static void main(String[] args) {
        try {
            // Check args
            System.err.println("Starting initiation process...");
            if (args.length != 2) {
                System.err.println("At least two arguments must be supplied.");
                System.err.println("Parameter required: <path_to_property_file> <date_to_initiate>");
                System.err.println(" -path_to_property_file - full filename and path to property file");
                System.err.println(" -date_to_initiate - date for which the ingest is based. Format yyyy-MM-dd.");
                System.exit(1);
            }
            // Get properties
            System.err.println("Parsing property file from argument");
            String filenameAndPath = args[0];
            Properties properties = null;
            properties = getPropertiesFromPropertyFile(filenameAndPath);
            // Get date to base ingest on
            System.err.println("Parsing property date from argument");
            String ingestBaseTimeString = args[1];
            DateTime ingestBaseTime = getIngestBaseTime(ingestBaseTimeString);
            // Create classes to inject into initiator and construct initiator
            System.err.println("Creating initiator..." );
            IngestMediaFilesInitiator ingestInitiatorMediaFiles = IngestMediaFilesInitiatorFactory.create(properties);
            // Start initator
            System.err.println("Starting initiator with base time: " + ingestBaseTime);
            ingestInitiatorMediaFiles.initiateIngest(ingestBaseTime);
        } catch (Exception e) {
            System.err.println("An unrecoverable error occured.");
            System.err.println("Error message: " + e.getMessage());
            System.err.println("Stacktrace:");
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static Properties getPropertiesFromPropertyFile(String filenameAndPath) throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(filenameAndPath));
        return properties;
    }

    private static DateTime getIngestBaseTime(String ingestBaseTimeString) {
        DateTime inputDate = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(ingestBaseTimeString);
        Log.debug("Input date parsed from parameter: " + inputDate);
        return inputDate;
    }
}
