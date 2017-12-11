package com.janmg.salary;

import lombok.Data;
import lombok.Getter;

@Data
public class CalculatedEntry {

	// a signed int has a max value of 2147483647, a timeentry can have maximum 2 days which is 172800 minutes, only nanosecond granularity will overflow this value. 
	@Getter private int persid; // personnel id
	@Getter private int minutes; // total amount of minutes worked on one day
	@Getter private int evening; // total amount of hours worked outside of office hours
	@Getter private int multiplier; // multiplier for overtime calculation

	public CalculatedEntry(int persid, int minutes, int evening, int multiplier) {
		this.persid = persid;
    	this.minutes = minutes;
    	this.evening = evening;
    	this.multiplier = multiplier;
	}
	
}
