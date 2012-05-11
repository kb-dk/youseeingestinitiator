package dk.statsbiblioteket.mediaplatform.ingest.mediafilesinitiator.mock;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import dk.statsbiblioteket.mediaplatform.ingest.model.ChannelArchiveRequest;
import dk.statsbiblioteket.mediaplatform.ingest.model.WeekdayCoverage;
import dk.statsbiblioteket.mediaplatform.ingest.model.service.ChannelArchiveRequestServiceIF;

public class ChannelArchiveRequestServiceStub implements
ChannelArchiveRequestServiceIF {

    @Override
    public List<ChannelArchiveRequest> getValidRequests(Date fromDate, Date toDate) {
        ArrayList<ChannelArchiveRequest> cars = new ArrayList<ChannelArchiveRequest>();
        cars.add(createCAR(1L, "dr1", WeekdayCoverage.MONDAY, new Time(8, 0, 0), new Time(20, 0, 0), new Date(0), new DateTime().plusMonths(3).toDate()));
        cars.add(createCAR(2L, "dr2", WeekdayCoverage.DAILY, new Time(8, 0, 0), new Time(20, 0, 0), new Date(0), new DateTime().plusMonths(3).toDate()));
        return cars;
    }

    public ChannelArchiveRequest createCAR(long id, String sBChannelId,
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
}
