package com.janmg.salary.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TimeEntry {
	// TODO: Changed to public because of bean accessibility

	@Id	@GeneratedValue	public Long id;
	  
	public String name;
	//@OneToMany
	public int persid;
	public String date;
	public String start;
	public String end;

	@SuppressWarnings("unused")
    protected TimeEntry() {
    }

    public TimeEntry(int persid, String name, String date, String start, String end) {
    	this.name = name;
    	this.persid = persid;
    	this.date = date;
    	this.start = start;
    	this.end = end;
	}

    // TODO: Fix Lombok, getters are so '90-ties
    // https://stackoverflow.com/questions/34241718/lombok-builder-and-jpa-default-constructor#35602246
	public int getPersid() {
		return persid;
	}

    public String getDate() {
        return date;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }
    
    @Override
    public String toString() {
        return String.format("TimeEntry[id=%d, name='%s', date='%s', start='%s', end='%s']", persid, name, date, start, end);
    }
}