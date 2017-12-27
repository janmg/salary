package com.janmg.salary.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TimeEntry {
	// TODO: Changed to public because of bean accessibility
    // TODO: Find overlapping entries
	

	@Id
	@GeneratedValue
	public Long id;
	  
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

    // TODO: Fix Lombox, getters are so '90-ties
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