package com.example.aldenroberts.testproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.format.Time;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
    private Uri uri;

    public static CalendarEvent createAllDayEvent(Integer calendarId, String title, boolean overwriteExisting, Context ctxt) {
        Calendar cal = new GregorianCalendar();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return createAllDayEvent(calendarId, title, cal.getTimeInMillis()+86400000, overwriteExisting, ctxt);
    }

    public static CalendarEvent createAllDayEvent(Integer calendarId, String title, long time, boolean overwriteExisting, Context ctxt) {
        SharedPreferences sharedPref = ctxt.getSharedPreferences(ctxt.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

/*
        // Prefs Clear
        SharedPreferences.Editor editor2 = sharedPref.edit();
        editor2.putStringSet("existingManagedEvents", new HashSet<String>());
        editor2.commit();
*/


        HashMap<String, String> existingManagedEvents = setToMap(sharedPref.getStringSet("existingManagedEvents", null));

        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(time);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        CalendarEvent ret;

        DateFormat df = new SimpleDateFormat("E");

        String key = cal.getTimeInMillis()+"";
        String dayShort = df.format(new Date(cal.getTimeInMillis())).substring(0,3);

        if( existingManagedEvents.containsKey(key) ) {
            ret = CalendarUtil.getCalendarEventById(Uri.parse(existingManagedEvents.get(key)), ctxt);
            Log.d("EXISTING EVENT: ", ret.getEventId()+" - "+ret.getTitle());

            if(overwriteExisting) {
                Log.d("TAG", "Modifying Event ["+existingManagedEvents.get(key)+"] - " + dayShort + "(" + key + ") in "+calendarId);
                ret.setTitle(title);
                CalendarUtil.updateEvent(ctxt, ret);
            } else {
                Log.d("TAG", "Skipping Event - ["+existingManagedEvents.get(key)+"] " + dayShort + "(" + key + ") in "+calendarId);
            }
        } else {
            ret = new CalendarEvent(calendarId, title, time, time+86400000);
            ret.setAllDay(1);
            ret.setEventTimezone(TimeZone.getDefault().getID());

            String eventUri = CalendarUtil.addEvent(ctxt, ret);

            Log.d("TAG", "Creating New Event ["+eventUri+"] - " + dayShort + "(" + key + ") in "+calendarId);

            existingManagedEvents.put(time+"", eventUri);
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet("existingManagedEvents", mapToSet(existingManagedEvents));
        editor.commit();

        return ret;
    }

    public CalendarEvent(Uri eventUri, Integer calendarId, String title, Long dtStart, Long dtEnd) {
        setEventUri(eventUri);
        setCalendarId(calendarId);
        setTitle(title);
        setDtStart(dtStart);
        setDtEnd(dtEnd);
        setEventTimezone(TimeZone.getDefault().getID());
    }

    public CalendarEvent(Integer calendarId, String title, Long dtStart, Long dtEnd) {
        setCalendarId(calendarId);
        setTitle(title);
        setDtStart(dtStart);
        setDtEnd(dtEnd);
        setEventTimezone(TimeZone.getDefault().getID());
    }

    public Integer getEventId() {
        return Integer.parseInt(uri.getLastPathSegment());
    }

    public Uri getEventUri() {
        return uri;
    }

    public void setEventUri(Uri eventUri) {
        this.uri = eventUri;
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

    public static Set<String> mapToSet(HashMap<String,String> map) {
        Set<String> result = new HashSet<String>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!entry.getKey().equals("")) {
                //Log.d("TAG", "mapToSet PUT : [" + entry.getKey() + "|" + entry.getValue() + "]");
                result.add(entry.getKey() + "-" + entry.getValue());
            }
        }

        return result;
    }

    public static HashMap<String, String> setToMap(Set<String> set) {
        HashMap<String, String> result = new HashMap<String, String>();

        if(set == null) return result;

        for (String kvp : set) {
            String tokens[] = kvp.split("-");
            //Log.d("TAG", "setToMap PUT : key="+tokens[0]+" value="+tokens[1]);
            result.put(tokens[0], tokens[1]);
        }

        return result;
    }
}
