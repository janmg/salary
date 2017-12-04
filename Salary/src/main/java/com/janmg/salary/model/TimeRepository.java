package com.janmg.salary.model;

import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeRepository extends JpaRepository<TimeEntry, Long> {

	ArrayList<TimeEntry> findDistinctPersid();
	//ArrayList<TimeEntry> findByPersid(String persid);
}
