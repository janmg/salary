package com.janmg.salary;

import lombok.Data;
import lombok.Getter;

@Data
public class CalculatedDailyAmount {

	// a signed int has a max value of 2147483647, a timeentry can have maximum 2 days which is 172800 minutes, only nanosecond granularity will overflow this value. 
    @Getter private int regular; // total amount of minutes worked on a day
    @Getter private int evening; // total amount of minutes worked outside of office hours
	@Getter private double pay; // multiplier for overtime calculation

	public CalculatedDailyAmount(int regular, int evening, double pay) {
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
}
