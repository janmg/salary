package com.janmg.salary.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import com.janmg.salary.domain.TimeEntry;

public class DateTime {
    // WTF: Java8 ZoneDateTime with timezone can track daylightsaving and can do date calculations and manipulations.
    private String timezone = "Europe/Helsinki";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy H:mm");
    private String shiftstart;
    private String shiftend;
    
    public DateTime(String timezone, String formatter, String shiftstart, String shiftend) {
        this.timezone = timezone;
        this.formatter = DateTimeFormatter.ofPattern(formatter);
        this.shiftstart = shiftstart;        
        this.shiftend = shiftend;
    }
    
    public int positiveOnly(int number) {
        if (number < 0) number = 0;
        return number;
    }

    public ZonedDateTime asMoment(ZonedDateTime time, String shift) {
        return time.withHour(new Integer(shift.split(":")[0])).withMinute(new Integer(shift.split(":")[1]));
    }

    public int getMinutesBetween(ZonedDateTime starttime, ZonedDateTime endtime) {
        return (int) Duration.between(starttime, endtime).toMinutes();
    }

    public ZonedDateTime format(String date, String time) {
        return ZonedDateTime.of(LocalDateTime.parse(date + " " + time, formatter), ZoneId.of(timezone));
    }

    // --- CUT HERE ---
    // Below should be put in an own  class, using datetime as an object
    public int calculateRegular(TimeEntry time) {
        return calculateRegular(time.getDate(), time.getStart(), time.getEnd());
    }
    
    public int calculateRegular(String date, String start, String end) {
        ZonedDateTime starttime = format(date, start);
        ZonedDateTime endtime = format(date, end);
        
        // Working past midnight should not be punished
        if (endtime.isBefore(starttime)) {
            endtime = ZonedDateTime.of(LocalDateTime.parse(date+" "+end, formatter).plusDays(1), ZoneId.of(timezone));
        }

        return getMinutesBetween(starttime, endtime);
    }
    
    public int calculateEveningtime(TimeEntry time) {
        return calculateEveningtime(time.getDate(), time.getStart(), time.getEnd());
    }

    public int calculateEveningtime(String date, String start, String end) {
        ZonedDateTime starttime = format(date, start);
        ZonedDateTime endtime = format(date, end);
        
        int evening = 0; // evening hours include all hours outside the regular working hours

        // overtime in the morning
        ZonedDateTime morningtime = asMoment(starttime, shiftstart);
        if (endtime.isBefore(morningtime)) morningtime = endtime;
        evening =+ positiveOnly(getMinutesBetween(starttime, morningtime));

        // overtime in the evening
        ZonedDateTime eveningtime = asMoment(starttime, shiftend);
        if (starttime.isAfter(eveningtime)) eveningtime = starttime;
        evening =+ positiveOnly(getMinutesBetween(eveningtime, endtime));
        
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
                timefortime += 150; // 1.25 * 2*60
                if (wallclock < 12*60) {
                    timefortime += 1.5 * (wallclock - 10*60);
                } else {
                    timefortime += 180 + 2 * (wallclock - 12*60); 
                }
            }
        }
        return timefortime;
    }
}
