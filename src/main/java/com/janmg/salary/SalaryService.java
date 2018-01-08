package com.janmg.salary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.Range;
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
import com.janmg.salary.utils.Config;
import com.janmg.salary.utils.DateTime;
import com.janmg.salary.utils.TimeCalc;

@Service
public class SalaryService {

    @Autowired private EmployeeRepository employeeRepo;
    @Autowired private TimeRepository timeRepo;
    @Autowired private CalculatedRepository calculatedRepo;
    
	// Calculate works with DateTime to manipulate dates using Java8 ZonedDateTime, not very pretty though
    Config conf = new Config();
    TimeCalc calc = new TimeCalc();
    private final Log log = LogFactory.getLog(getClass());
    
    public void upload(byte[] file) throws IOException {
        Reader reader = new StringReader(new String(file));
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
		
		ArrayList<TimeEntry> allEntries = new ArrayList<>();
		for (final CSVRecord rec : records) {

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
			log.error("Boo! File upload error: "+e.getLocalizedMessage());
			log.debug("Number of loaded entries: "+allEntries.size()+", number in the repository "+timeRepo.count());
		} finally {
			reader.close();
        }
    }

	public void reset() {
		timeRepo.deleteAll();
		employeeRepo.deleteAll();
		calculatedRepo.deleteAll();
	}

	public void calculate() {
		
		DateTime dt = new DateTime(conf);
    	calculatedRepo.deleteAll();

        List<String> months = timeRepo.findDistinctByMonthyear();
        for (String month : months) {

        	ArrayList<Employee> employees = (ArrayList<Employee>) employeeRepo.findAll();
        	for (Employee employee : employees) {
        		int persid = employee.getPersid();

        		TreeMap<Integer, CalculatedDailyAmount> amounts = new TreeMap<>();
        		double totalpay = 0;

            	List<Range<Integer>> ranges = new ArrayList<Range<Integer>>();
        		ArrayList<TimeEntry> entries = (ArrayList<TimeEntry>) timeRepo.findByPersidAndMonthyear(employee.getPersid(), month);
    		    for (TimeEntry time : entries)
    		    {
    		    	int day = new Integer(time.getDate().split("\\.")[0]);
                    int regular = 0;
                    int evening = 0;
            	    Range<Integer> check = dt.asRange(time);
            	    
            	    boolean isUniqueRange = true;
    	        	for (Range<Integer>range : ranges) {
    	        	    if (range.isOverlappedBy(check)) {
    	        	    	log.error("Time range overlaps, not adding this range in the calculations");
    	        	    	isUniqueRange = false;
    	        	    	break;
    	        	    }
    	        	}
    	        	
    	        	if (isUniqueRange) {
    	        		ranges.add(check);
    			        if (amounts.containsKey(day)) {
    			        	// Remove first entries for same date and recalculate the payment
    			        	CalculatedDailyAmount cda = amounts.get(day);
    			            regular = cda.getRegular();
    			            evening = cda.getEvening();
    		                totalpay -= cda.getPay();
    		                amounts.remove(day);
                        }
    
                        // Calculate regular time
                        regular += calc.calculateRegular(time);
    
                        // Calculate evening time compensation (out-of-office hours)
                        evening += calc.calculateEveningtime(time);
        
                        // Calculate overtime as regular time plus overtime added as extra 'regular' time.
                        // For example 480min is 8h of regular time. 540min is 8h + 1h of overtime, which adds 15min to 555min 
                        float overtime = calc.calculateOvertime(regular);
                        double pay = ((employee.getRate() * overtime) + (conf.getRate("evening") * evening)) / 60;
    
                        // Calculate weekend and holidays
                        // TODO: not part of the assignment, but would be very useful
    
                        amounts.put(day, new CalculatedDailyAmount(regular, evening, pay));
                        totalpay += pay;
                        log.debug("persid: " + persid + ", time: "+time.getDate()+" "+time.getStart()+"-"+time.getEnd()+", regular: "+regular+", evening: "+evening+", overtime: "+overtime+", pay: "+conf.getDenomination("default")+pay);
                    }
    		    }
                String name = employee.getName();
    
                ArrayList<CalculatedEntry> list = new ArrayList<>();
                list.add(new CalculatedEntry(persid, name, String.format("%.2f", totalpay), month));
                calculatedRepo.saveAll(list);
    
                log.info("persid: " + persid + " has earned: " + totalpay);
        	}
    	}
    }
    
	public byte[] download() throws IOException {
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    Writer writer = new OutputStreamWriter(bout);
	    List<String> months = calculatedRepo.findDistinctByMonthyear();
	    for (String month : months) {
	        writer.write("Monthly Wages "+month.split("\\.")[0]+"/"+month.split("\\.")[1]+":"+"\n");
	        List<CalculatedEntry> entries = calculatedRepo.findByMonthyear(month);
	        for (CalculatedEntry ce : entries) {
	            writer.write(ce.getPersid() + ", " + ce.getName() + ", " + conf.getDenomination() + ce.getPay()+"\n");
	        }
	    }
        writer.flush();
        writer.close();
        return bout.toByteArray();
	}
}
