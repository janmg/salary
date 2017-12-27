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
    private String shiftstart = "6:00";
    private String shiftend = "18:00";

    public DateTime() {
    }

    public DateTime(String timezone, String formatter, String shiftstart, String shiftend) {
        // Not used
        this.timezone = timezone;
        this.formatter = DateTimeFormatter.ofPattern(formatter);
        this.shiftstart = shiftstart;        
        this.shiftend = shiftend;
    }

    public DateTime(Config conf) {
        this.timezone = conf.get("date.timezone");
        this.formatter = DateTimeFormatter.ofPattern(conf.get("date.format"));
        this.shiftstart = conf.get("shift.start");        
        this.shiftend = conf.get("shift.end");
    }

    public ZonedDateTime format(String date, String time) {
        return ZonedDateTime.of(LocalDateTime.parse(date + " " + time, formatter), ZoneId.of(timezone));
    }

    public int positiveOnly(int number) {
        if (number < 0) number = 0;
        return number;
    }

    public int getMinutesBetween(ZonedDateTime starttime, ZonedDateTime endtime) {
        return (int) Duration.between(starttime, endtime).toMinutes();
    }

    public ZonedDateTime getDayLater(ZonedDateTime endtime) {
        return endtime.plusDays(1);
    }

    public ZonedDateTime getShiftStart(ZonedDateTime date) {
        return asZoneDateTime(date, shiftstart);
    }

    public ZonedDateTime getShiftEnd(ZonedDateTime date) {
        return asZoneDateTime(date, shiftend);
    }
    
    private ZonedDateTime asZoneDateTime(ZonedDateTime date, String shift) {
        return date.withHour(new Integer(shift.split(":")[0])).withMinute(new Integer(shift.split(":")[1]));
    }
}
