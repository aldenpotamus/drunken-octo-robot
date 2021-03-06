package com.example.aldenroberts.testproject;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotificationIntentService extends IntentService {

    //private static final String BRENTS_ACCOUNT_NAME = "smithbrent@google.com";
    private static final String BRENTS_LDAP = "smithbrent";
    private static final Integer BRENTS_CALENDAR_ID = 126;

    private String username;
    private String calendarName;

    private SharedPreferences sharedPref;

    // Add an office to the appropriate calendar.
    public static final String ACTION_ADD_OFFICE_EVENT = "com.example.aldenroberts.testproject.action.ADD_OFFICE_EVENT";

    // Parameters
    public static final String PARAM_OFFICE_NAME = "com.example.aldenroberts.testproject.extra.PARAM_OFFICE_NAME";

    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String calendarNames[] = sharedPref.getString(getString(R.string.cal_name_pref), "").split(",");
        this.username = sharedPref.getString(getString(R.string.username_pref), "");

        Log.d("IntentService","Starting Intent Service: "+this.calendarName+" - "+this.username);

        Log.d("TAG", "onHandleIntent()");

        for(int i = 0; i < calendarNames.length; i++) {
            if (intent != null) {
                String action = intent.getAction();

                // XXX: delete this when we actually pass this.
                action = ACTION_ADD_OFFICE_EVENT;

                if (ACTION_ADD_OFFICE_EVENT.equals(action)) {

                    // The office the user chose.
                    String officeName = intent.getStringExtra(PARAM_OFFICE_NAME);
                    //intent.getCharSequenceExtra(PARAM_OFFICE_NAME);

                    String title = this.username + " @ " + officeName;

                    CalendarEvent newEvent = CalendarEvent.createAllDayEvent(CalendarUtil.getCalendarIdByName(calendarNames[i], NotificationIntentService.this), title, true, NotificationIntentService.this);

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Log.d("TAG", "dtStart = " + df.format(new Date(newEvent.getDtStart())));
                    Log.d("TAG", "dtEnd = " + df.format(new Date(newEvent.getDtEnd())));


                } else {
                    Log.d("TAG", "Unrecognized Action You Jerk!");
                }
            }
        }

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancel(1);
    }
}
