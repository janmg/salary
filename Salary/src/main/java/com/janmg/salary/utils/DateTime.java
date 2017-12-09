package com.janmg.salary.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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

    public int calculateRegular(String date, String start, String end) {
        ZonedDateTime starttime = format(date, start);
        ZonedDateTime endtime = format(date, end);
        
        // Working past midnight should not be punished
        if (endtime.isBefore(starttime)) {
            endtime = ZonedDateTime.of(LocalDateTime.parse(date+" "+end, formatter).plusDays(1), ZoneId.of(timezone));
        }

        return getMinutesBetween(starttime, endtime);
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
    
    public float calculateOvertime(float regular) {
        // All worked minutes with overtime add as minutes
        // < 8h = 8h
        // <10h = 8h + rest 25%
        // <12h = 8h + 2h 25% + rest 50%
        // >12h = 8h + 2h 25% + 2h 50% + rest 100%
        
        float multiplier = 0;
        
        if (regular < 8*60) {
            multiplier = regular;
        } else {
            multiplier = 8*60;
            if (regular < 10*60) {
                multiplier = multiplier + ((25/100) * (regular-8) * 60);
            } else {
                multiplier = multiplier + ((25/100) * 2);
                if (regular < 12) {
                    multiplier = multiplier + ((50/100) * (regular-10) * 60);
                } else {
                    multiplier = multiplier + ((50/100) * 2)  + ((100/100) * (regular-12)*60);
                }
            }
        }
        return multiplier;
    }
}
