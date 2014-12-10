package com.example.aldenroberts.testproject;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotificationIntentService extends IntentService {

    private static final String BRENTS_ACCOUNT_NAME = "smithbrent@google.com";
    private static final String BRENTS_LDAP = "smithbrent";
    private static final Integer BRENTS_CALENDAR_ID = 126;

    // Add an office to the appropriate calendar.
    public static final String ACTION_ADD_OFFICE_EVENT = "com.example.aldenroberts.testproject.action.ADD_OFFICE_EVENT";

    // Parameters
    public static final String PARAM_OFFICE_NAME = "com.example.aldenroberts.testproject.extra.PARAM_OFFICE_NAME";

    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("TAG", "onHandleIntent()");

        if (intent != null) {
            String action = intent.getAction();

            // XXX: delete this when we actually pass this.
            action = ACTION_ADD_OFFICE_EVENT;

            if (ACTION_ADD_OFFICE_EVENT.equals(action)) {

                // The office the user chose.
                String officeName = intent.getStringExtra(PARAM_OFFICE_NAME);
                //intent.getCharSequenceExtra(PARAM_OFFICE_NAME);

                String title = BRENTS_LDAP + " @ " + officeName;

                CalendarEvent newEvent = CalendarEvent.createAllDayEvent(
                        BRENTS_CALENDAR_ID, title);

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
                Log.d("TAG", "dtStart = " + df.format(new Date(newEvent.getDtStart())));
                Log.d("TAG", "dtEnd = " + df.format(new Date(newEvent.getDtEnd())));

                String eventId = CalendarUtil.addEvent(NotificationIntentService.this, newEvent);
//
                Log.d("TAG", "Added " + title + " to calendar (eventId = " + eventId + " )");


            } else  {
                Log.d("TAG", "Unrecognized Action You Jerk!");
            }
        }
    }


}
