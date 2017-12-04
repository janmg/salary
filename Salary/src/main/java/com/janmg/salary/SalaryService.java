package com.janmg.salary;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Duration;
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

	// WTF: Java8 ZoneDateTime with timezone can track daylightsaving and can do date calculations and manipulations.
    private final String TIME_ZONE = "Europe/Helsinki";
    private final String STARTTIME = "6:00";
    private final String ENDTIME   = "18:00";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy H:mm");
 
    @PostConstruct
    @Transactional
    public void populate() throws IOException {

    	// Read CSV and populate JPA
    	Reader in = new InputStreamReader(getClass().getResourceAsStream("/HourList201403.csv"));
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
		
		ArrayList<TimeEntry> allEntries = new ArrayList<>();
		for (final CSVRecord rec : records) {
		    allEntries.add(new TimeEntry(rec.get("Person Name"), new Integer(rec.get("Person ID")), rec.get("Date"), rec.get("Start"), rec.get("End")));
		}
		repository.saveAll(allEntries);
    }

    public void calculate() {
    	ArrayList<TimeEntry> entries = repository.findDistinctPersid();
    	for (TimeEntry entry : entries) {
    		int persid = entry.getPersid();
    		System.out.println(persid);
    	}
    	
    	/*
    	
    	
		TreeMap<String,TimeEntry> allEntries = new TreeMap<>();
		repository.
		
		    float regular = 0;
		    float evening = 0;
		    
	        ZonedDateTime starttime = ZonedDateTime.of(LocalDateTime.parse(date+" "+start, formatter), ZoneId.of(TIME_ZONE));
	        ZonedDateTime endtime = ZonedDateTime.of(LocalDateTime.parse(date+" "+end, formatter), ZoneId.of(TIME_ZONE));
	        
	        // Working past midnight should not be punished
	    	if (endtime.isBefore(starttime)) {
	            endtime = ZonedDateTime.of(LocalDateTime.parse(date+" "+end, formatter).plusDays(1), ZoneId.of(TIME_ZONE));
	    	}

	    	// pk is the primary key only used for storing the employee_id and the date. pk is used to find multiple entries and combine them
		    String pk = persid+"_"+date;
		    TimeEntry entry;
		    if (allEntries.containsKey(pk));
		    {
		    	entry = allEntries.get(pk);
		    	allEntries.remove(pk);
				if (entry.getRegular()>0) regular = entry.getRegular();
				if (entry.getEvening()>0) evening = entry.getEvening();
		    }

		    // Calculate regular time
		    regular = regular + getMinutesBetween(starttime, endtime);

		    // Calculate evening time compensation (out-of-office hours)
		    evening = evening + calculateEveningtime(starttime, endtime);

		    // Calculate overtime multiplier;
		    float overtime = calculateOvertime(regular);
		    double pay = ((regular * overtime) + (1.15 * evening)); 

		}
		*/
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

    private float calculateOvertime(float regular) {
    	float multiplier = 0;
    	if (regular < 8) {
    		multiplier = regular;
    	} else {
    		multiplier = 8;
    		if (regular < 10) {
    			multiplier = multiplier + ((25/100) * (regular-8));
    		} else {
    			multiplier = multiplier + ((25/100) * 2);
    			if (regular < 12) {
    				multiplier = multiplier + ((50/100) * (regular-8));
    			} else {
    				multiplier = multiplier + ((50/100) * 2)  + ((100/100) * (regular-12));
    			}
    		}
    	}
		return multiplier;
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