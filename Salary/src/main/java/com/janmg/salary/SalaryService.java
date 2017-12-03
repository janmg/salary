package com.janmg.salary;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.janmg.salary.model.TimeEntry;
import com.janmg.salary.model.TimeRepository;

@Service
@Repository
public class SalaryService {
 
    @Autowired
    private TimeRepository repository;

	// WTF: ZoneDateTime with timezone can track daylightsaving and allows for date calculations.
    private final String TIME_ZONE = "Europe/Helsinki";
    private final String STARTTIME = "6:00";
    private final String ENDTIME   = "18:00";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy H:mm");
 
    @PostConstruct
    @Transactional
    public void populate() throws IOException {
 
    	Reader in = new InputStreamReader(getClass().getResourceAsStream("/HourList201403.csv"));
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

		for (final CSVRecord record : records) {
		    int regular = 0;
		    int evening = 0;

		    String name = record.get("Person Name");
		    String persid = record.get("Person ID");

		    String date = record.get("Date");
		    String start = record.get("Start");
		    String end = record.get("End");
		    
	        ZonedDateTime starttime = ZonedDateTime.of(LocalDateTime.parse(date+" "+start, formatter), ZoneId.of(TIME_ZONE));
	        ZonedDateTime endtime = ZonedDateTime.of(LocalDateTime.parse(date+" "+end, formatter), ZoneId.of(TIME_ZONE));
	        
	        // Working past midnight should not be punished
	    	if (endtime.isBefore(starttime)) {
	            endtime = ZonedDateTime.of(LocalDateTime.parse(date+" "+end, formatter).plusDays(1), ZoneId.of(TIME_ZONE));
	    	}

	    	// TODO: convert back to ArrayList and do not save to persistent storage for every record. (Maybe TreeMap?)
		    String pk = persid+"_"+date;
		    try {
		    	TimeEntry entry = repository.findByName(pk);
			    if (entry.getRegular()>0) regular = entry.getRegular();
			    if (entry.getEvening()>0) evening = entry.getEvening();
			    if (entry.getOvertime()>0) evening = entry.getOvertime();
		    } catch (NullPointerException e) {
		    	// WTF If there is no previous entry, ignore and continue ... this is bad/lazy code, should really check first 
		    }

		    // Calculate regular time
		    regular = regular + getMinutesBetween(starttime, endtime);

		    // Calculate evening time compensation (out-of-office hours)
		    evening = evening + calculateEveningtime(starttime, endtime);
		    int overtime = 0;
		    repository.saveAndFlush(new TimeEntry(name, persid, date, regular, evening, overtime));
		}
    }
 
    private int calculateEveningtime(ZonedDateTime starttime, ZonedDateTime endtime) {
    	int evening = 0;
    	// Set start and end of regular hours based on the date of the starttime, by resetting the time to "6:00" and "18:00"
    	ZonedDateTime morningtime = starttime.withHour(new Integer(STARTTIME.split(":")[0])).withMinute(new Integer(STARTTIME.split(":")[1]));
    	ZonedDateTime eveningtime = starttime.withHour(new Integer(ENDTIME.split(":")[0])).withMinute(new Integer(ENDTIME.split(":")[1]));;

    	// overtime in the morning
        if (endtime.isBefore(morningtime)) morningtime = endtime;
	    int under = getMinutesBetween(starttime, morningtime);
	    if (under > 0) 
	    	{ evening = evening + under;}

	    // overtime in the evening
	    if (starttime.isAfter(eveningtime)) eveningtime = starttime;
	    int over = getMinutesBetween(eveningtime, endtime);
	    if (over > 0)
	    	{evening = evening + over;}

		return evening;
	}

	@Transactional(readOnly=true)
    public List<TimeEntry> getAll() {
 
        return repository.findAll();
 
    }
 
    @Transactional
    public TimeEntry saveAndFlush(TimeEntry te) {
 
        if ( te != null ) {
            te = repository.saveAndFlush(te);
        }
 
        return te;
 
    }
 
    @Transactional
    public void delete(long id) {
 
    	repository.deleteById(id);
 
    }
    
    private int getMinutesBetween(ZonedDateTime starttime, ZonedDateTime endtime) {
    	return (int)Duration.between(starttime, endtime).toMinutes();
    }
 
}