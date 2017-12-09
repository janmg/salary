package com.janmg.salary;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.janmg.salary.domain.Employee;
import com.janmg.salary.domain.TimeEntry;
import com.janmg.salary.repository.EmployeeRepository;
import com.janmg.salary.repository.TimeRepository;
import com.janmg.salary.utils.Config;
import com.janmg.salary.utils.DateTime;

@Service
public class SalaryService {
 
    @Autowired
    private EmployeeRepository employees;
	@Autowired
    private TimeRepository timeRepository;
//    @Autowired
//    private TimeRepository calculatedRepository;

    Config conf = new Config();
    private final Log log = LogFactory.getLog(getClass());
    
    @PostConstruct
    @Transactional
    public void init() throws IOException {
    	// Read CSV and populate JPA
    	Reader in = new InputStreamReader(getClass().getResourceAsStream("/HourList201403.csv"));
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
		
		// TreeMap<String,CalculatedEntry>  = new TreeMap<>();
		
		ArrayList<TimeEntry> allEntries = new ArrayList<>();
		for (final CSVRecord rec : records) {
		    allEntries.add(new TimeEntry(rec.get("Person Name"), new Integer(rec.get("Person ID")), rec.get("Date"), rec.get("Start"), rec.get("End")));
		}
		timeRepository.saveAll(allEntries);
		
		// And finally calculate salary details
		calculate();
    }

    public void calculate() {
    	
    	ArrayList<Employee> entries = (ArrayList<Employee>) employees.findAll();
    	for (Employee entry : entries) {
    		int persid = entry.getPersid();
    	
    		TreeMap<String,CalculatedEntry> calc = new TreeMap<>();
		
		    int regular = 0;
		    int evening = 0;
		    
		    String date = "2.3.2014";
		    String start = "5:00";
		    String end = "14:00";


	    	// Load previous time entries
		    CalculatedEntry ce = calc.get(date);
/*
	    	// pk is the primary key only used for storing the employee_id and the date. pk is used to find multiple entries and combine them
		    String pk = persid+"_"+date;
		    TimeEntry entry;
		    if (allEntries.containsKey(pk));
		    {
		    	entry = allEntries.get(pk);
		    	allEntries.remove(pk);
				if (entry.getRegular()>0) regular = entry.getRegular();
				if (entry.getEvening()>0) evening = entry.getEvening();
		    }
*/

		    DateTime dt = new DateTime(conf.get("date.timezone"), conf.get("date.format"), conf.get("shift.start"), conf.get("shift.end"));
		    
		    // Calculate regular time
            regular =+ dt.calculateRegular(date, start, end);

		    // Calculate evening time compensation (out-of-office hours)
		    evening =+ dt.calculateEveningtime(date, start, end);

		    // Calculate overtime multiplier;
		    float overtime = dt.calculateOvertime(regular);
		    double pay = ((regular * overtime) + (conf.getRate("evening") * evening));
		    		    
		    // Calculate weekend and holidays
		    // TODO: not part of the assignment, but would be very useful

		    log.info("persid: " + persid + ", regular: "+regular+", evening: "+evening+", overtime: "+overtime+", pay: "+conf.getDenomination("default")+" "+pay);
    	}
    }

	@Transactional(readOnly=true)
    public List<TimeEntry> getAll() {
        return timeRepository.findAll();
    }
 
    @Transactional
    public TimeEntry saveAndFlush(TimeEntry te) {
 
        if ( te != null ) {
            te = timeRepository.saveAndFlush(te);
        }
        return te;
    }
 
    @Transactional
    public void delete(long id) {
    	timeRepository.deleteById(id);
    }
 
}