package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.stub;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dk.statsbiblioteket.mediaplatform.ingest.model.ChannelArchiveRequest;
import dk.statsbiblioteket.mediaplatform.ingest.model.WeekdayCoverage;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchiveRequestServiceIF;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ServiceException;

public class ChannelArchiveRequestServiceStub implements
ChannelArchiveRequestServiceIF {

    private ArrayList<ChannelArchiveRequest> cars = new ArrayList<ChannelArchiveRequest>();
    
    @SuppressWarnings("deprecation")
    @Override
    public List<ChannelArchiveRequest> getValidRequests(Date fromDate, Date toDate) {
        cars.add(createRequest(1, "dr1", WeekdayCoverage.DAILY, new Time(8, 0, 0), new Time(20,0 ,0), new Date(0), new Date(2030, 1, 1)));
        return cars;
    }

    public void addRequest(ChannelArchiveRequest car) {
        cars.add(car);
    }

    public static ChannelArchiveRequest createRequest(long id, String sBChannelId,
            WeekdayCoverage weekdayCoverage, Time fromTime, Time toTime,
            Date fromDate2, Date toDate2) {
        ChannelArchiveRequest car = new ChannelArchiveRequest();
        car.setId(id);
        car.setsBChannelId(sBChannelId);
        car.setWeekdayCoverage(weekdayCoverage);
        car.setFromTime(fromTime);
        car.setToTime(toTime);
        car.setFromDate(fromDate2);
        car.setToDate(toDate2);
        return car;
    }

    @Override
    public void delete(ChannelArchiveRequest arg0) throws ServiceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void insert(ChannelArchiveRequest arg0) throws ServiceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(ChannelArchiveRequest arg0) throws ServiceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<ChannelArchiveRequest> getAllRequests() throws ServiceException {
        // TODO Auto-generated method stub
        return null;
    }
}
