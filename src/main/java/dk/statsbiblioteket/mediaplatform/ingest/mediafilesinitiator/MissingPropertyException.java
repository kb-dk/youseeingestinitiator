package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

@SuppressWarnings("serial")
public class MissingPropertyException extends Exception {

    public MissingPropertyException(String msg) {
        super(msg);
    }

}
