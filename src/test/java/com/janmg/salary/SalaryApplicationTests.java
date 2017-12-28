package com.janmg.salary;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.janmg.salary.domain.TimeEntry;
import com.janmg.salary.utils.TimeCalc;
import com.janmg.salary.utils.Config;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SalaryApplicationTests {

	@Test
	public void rangeTest() {
	    // The range test checks for a full 24 hours if the regular and overtime are increasing linearly for every minute, this does not check the actual values. Those are checked in the spotChecks.  
	    TimeCalc calc = new TimeCalc();
	    int prev_regular = 0;
	    float prev_overtime = 1;
	    
	    for (int hours=4;hours<28;hours++) {
	        for (int mins=0;mins<60;mins++) {
	            String endtime = String.format("%d:%02d", hours%24, mins);
	            
	            // Test if regular work time increases for every minute
	            int regular = calc.calculateRegular(new TimeEntry(0,"JunitUser","1.1.2010", "4:00", endtime));
	            if (regular < prev_regular) fail("More work should always result in more regular minutes");
	            prev_regular = regular;

	            // Test if the over regular work time increases for every minute
	            float overtime = calc.calculateOvertime(regular);
	            if (hours < 8) {
	                if (overtime > (60*hours+mins)) fail("Less then 8 hours should never result in overtime");
	            } else {
	                if (overtime < prev_overtime) fail("Overtime factor should always increase");
	            }
	            prev_overtime = overtime;
	        }
	    }
	}

   @Test
   public void spotChecksTest() {
       TimeCalc calc = new TimeCalc();
       assertTrue(calc.calculateRegular(new TimeEntry(0,"JunitUser","1.1.2010", "0:00", "8:00")) == 480);
       assertTrue(calc.calculateRegular(new TimeEntry(0,"JunitUser","1.1.2010", "0:00", "9:00")) == 540);
       assertTrue(calc.calculateEveningtime(new TimeEntry(0,"JunitUser","1.1.2010", "6:00", "18:00")) == 0);
       assertTrue(calc.calculateEveningtime(new TimeEntry(0,"JunitUser","1.1.2010", "5:00", "6:00")) == 60); // This test caught a =+ instead of +=  
       assertTrue(calc.calculateEveningtime(new TimeEntry(0,"JunitUser","1.1.2010", "18:00", "19:00")) == 60);
       assertTrue(calc.calculateEveningtime(new TimeEntry(0,"JunitUser","1.1.2010", "23:00", "01:00")) == 120); // This test caught past midnight check for overtime
   }

   @Test
   public void dayLightSavingTimeTest() {
       // Sunday, 26 March 2017, 03:00:00 clocks were turned forward 1 hour 
       TimeCalc calc = new TimeCalc();
       assertTrue(calc.calculateRegular(new TimeEntry(0,"JunitUser","26.3.2017", "2:00", "05:00")) == 120);
   }
}
