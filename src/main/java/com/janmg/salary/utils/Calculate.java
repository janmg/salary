package com.janmg.salary.utils;

import java.time.ZonedDateTime;
import com.janmg.salary.domain.TimeEntry;

public class Calculate {
    DateTime dt;
    
    public Calculate(Config conf) {
        dt = new DateTime(conf);
    }
    
    public int calculateRegular(TimeEntry time) {
        return calculateRegular(time.getDate(), time.getStart(), time.getEnd());
    }
    
    public int calculateRegular(String date, String start, String end) {
        ZonedDateTime starttime = dt.format(date, start);
        ZonedDateTime endtime = dt.format(date, end);
        endtime = fixMidnight(starttime, endtime);

        return dt.getMinutesBetween(starttime, endtime);
    }

    private ZonedDateTime fixMidnight(ZonedDateTime starttime, ZonedDateTime endtime) {
        // Working past midnight should not be punished
        if (endtime.isBefore(starttime)) {
            endtime = dt.getDayLater(endtime);
        }
        return endtime;
    }

    public int calculateEveningtime(TimeEntry time) {
        return calculateEveningtime(time.getDate(), time.getStart(), time.getEnd());
    }

    public int calculateEveningtime(String date, String start, String end) {
        ZonedDateTime starttime = dt.format(date, start);
        ZonedDateTime endtime = dt.format(date, end);
        endtime = fixMidnight(starttime, endtime);
        
        int evening = 0; // evening hours includes also morning hours
        // TODO: Maybe better as Apache Commons Range?
        
        // overtime in the morning
        ZonedDateTime shiftstart = dt.getShiftStart(starttime);
        if (endtime.isBefore(shiftstart)) shiftstart = endtime;
        evening += dt.positiveOnly(dt.getMinutesBetween(starttime, shiftstart));

        // overtime in the evening
        ZonedDateTime shiftend = dt.getShiftEnd(starttime); // WTF: starttime is used for it's date component as endtime may be a day later.
        if (starttime.isAfter(shiftend)) shiftend = starttime;
        evening += dt.positiveOnly(dt.getMinutesBetween(shiftend, endtime));
        
        return evening;
    }
    
    public float calculateOvertime(float wallclock) {
        // All worked minutes with overtime added as extra minutes
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
