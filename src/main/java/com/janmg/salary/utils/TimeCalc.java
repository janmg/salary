package com.janmg.salary.utils;

import org.apache.commons.lang3.Range;

import com.janmg.salary.domain.TimeEntry;

public class TimeCalc {
    DateTime dt = new DateTime(new Config());
    
    public TimeCalc() {
    }
    
    public int calculateRegular(TimeEntry time) {
    	Range<Integer> worktime = dt.asRange(time);
        return dt.getMinutes(worktime);
    }

    public int calculateEveningtime(TimeEntry time) {
    	// 'evening' is the term used in the assignment for work done outside the normal shift hours, this includes early in the morning
    	Range<Integer> worktime = dt.asRange(time);
    	Range<Integer> shift = dt.getShift(time.getDate());
    	if (worktime.isOverlappedBy(shift)) {
            Range<Integer> intersection = worktime.intersectionWith(shift);
      	    return dt.getMinutes(worktime) - dt.getMinutes(intersection);
    	}
    	return dt.getMinutes(worktime);
    }
    
    public float calculateOvertime(float wallclock) {
        // All worked minutes with overtime added as timefortime, resulting in extra minutes.
        // < 8h = 8h
        // <10h = 8h + rest 25%
        // <12h = 8h + 2h 25% + rest 50%
        // >12h = 8h + 2h 25% + 2h 50% + rest 100%
        
        float timefortime = wallclock;

        if (wallclock > 8*60) {
            timefortime = 8*60;
            if (wallclock < 10*60) {
                timefortime += 1.25 * (wallclock - 8*60);
            } else {
                timefortime += 150; // 1.25 * 2*60 = 150
                if (wallclock < 12*60) {
                    timefortime += 1.5 * (wallclock - 10*60);
                } else {
                    timefortime += 180 + 2 * (wallclock - 12*60);  // 1.5 * 2*60 = 180 
                }
            }
        }
        return timefortime;
    }
}
