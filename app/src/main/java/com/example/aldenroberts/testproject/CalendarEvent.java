package com.example.aldenroberts.testproject;

import android.text.format.Time;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * Created by smithbrent on 12/9/14.
 */
public class CalendarEvent {

    // See the following:
    // http://developer.android.com/reference/android/provider/CalendarContract.EventsColumns.html#CALENDAR_ID
    private Integer calendarId;
    private String title;
    private String description;
    private Integer eventColor;
    private String eventTimezone;
    private Long dtStart;
    private Long dtEnd;
    private Integer allDay;

    public static CalendarEvent createAllDayEvent(Integer calendarId, String title) {
//        Calendar cal = Calendar.getInstance();
        Calendar cal = new GregorianCalendar();
        //Calendar cal = Calendar.getInstance(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long millis = cal.getTimeInMillis();
        CalendarEvent ret = new CalendarEvent(calendarId, title, millis+86400000, millis+172800000);

        ret.setAllDay(1);
        ret.setEventTimezone(TimeZone.getDefault().getID());
        return ret;
    }

    public CalendarEvent(Integer calendarId, String title, Long dtStart, Long dtEnd) {
        setCalendarId(calendarId);
        setTitle(title);
        setDtStart(dtStart);
        setDtEnd(dtEnd);
        setEventTimezone(TimeZone.getDefault().getID());
    }

    public Integer getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Integer calendarId) {
        this.calendarId = calendarId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEventColor() {
        return eventColor;
    }

    public void setEventColor(Integer eventColor) {
        this.eventColor = eventColor;
    }

    public String getEventTimezone() {
        return eventTimezone;
    }

    public void setEventTimezone(String eventTimezone) {
        this.eventTimezone = eventTimezone;
    }

    public Long getDtStart() {
        return dtStart;
    }

    public void setDtStart(Long dtStart) {
        this.dtStart = dtStart;
    }

    public Long getDtEnd() {
        return dtEnd;
    }

    public void setDtEnd(Long dtEnd) {
        this.dtEnd = dtEnd;
    }

    public Integer getAllDay() {
        return allDay;
    }

    public void setAllDay(Integer allDay) {
        this.allDay = allDay;
    }
}
