package com.janmg.salary.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.Range;

import com.janmg.salary.domain.TimeEntry;

public class DateTime {
    private String timezone = "Europe/Helsinki";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy H:mm");
    private String shiftstart = "6:00";
    private String shiftend = "18:00";

    public DateTime() {
    }

    public DateTime(Config conf) {
        this.timezone = conf.get("date.timezone");
        this.formatter = DateTimeFormatter.ofPattern(conf.get("date.format"));
        this.shiftstart = conf.get("shift.start");        
        this.shiftend = conf.get("shift.end");
    }

    // Calculate with Java8 ZoneDateTime with timezone can track daylightsaving and can do date calculations and manipulations.
    public ZonedDateTime format(String date, String time) {
        return ZonedDateTime.of(LocalDateTime.parse(date + " " + time, formatter), ZoneId.of(timezone));
    }

    public int positiveOnly(int number) {
        if (number < 0) number = 0;
        return number;
    }

    public ZonedDateTime fixMidnight(ZonedDateTime starttime, ZonedDateTime endtime) {
        // Working past midnight should not be punished
        if (endtime.isBefore(starttime)) {
            endtime = getDayLater(endtime);
        }
        return endtime;
    }

    public ZonedDateTime getDayLater(ZonedDateTime endtime) {
        return endtime.plusDays(1);
    }

    private ZonedDateTime asZoneDateTime(ZonedDateTime date, String shift) {
        return date.withHour(new Integer(shift.split(":")[0])).withMinute(new Integer(shift.split(":")[1]));
    }

    // Calculate as ranges in minutes with the start of the month as epoch
    public int getMinutes(Range<Integer> time) {
		return time.getMaximum() - time.getMinimum();
	}

	public Range<Integer> getShift(String date) {
		return getShift(format(date, shiftstart));
	}

    public Range<Integer> getShift(ZonedDateTime date) {
        return asRange(date, shiftstart, shiftend);
    }

    public Range<Integer> asRange(TimeEntry te) {
    	return asRange(format(te.getDate(),te.getStart()), te.getStart(), te.getEnd());
    }

    public Range<Integer> asRange(ZonedDateTime date, String start, String end) {
        ZonedDateTime starttime = asZoneDateTime(date, start);
        ZonedDateTime endtime = asZoneDateTime(date, end);
        endtime = fixMidnight(starttime, endtime);
        return Range.between(asMontlyMinutes(starttime), asMontlyMinutes(endtime));
    }

	private int asMontlyMinutes(ZonedDateTime time) {
		// remove year and month so that the epoch starts every month
		return (int)time.withYear(1970).withMonth(1).toEpochSecond() / 60;
	}
}
