package com.janmg.salary;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.janmg.salary.domain.CalculatedEntry;
import com.janmg.salary.domain.Employee;
import com.janmg.salary.domain.TimeEntry;
import com.janmg.salary.repository.CalculatedRepository;
import com.janmg.salary.repository.EmployeeRepository;
import com.janmg.salary.repository.TimeRepository;
import com.janmg.salary.utils.Config;
import com.janmg.salary.utils.DateTime;

@Service
public class SalaryService {
 
    @Autowired
    private EmployeeRepository employeeRepo;
	@Autowired
    private TimeRepository timeRepo;
    @Autowired
    private CalculatedRepository calculatedRepo;

    Config conf = new Config();
    private final Log log = LogFactory.getLog(getClass());
    
    @PostConstruct
    @Transactional
    public void init() throws IOException {
    	// Read CSV and populate JPA
    	Reader in = new InputStreamReader(getClass().getResourceAsStream("/HourList201403.csv"));
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
		
		ArrayList<TimeEntry> allEntries = new ArrayList<>();
		for (final CSVRecord rec : records) {
		    int persid = new Integer(rec.get("Person ID"));
		    String name = rec.get("Person Name");
		    allEntries.add(new TimeEntry(persid, name, rec.get("Date"), rec.get("Start"), rec.get("End")));
		}
		timeRepo.saveAll(allEntries);

		// And finally calculate salary details
		calculate();
    }

    private void calculate() {
    	
    	ArrayList<Employee> employees = (ArrayList<Employee>) employeeRepo.findAll();
    	for (Employee employee : employees) {
    		int persid = employee.getPersid();

    		TreeMap<String,CalculatedDailyAmount> calc = new TreeMap<>();
    		double totalpay = 0;
    		
    		ArrayList<TimeEntry> entries = (ArrayList<TimeEntry>) timeRepo.findByPersid(employee.getPersid());
		    for (TimeEntry time : entries)
		    {
                int regular = 0;
                int evening = 0;

		        if (calc.containsKey(time.getDate())) {
		            // Remove first entries for same date and recalculate the payment
		            CalculatedDailyAmount cda = calc.get(time.getDate());
		            regular = cda.getRegular();
		            evening = cda.getEvening();
	                totalpay -= cda.getPay();
		            calc.remove(time.getDate());
		        }
		         
		        // Load previous time entries
		        // CalculatedEntry ce = calc.get(date);
		        DateTime dt = new DateTime(conf.get("date.timezone"), conf.get("date.format"), conf.get("shift.start"), conf.get("shift.end"));
		    
    		    // Calculate regular time
                regular += dt.calculateRegular(time);
    
    		    // Calculate evening time compensation (out-of-office hours)
    		    evening += dt.calculateEveningtime(time);
    
    		    // Calculate overtime as regular time plus overtime added as extra 'regular' time.
    		    // For example 480min is 8h of regular time. 540min is 8h + 1h of overtime, which adds 15min to 555min 
    		    float overtime = dt.calculateOvertime(regular);
    		    double pay = ((conf.getRate("default") * overtime) + (conf.getRate("evening") * evening)) / 60;
    		    		    
    		    // Calculate weekend and holidays
    		    // TODO: not part of the assignment, but would be very useful

    		    calc.put(time.getDate(), new CalculatedDailyAmount(regular, evening, pay));
    		    totalpay += pay;
    		    log.debug("persid: " + persid + ", time: "+time.getDate()+" "+time.getStart()+"-"+time.getEnd()+", regular: "+regular+", evening: "+evening+", overtime: "+overtime+", pay: "+conf.getDenomination("default")+pay);
        	}
		    
		    String name = employee.getName();
		    
		    ArrayList<CalculatedEntry> list = new ArrayList<>();
		    list.add(new CalculatedEntry(persid, name, String.format("%.2f", totalpay)));
	        calculatedRepo.saveAll(list);
		    
		    log.info("persid: " + persid + " has earned: " + totalpay);
    	}
    }
}