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

    private static final String[] EVENT_COLS = new String[] {
            CalendarContract.Events._ID,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
    };

    private static final String[] EVENT_UPDATE_COLS = new String[] {
            CalendarContract.Events.TITLE
    };

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
        //String eventID = uri.getLastPathSegment();
        return uri.toString();
    }

    public static String updateEvent(Context ctxt, CalendarEvent event) {
        if(event == null) return null;

        ContentResolver cr = ctxt.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.TITLE, event.getTitle());
        values.put(CalendarContract.Events.DESCRIPTION, event.getDescription());
        values.put(CalendarContract.Events.CALENDAR_ID, event.getCalendarId());
        cr.update(event.getEventUri(), values, null, null);

        // Retrieve ID for new event
        return event.getEventUri().toString();
    }

    public static CalendarEvent getCalendarEventById(Uri eventURI, Context ctxt) {
        Log.d("TAG", "LOOKING FOR "+eventURI);

        ContentResolver cr = ctxt.getContentResolver();

        Cursor mCursor = null;

        String query = CalendarContract.Events._ID+" IN ( ? )";
        String[] queryArgs = new String[]{ eventURI.getLastPathSegment() };

        mCursor = cr.query(CalendarContract.Events.CONTENT_URI, EVENT_COLS, query, queryArgs , null);

        Log.d("TAG", "NUMBER OF RESULTS "+mCursor.getCount());

        mCursor.moveToFirst();

        Log.d("TAG", mCursor.getInt(0)+" - "+mCursor.getInt(1)+" - "+mCursor.getString(2)+" - "+mCursor.getLong(3)+" - "+mCursor.getLong(4));
        return new CalendarEvent(eventURI, mCursor.getInt(1), mCursor.getString(2)+"", mCursor.getLong(3), mCursor.getLong(4));
    }

}
