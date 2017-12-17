package com.janmg.salary.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;
import lombok.Getter;

@Entity
@Data
public class CalculatedEntry {

	// a signed int has a max value of 2147483647, a timeentry can have maximum 2 days which is 172800 minutes, only nanosecond granularity will overflow this value. 
    @Id @GeneratedValue(strategy = GenerationType.AUTO) private long id;
    @Getter private int persid;
    @Getter private String name;
	@Getter private String pay;

    // https://spring.io/guides/gs/accessing-data-jpa/
    // The default constructor only exists for the sake of JPA
    @SuppressWarnings("unused")
    protected CalculatedEntry() {
    }

	public CalculatedEntry(int persid, String name, String pay) {
        this.persid = persid;
        this.name = name;
	    this.pay = pay;
	}
	
    public int getPersid() {
        return persid;
    }

    public String getName() {
        return name;
    }

    public String getPay() {
	    return pay;
	}
}
