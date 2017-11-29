package com.janmg.salary.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

import lombok.Data;
import lombok.Getter;

@Entity
@Table
@Data // Lombok for creating all getters
public class TimeEntry extends AbstractPersistable<Long> {
	private @Id @GeneratedValue Long id;
	private String name;
	private String persid;
	private String date;
	private String start;
	private String end;
	private String minutes;
    
	public TimeEntry() {}
	
    public TimeEntry(String name, String persid, String date, String start, String end) {
    	this.name = name;
    	this.persid = persid;
    	this.date = date;
    	this.start = start;
    	this.end = end;
    	this.minutes = getWorkingMinutes();
	}
    
    public String getWorkingMinutes() {
    	final String TIME_ZONE = "Europe/Helsinki";

    	// ZoneDateTime is used so that the timezones can take daylightsaving into account
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy H:mm");
        ZonedDateTime starttime = ZonedDateTime.of(LocalDateTime.parse(date+" "+start, formatter), ZoneId.of(TIME_ZONE));
        ZonedDateTime endtime = ZonedDateTime.of(LocalDateTime.parse(date+" "+end, formatter), ZoneId.of(TIME_ZONE));

        // Working past midnight
    	if (endtime.isBefore(starttime)) {
            endtime = ZonedDateTime.of(LocalDateTime.parse(date+" "+end, formatter).plusDays(1), ZoneId.of(TIME_ZONE));
    	}
    	return ((int)Duration.between(starttime, endtime).toMinutes())+"";
    }
    
    // TODO: Find overlapping entries
    // TODO: Find inconsistent name/id pairs
    // TODO: Fix Daylight saving time
}
