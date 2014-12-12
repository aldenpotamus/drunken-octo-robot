package com.example.aldenroberts.testproject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.Time;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by smithbrent on 12/9/14.
 */
public class CalendarUtil {

    private static final String[] CALENDAR_COLS = new String[] {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
    };

    public static List<Calendar> listCalendars(Context ctxt) {
        Cursor mCursor = null;
        mCursor = ctxt.getContentResolver().query(
                CalendarContract.Calendars.CONTENT_URI, CALENDAR_COLS, null, null, null);

        mCursor.moveToFirst();

        List<Calendar> cals = new ArrayList<Calendar>();
        while(mCursor.moveToNext()) {
            Integer id = mCursor.getInt(0);
            Calendar cal = new Calendar(mCursor.getString(1), mCursor.getString(2));
            cal.setId(id);
            cals.add(cal);
        }

        return cals;
    }

    public static int getCalendarIdByName(String calName, Context ctxt) {
        List<Calendar> calendars = CalendarUtil.listCalendars(ctxt);

        for(int i = 0; i < calendars.size(); i++)
            if(calendars.get(i).getDisplayName().equals(calName))
                return calendars.get(i).getId();

        return -1;
    }

    public static String addEvent(Context ctxt, CalendarEvent event) {
        if(event == null) return null;

        ContentResolver cr = ctxt.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, event.getDtStart());
        values.put(CalendarContract.Events.DTEND, event.getDtEnd());
        values.put(CalendarContract.Events.ALL_DAY, event.getAllDay());
        values.put(CalendarContract.Events.EVENT_TIMEZONE, event.getEventTimezone());
        values.put(CalendarContract.Events.TITLE, event.getTitle());
        values.put(CalendarContract.Events.DESCRIPTION, event.getDescription());
        values.put(CalendarContract.Events.CALENDAR_ID, event.getCalendarId());
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // Retrieve ID for new event
        String eventID = uri.getLastPathSegment();
        return eventID;
    }

    public static CalendarEvent getCalendarEventById(String calendarName, String eventid, Context ctxt) {
        //int calendarId = CalendarUtil.getCalendarIdByName(calendarName, ctxt);

        return null;
    }

}
