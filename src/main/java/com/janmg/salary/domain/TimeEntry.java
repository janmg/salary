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
	@Getter private String name;
	//@OneToMany
	@Getter private int persid;
	@Getter private String date;
	@Getter private String start;
	@Getter private String end;
    
    protected TimeEntry() {
    }

    public TimeEntry(String name, int persid, String date, String start, String end) {
    	this.name = name;
    	this.persid = persid;
    	this.date = date;
    	this.start = start;
    	this.end = end;
	}

	public int getPersid() {
		return persid;
	}

    // TODO: Find overlapping entries
    // TODO: Find inconsistent name/id pairs
}