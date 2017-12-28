package com.janmg.salary.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TimeEntry {
    // TODO: last minute change from full date as string to split to days and monthyear, this would require some rewriting to solve elegantly.
    private final String DEL = ".";

    // TODO: Changed to public because of bean accessibility
	@Id	@GeneratedValue	public Long id;
	public String name;
	//@OneToMany
	public int persid;
	public String monthyear;
    public String day;
	public String start;
	public String end;

	@SuppressWarnings("unused")
    protected TimeEntry() {
    }

    public TimeEntry(int persid, String name, String date, String start, String end) {
    	this.name = name;
    	this.persid = persid;
    	this.monthyear = date.substring(date.indexOf(DEL)+1);
        this.day = date.substring(0, date.indexOf(DEL));
    	this.start = start;
    	this.end = end;
	}

    // TODO: Fix Lombok, getters are so '90-ties
    // https://stackoverflow.com/questions/34241718/lombok-builder-and-jpa-default-constructor#35602246
	public int getPersid() {
		return persid;
	}

    public String getDate() {
        return day+DEL+monthyear;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }
    
    @Override
    public String toString() {
        return String.format("TimeEntry[id=%d, name='%s', date='%s', start='%s', end='%s']", persid, name, getDate(), start, end);
    }
}