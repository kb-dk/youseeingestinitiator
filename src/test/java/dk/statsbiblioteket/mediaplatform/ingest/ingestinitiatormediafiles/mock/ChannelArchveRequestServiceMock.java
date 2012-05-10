package dk.statsbiblioteket.mediaplatform.ingest.ingestinitiatormediafiles.mock;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import dk.statsbiblioteket.mediaplatform.ingest.model.ChannelArchiveRequest;
import dk.statsbiblioteket.mediaplatform.ingest.model.WeekdayCoverage;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchveRequestServiceIF;

public class ChannelArchveRequestServiceMock implements
        ChannelArchveRequestServiceIF {

    @Override
    public List<ChannelArchiveRequest> getValidRequests(Date fromDate, Date toDate) {
        ArrayList<ChannelArchiveRequest> cars = new ArrayList<ChannelArchiveRequest>();
        ChannelArchiveRequest car = new ChannelArchiveRequest();
        car.setId(1L);
        car.setsBChannelId("dr1");
        car.setWeekdayCoverage(WeekdayCoverage.MONDAY);
        car.setFromTime(new Time(8, 0, 0));
        car.setToTime(new Time(20, 0, 0));
        car.setFromDate(new Date(0));
        car.setToDate(new DateTime().plusMonths(3).toDate());
        cars.add(car);
        return cars;
    }

}
