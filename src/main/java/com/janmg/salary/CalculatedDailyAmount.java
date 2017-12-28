package com.janmg.salary;

import org.apache.commons.lang3.Range;

import lombok.Data;
import lombok.Getter;

@Data
public class CalculatedDailyAmount {

	// a signed int has a max value of 2147483647, a timeentry can have maximum 2 days which is 172800 minutes, only nanosecond granularity will overflow this value.
    @Getter private Range<Integer> range; // range
    @Getter private int regular; // total amount of minutes worked on a day
    @Getter private int evening; // total amount of minutes worked outside of office hours
	@Getter private double pay; // multiplier for overtime calculation

	public CalculatedDailyAmount(Range range, int regular, int evening, double pay) {
		this.range = range;
	    this.regular = regular;
	    this.evening = evening;
	    this.pay = pay;
	}

	public int getRegular() {
	    return regular;
	}
	
	public int getEvening() {
	    return evening;
	}
    public double getPay() {
	    return pay;
	}

	public Range<Integer> getRange() {
		return range;
	}
}
