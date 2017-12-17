package com.janmg.salary.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;

@Entity
public class Employee {

	@Id int persid;
	@Getter private String name;
	@Getter private double hourlyrate;
	
	// https://spring.io/guides/gs/accessing-data-jpa/
	// The default constructor only exists for the sake of JPA
	@SuppressWarnings("unused")
	protected Employee() {
	}
	
	public Employee(int persid, String name, double hourlyrate) {
		this.name = name;
		this.persid = persid;
		this.hourlyrate = hourlyrate;
	}

	public int getPersid() {
		return persid;
	}

	public String getName() {
        return name;
    }
	
    @Override
    public String toString() {
        return String.format("Employee[id=%d, name='%s', rate='%s']", persid, name, hourlyrate);
    }
}
