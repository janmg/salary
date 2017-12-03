package com.janmg.salary.model;

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
	private String pk;
	private String name;
	private String persid;
	private String date;
	private String start;
	private String end;
	@Getter private int regular;
	@Getter private int evening;
	@Getter private int overtime;
    
	public TimeEntry() {}
	
    public TimeEntry(String name, String persid, String date, int regular, int evening, int overtime) {
    	// WTF: Using Personnel ID and data as primary key instead of @ID ... PrimaryKeyJoinColumn did not look pretty.
    	// https://en.wikibooks.org/wiki/Java_Persistence/Identity_and_Sequencing#Example_JPA_2.0_ManyToOne_id_annotation
    	this.pk = persid+"_"+date;
    	this.name = name;
    	this.persid = persid;
    	this.date = date;
    	this.regular = regular;
    	this.evening = evening;
    	this.overtime = overtime;
    	//this.minutes = getWorkingMinutes();
    	
	}

	public int getRegular() {
		return regular;
	}
	public int getOvertime() {
		return overtime;
	}
	public int getEvening() {
		return evening;
	}

	
    // TODO: Find overlapping entries
    // TODO: Find inconsistent name/id pairs
    // TODO: Fix Daylight saving time
}
