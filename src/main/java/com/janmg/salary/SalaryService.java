package com.janmg.salary;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.janmg.salary.domain.CalculatedEntry;
import com.janmg.salary.domain.Employee;
import com.janmg.salary.domain.TimeEntry;
import com.janmg.salary.repository.CalculatedRepository;
import com.janmg.salary.repository.EmployeeRepository;
import com.janmg.salary.repository.TimeRepository;
import com.janmg.salary.utils.Calculate;
import com.janmg.salary.utils.Config;

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
    
    public void upload(byte[] file) throws IOException {
    	// Read CSV and populate JPA
        Reader reader = new StringReader(new String(file));
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
		
		ArrayList<TimeEntry> allEntries = new ArrayList<>();
		for (final CSVRecord rec : records) {
			// parse all time records
		    int persid = new Integer(rec.get("Person ID"));
		    String name = rec.get("Person Name");
		    allEntries.add(new TimeEntry(persid, name, rec.get("Date"), rec.get("Start"), rec.get("End")));
		    
		    // learn all the employees
		    Employee emp = employeeRepo.findByPersid(persid);
		    if (emp != null) {
		    	if (!emp.getName().equals(name)) {
		    		log.error("employee-id "+persid+" with name "+name+" already exists as "+emp.getName());
		    	}
		    } else {
		    	employeeRepo.save(new Employee(persid,name,conf.getRate("default")));
		    }
		}
		try {
			timeRepo.saveAll(allEntries);
		} catch (Exception e) {
			log.debug("Number of loaded entries: "+allEntries.size()+", number in the repository "+timeRepo.count());
			log.error("Boo! File upload error: "+e.getLocalizedMessage());
		} finally {
			reader.close();
        }
    }

    public void reset() {
		timeRepo.deleteAll();
	}

    public void calculate() {
    	calculatedRepo.deleteAll();
		
    	ArrayList<Employee> employees = (ArrayList<Employee>) employeeRepo.findAll();
    	for (Employee employee : employees) {
    		int persid = employee.getPersid();

    		TreeMap<String,CalculatedDailyAmount> amounts = new TreeMap<>();
    		double totalpay = 0;
    		
    		ArrayList<TimeEntry> entries = (ArrayList<TimeEntry>) timeRepo.findByPersid(employee.getPersid());
		    for (TimeEntry time : entries)
		    {
                int regular = 0;
                int evening = 0;

		        if (amounts.containsKey(time.getDate())) {
		            // Remove first entries for same date and recalculate the payment
		            CalculatedDailyAmount cda = amounts.get(time.getDate());
		            regular = cda.getRegular();
		            evening = cda.getEvening();
	                totalpay -= cda.getPay();
	                amounts.remove(time.getDate());
		        }
		         
		        // Load previous time entries
		        Calculate calc = new Calculate(conf);
		    
    		    // Calculate regular time
                regular += calc.calculateRegular(time);
    
    		    // Calculate evening time compensation (out-of-office hours)
    		    evening += calc.calculateEveningtime(time);
    
    		    // Calculate overtime as regular time plus overtime added as extra 'regular' time.
    		    // For example 480min is 8h of regular time. 540min is 8h + 1h of overtime, which adds 15min to 555min 
    		    float overtime = calc.calculateOvertime(regular);
    		    double pay = ((conf.getRate("default") * overtime) + (conf.getRate("evening") * evening)) / 60;
    		    		    
    		    // Calculate weekend and holidays
    		    // TODO: not part of the assignment, but would be very useful

    		    amounts.put(time.getDate(), new CalculatedDailyAmount(regular, evening, pay));
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
    
	public void download() throws FileNotFoundException, UnsupportedEncodingException {
		// Read JPA and populate CSV
        PrintWriter writer = new PrintWriter("monthly-output.csv", "UTF-8");
        writer.println("Monthly Wages 03/2014:");
        List<CalculatedEntry> entries = calculatedRepo.findAll();
        for (CalculatedEntry ce : entries) {
            writer.println(ce.getPersid() + ", " + ce.getName() + ", " + conf.getDenomination() + ce.getPay());
        }
        writer.close();
	}
}