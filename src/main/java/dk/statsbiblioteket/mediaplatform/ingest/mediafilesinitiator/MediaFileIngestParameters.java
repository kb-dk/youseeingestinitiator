package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator;

import org.joda.time.DateTime;

public class MediaFileIngestParameters implements Comparable<MediaFileIngestParameters> {

    public String channelIDSB;
    public String channelIDYouSee;
    public DateTime startDate; // YYYYMMDDHHMMSS
    public DateTime endDate; // YYYYMMDDHHMMSS
    public String youseeFileName;
    
    public MediaFileIngestParameters(String channelIDSB, DateTime startDate, DateTime endDate) {
        this.channelIDSB = channelIDSB;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public MediaFileIngestParameters(String youseeFilename, String channelIDSB, String channelIDYouSee, DateTime startDate, DateTime endDate) {
        this.youseeFileName = youseeFilename;
        this.channelIDSB = channelIDSB;
        this.channelIDYouSee = channelIDYouSee;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getChannelIDSB() {
        return channelIDSB;
    }

    public void setChannelIDSB(String channelIDSB) {
        this.channelIDSB = channelIDSB;
    }

    public String getChannelIDYouSee() {
        return channelIDYouSee;
    }

    public void setChannelIDYouSee(String channelIDYouSee) {
        this.channelIDYouSee = channelIDYouSee;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public String getYouseeFileName() {
        return youseeFileName;
    }

    public void setYouseeFileName(String youseeFileName) {
        this.youseeFileName = youseeFileName;
    }

    @Override
    public int compareTo(MediaFileIngestParameters other) {
        return this.startDate.compareTo(other.startDate);
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
                + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result
                + ((youseeFileName == null) ? 0 : youseeFileName.hashCode());
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
        MediaFileIngestParameters other = (MediaFileIngestParameters) obj;
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
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (youseeFileName == null) {
            if (other.youseeFileName != null)
                return false;
        } else if (!youseeFileName.equals(other.youseeFileName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MediaFileIngestParameters [channelIDSB=" + channelIDSB
                + ", channelIDYouSee=" + channelIDYouSee + ", startDate="
                + startDate + ", endDate=" + endDate + ", youseeFileName="
                + youseeFileName + "]";
    }
    
    
}
