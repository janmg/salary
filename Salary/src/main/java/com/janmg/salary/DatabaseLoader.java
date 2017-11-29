package com.janmg.salary;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.janmg.salary.model.TimeEntry;
import com.janmg.salary.model.TimeRepository;

@Component
public class DatabaseLoader implements CommandLineRunner {

	private final TimeRepository repository;

	@Autowired
	public DatabaseLoader(TimeRepository repository) {
		this.repository = repository;
	}

	@Override
	public void run(String... strings) throws Exception {
    	Reader in = new InputStreamReader(getClass().getResourceAsStream("/HourList201403.csv"));
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

		ArrayList<TimeEntry> allEntries = new ArrayList<>();
		for (final CSVRecord record : records) {
		    String name = record.get("Person Name");
		    String persid = record.get("Person ID");
		    String date = record.get("Date");
		    String start = record.get("Start");
		    String end = record.get("End");

		    allEntries.add(new TimeEntry(name,persid,date,start,end));
		}
		this.repository.saveAll(allEntries);
	}
}