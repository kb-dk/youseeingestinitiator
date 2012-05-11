package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.jfree.util.Log;
import org.joda.time.DateTime;

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
        // Check args
        System.err.println("Starting initiation process...");
        if (args.length != 2) {
            System.err.println("At least two arguments must be supplied.");
            System.err.println("Parameter required: <path_to_property_file> <date_to_initiate>");
            System.err.println(" -path_to_property_file - full filename and path to property file");
            System.err.println(" -date_to_initiate - date for which the ingest is based");
            System.exit(1);
        }
        // Get properties
        System.err.println("Parsing property file from argument");
        String filenameAndPath = args[0];
        Properties properties = null;
        try {
            properties = getPropertiesFromPropertyFile(filenameAndPath);
        } catch (IOException e) {
            String msg = "Error reading property file from path: " + filenameAndPath;
            errorMessage(e, msg);
            System.exit(2);
        }
        // Get date to base ingest on
        System.err.println("Parsing property date from argument");
        String ingestBaseTimeString = args[1];
        DateTime ingestBaseTime = getIngestBaseTime(ingestBaseTimeString);
        // Create classes to inject into initiator and construct initiator
        System.err.println("Creating initiator..." );
        try {
            IngestMediaFilesInitiator ingestInitiatorMediaFiles = IngestMediaFilesInitiatorFactory.create(properties);
            // Start initator
            System.err.println("Starting initiator..." );
            ingestInitiatorMediaFiles.start(ingestBaseTime);
        } catch (MissingPropertyException e) {
            String msg = "Error reading property from file: " + filenameAndPath;
            errorMessage(e, msg);
            System.exit(3);
        }
    }

    private static Properties getPropertiesFromPropertyFile(String filenameAndPath) throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(filenameAndPath));
        return properties;
    }

    private static DateTime getIngestBaseTime(String ingestBaseTimeString) {
        //System.err.println("Dummy date used for ingest base time. Should have extracted date from: " + ingestBaseTimeString);
        return new DateTime(1968, 1, 1, 0, 0, 0, 0);
    }

    public static void errorMessage(Exception e, String msg) {
        System.err.println(msg);
        System.err.println("Error message: " + e.getMessage());
        System.err.println("Error stacktrace:");
        System.err.println(e.getStackTrace());
    }
}