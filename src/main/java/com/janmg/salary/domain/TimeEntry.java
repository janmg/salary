package com.janmg.salary.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.data.jpa.domain.AbstractPersistable;

import lombok.Getter;

@Entity
public class TimeEntry extends AbstractPersistable<Long> {
	
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	// TODO: Changed to public because of bean accessibility
	@Getter public String name;
	//@OneToMany
	@Getter public int persid;
	@Getter public String date;
	@Getter public String start;
	@Getter public String end;
    
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

    // TODO: Find overlapping entries
}