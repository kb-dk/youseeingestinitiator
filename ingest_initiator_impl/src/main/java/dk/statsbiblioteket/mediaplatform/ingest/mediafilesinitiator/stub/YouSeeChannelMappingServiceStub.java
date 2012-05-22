package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.stub;

import java.util.Date;
import java.util.List;

import dk.statsbiblioteket.mediaplatform.ingest.model.YouSeeChannelMapping;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ServiceException;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.YouSeeChannelMappingServiceIF;

public class YouSeeChannelMappingServiceStub implements
        YouSeeChannelMappingServiceIF {

    @Override
    public YouSeeChannelMapping getUniqueMappingFromYouSeeChannelId(
            String youSeeChannelId, Date date) throws ServiceException {
        return getDR1ChannelMapping();
    }

    @Override
    public YouSeeChannelMapping getUniqueMappingFromSbChannelId(
            String sBChannelId, Date date) throws ServiceException {
        return getDR1ChannelMapping();
    }

    @SuppressWarnings("deprecation")
    private YouSeeChannelMapping getDR1ChannelMapping() {
        YouSeeChannelMapping youSeeChannelMapping = new YouSeeChannelMapping();
        youSeeChannelMapping.setId(0L);
        youSeeChannelMapping.setDisplayName("DR1");
        youSeeChannelMapping.setYouSeeChannelId("DR1");
        youSeeChannelMapping.setSbChannelId("dr1");
        youSeeChannelMapping.setFromDate(new Date(0));
        youSeeChannelMapping.setToDate(new Date(2030, 1, 1));
        return youSeeChannelMapping;
    }

    @Override
    public List<YouSeeChannelMapping> getAllMappings() throws ServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void create(YouSeeChannelMapping arg0) throws ServiceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(YouSeeChannelMapping arg0) throws ServiceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(YouSeeChannelMapping arg0) throws ServiceException {
        // TODO Auto-generated method stub
        
    }

}
