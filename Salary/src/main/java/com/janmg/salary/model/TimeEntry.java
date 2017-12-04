package com.janmg.salary.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.AbstractPersistable;

import lombok.Data;
import lombok.Getter;

// The Data annotation is for Lombok to autogenerate all getters
@Entity
@Table
@Data
public class TimeEntry extends AbstractPersistable<Long> {
	
	private @Id @GeneratedValue Long id;
	private String name;
	private int persid;
	private String date;
	private String start;
	private String end;
    
	public TimeEntry() {}
	
    public TimeEntry(String name, int persid, String date, String start, String end) {
    	// WTF: Using random id as PK. A composite primary key with Personnel ID and date with PrimaryKeyJoinColumn did not look pretty.
    	// https://en.wikibooks.org/wiki/Java_Persistence/Identity_and_Sequencing#Example_JPA_2.0_ManyToOne_id_annotation
    	this.name = name;
    	this.persid = persid;
    	this.date = date;
    	this.start = start;
    	this.end = end;
	}

    // TODO: Fix Lombok
    public int getPersid() {
    	return persid;
    }
    
    // TODO: Find overlapping entries
    // TODO: Find inconsistent name/id pairs
}
