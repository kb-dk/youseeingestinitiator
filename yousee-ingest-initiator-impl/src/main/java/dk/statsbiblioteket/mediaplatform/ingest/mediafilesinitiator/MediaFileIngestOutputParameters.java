package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import org.joda.time.DateTime;

/**
 * This class represents container objects for the output of the initiator. The output
 * consists of a list of objects of this class.
 * 
 * Each instans represents media file that must be downloaded and ingested to SB's 
 * media platform. The information contained in the objects are required in the tasks
 * later in the workflow. 
 * 
 * The primary field is the filename of the media file that is planned to get downloaded
 * from YouSee. 
 * 
 * @author henningbottger
 *
 */
public class MediaFileIngestOutputParameters implements Comparable<MediaFileIngestOutputParameters> {

    /** The SB channel id of the medie that is planned for ingest. */
    public final String channelIDSB;
    
    /** The YouSee channel id */
    public final String channelIDYouSee;
    
    /** The start time (whole hour) of the media, ie. 2012-03-04 14:00:00 */
    public final DateTime startDate; // YYYYMMDDHHMMSS

    /** The end time (whole hour) of the media, ie. 2012-03-04 15:00:00 */
    public final DateTime endDate; // YYYYMMDDHHMMSS
    
    /** The filename of the media as found on the YouSee ftp-server */
    public final String fileNameYouSee;

    /** The filename of the media in the archive */
    public final String fileNameSB;

    public MediaFileIngestOutputParameters(String filenameSB, String youseeFilename, String channelIDSB, String channelIDYouSee, DateTime startDate, DateTime endDate) {
        this.fileNameYouSee = youseeFilename;
        this.fileNameSB = filenameSB;
        this.channelIDSB = channelIDSB;
        this.channelIDYouSee = channelIDYouSee;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getChannelIDSB() {
        return channelIDSB;
    }

    public String getChannelIDYouSee() {
        return channelIDYouSee;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public String getFileNameYouSee() {
        return fileNameYouSee;
    }

    public String getFileNameSB() {
        return fileNameSB;
    }

    @Override
    public int compareTo(MediaFileIngestOutputParameters other) {
        return this.startDate.compareTo(other.startDate);
    }

    @Override
    public String toString() {
        return "MediaFileIngestOutputParameters [channelIDSB=" + channelIDSB
                + ", channelIDYouSee=" + channelIDYouSee + ", startDate="
                + startDate + ", endDate=" + endDate + ", fileNameYouSee="
                + fileNameYouSee + ", fileNameSB=" + fileNameSB + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((channelIDSB == null) ? 0 : channelIDSB.hashCode());
        result = prime * result
                + ((channelIDYouSee == null) ? 0 : channelIDYouSee.hashCode());
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result
                + ((fileNameSB == null) ? 0 : fileNameSB.hashCode());
        result = prime * result
                + ((fileNameYouSee == null) ? 0 : fileNameYouSee.hashCode());
        result = prime * result
                + ((startDate == null) ? 0 : startDate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MediaFileIngestOutputParameters other = (MediaFileIngestOutputParameters) obj;
        if (channelIDSB == null) {
            if (other.channelIDSB != null)
                return false;
        } else if (!channelIDSB.equals(other.channelIDSB))
            return false;
        if (channelIDYouSee == null) {
            if (other.channelIDYouSee != null)
                return false;
        } else if (!channelIDYouSee.equals(other.channelIDYouSee))
            return false;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (fileNameSB == null) {
            if (other.fileNameSB != null)
                return false;
        } else if (!fileNameSB.equals(other.fileNameSB))
            return false;
        if (fileNameYouSee == null) {
            if (other.fileNameYouSee != null)
                return false;
        } else if (!fileNameYouSee.equals(other.fileNameYouSee))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        return true;
    }
}
