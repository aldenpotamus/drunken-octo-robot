package com.example.aldenroberts.testproject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jamesknight on 12/10/14.
 */
public class CalendarNotificationManager {
    private static CalendarNotificationManager sSharedInstance = null;

    private static final int INTENT_ALARM_ID = 0xFEEF;

    // Prevent instantiation:
    private CalendarNotificationManager() {}

    public static CalendarNotificationManager getInstance() {
        if (sSharedInstance == null) {
            sSharedInstance = new CalendarNotificationManager();
        }

        return sSharedInstance;
    }

    public void scheduleNotificationAtHour(int hour, Context context) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("ALERT_HOUR", hour);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, INTENT_ALARM_ID, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        alarmManager.cancel(pendingIntent);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d("TAG", "Scheduling Notification Intent for "+df.format(new Date(calendar.getTimeInMillis())));

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public Notification buildNotification(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.notification);

        String preferenceKey = context.getString(R.string.preference_file_key);
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);

        String site1 = sharedPreferences.getString(context.getString(R.string.site1_pref), "");
        String site2 = sharedPreferences.getString(context.getString(R.string.site2_pref), "");
        String site3 = sharedPreferences.getString(context.getString(R.string.site3_pref), "");

        String tomorrowSite = sharedPreferences.getString("schedule_"+getTomorrowRoot(), "");

        remoteViews.setTextViewText(R.id.notification_button1, site1);
        if( site1.equals(tomorrowSite) ) remoteViews.setTextColor(R.id.notification_button1, Color.GREEN);

        remoteViews.setTextViewText(R.id.notification_button2, site2);
        if( site2.equals(tomorrowSite) ) remoteViews.setTextColor(R.id.notification_button2, Color.GREEN);

        remoteViews.setTextViewText(R.id.notification_button3, site3);
        if( site3.equals(tomorrowSite) ) remoteViews.setTextColor(R.id.notification_button3, Color.GREEN);

        remoteViews.setTextViewText(R.id.notification_button4, "...");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContent(remoteViews)
                        .setSmallIcon(R.drawable.ic_launcher);

        Intent buttonOneIntent = new Intent(context, NotificationIntentService.class);
        buttonOneIntent.putExtra(NotificationIntentService.PARAM_OFFICE_NAME, site1);
        remoteViews.setOnClickPendingIntent(R.id.notification_button1, PendingIntent.getService(context, 0, buttonOneIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        Intent buttonTwoIntent = new Intent(context, NotificationIntentService.class);
        buttonTwoIntent.putExtra(NotificationIntentService.PARAM_OFFICE_NAME, site2);
        remoteViews.setOnClickPendingIntent(R.id.notification_button2, PendingIntent.getService(context, 1, buttonTwoIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        Intent buttonThreeIntent = new Intent(context, NotificationIntentService.class);
        buttonThreeIntent.putExtra(NotificationIntentService.PARAM_OFFICE_NAME, site3);
        remoteViews.setOnClickPendingIntent(R.id.notification_button3, PendingIntent.getService(context, 2, buttonThreeIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        Intent buttonCustomIntent = new Intent(context, NotificationIntentService.class);
        buttonCustomIntent.putExtra(NotificationIntentService.PARAM_OFFICE_NAME, "CUSTOM");
        remoteViews.setOnClickPendingIntent(R.id.notification_button4, PendingIntent.getService(context, 3, buttonCustomIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        return mBuilder.build();
    }

    public static String getTodayRoot() {
        Calendar calendar = Calendar.getInstance();

        DateFormat df = new SimpleDateFormat("E");

        return df.format(new Date(calendar.getTimeInMillis())).substring(0,3);
    }

    public static String getTomorrowRoot() {
        Calendar calendar = Calendar.getInstance();

        DateFormat df = new SimpleDateFormat("E");

        return df.format(new Date(calendar.getTimeInMillis()+86400000)).substring(0,3);
    }

    public static String getDayRoot(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        DateFormat df = new SimpleDateFormat("E");

        return df.format(new Date(calendar.getTimeInMillis())).substring(0,3);
    }

    public static void generateEvents(boolean overwriteEvents, Context ctxt) {
        SharedPreferences sharedPref = ctxt.getSharedPreferences(ctxt.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String calendarNames[] = sharedPref.getString(ctxt.getString(R.string.cal_name_pref), "").split(",");
        String username = sharedPref.getString(ctxt.getString(R.string.username_pref), "");
        Map<String, String> schedule = getScheduleAsMap(ctxt);

        Calendar calendar = Calendar.getInstance();

        calendar.set( Calendar.HOUR_OF_DAY, 0 );
        calendar.set( Calendar.MINUTE, 0 );
        calendar.set( Calendar.SECOND, 0 );
        calendar.set( Calendar.MILLISECOND, 0 );

        // TODO: Crank that 2 up to like 30 so that the next months is scheduled.
        for(int i = 0; i < 15; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);

            if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                for (int j = 0; j < calendarNames.length; j++) {
                    String eventLoc = schedule.get("schedule_"+getDayRoot(calendar.getTimeInMillis()));

                    if(eventLoc != null && !eventLoc.equals(""))
                        CalendarEvent.createAllDayEvent(CalendarUtil.getCalendarIdByName(calendarNames[j], ctxt), username+" @ "+eventLoc, calendar.getTimeInMillis(), overwriteEvents, ctxt);
                }
            }
        }
    }

    public static void refreshEvents(Map<String, String> oldEvents, Context ctxt) {
        SharedPreferences sharedPref = ctxt.getSharedPreferences(ctxt.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String calendarNames[] = sharedPref.getString(ctxt.getString(R.string.cal_name_pref), "").split(",");
        String username = sharedPref.getString(ctxt.getString(R.string.username_pref), "");
        Map<String, String> schedule = getScheduleAsMap(ctxt);

        Calendar calendar = Calendar.getInstance();

        calendar.set( Calendar.HOUR_OF_DAY, 0 );
        calendar.set( Calendar.MINUTE, 0 );
        calendar.set( Calendar.SECOND, 0 );
        calendar.set( Calendar.MILLISECOND, 0 );

        for(int i = 0; i < 15; i++) {
            if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                for (int j = 0; j < calendarNames.length; j++) {
                    String key = "schedule_"+getDayRoot(calendar.getTimeInMillis());
                    String eventLoc = schedule.get(key);

                    CalendarEvent ce = CalendarUtil.getCalendarEventByTime(calendar.getTimeInMillis(), ctxt);
                    String currentCalLoc = null;
                    if(ce != null)
                        currentCalLoc = ce.getTitle().split(" @ ")[1];

                    Log.d("TAG", "[Key]"+key+" [EventLoc]"+eventLoc+" [CalLoc]"+currentCalLoc );

                    if(currentCalLoc == null || currentCalLoc.equals(oldEvents.get(key)))
                        if(eventLoc != null && !eventLoc.equals(""))
                            CalendarEvent.createAllDayEvent(CalendarUtil.getCalendarIdByName(calendarNames[j], ctxt), username + " @ " + eventLoc, calendar.getTimeInMillis(), true, ctxt);
                }
            }

            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    public static Map<String, String> getScheduleAsMap(Context ctxt) {
        Map<String, String> result = new HashMap<String, String>();

        SharedPreferences sharedPref = ctxt.getSharedPreferences(ctxt.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        result.put("schedule_Mon", sharedPref.getString("schedule_Mon", null));
        result.put("schedule_Tue", sharedPref.getString("schedule_Tue", null));
        result.put("schedule_Wed", sharedPref.getString("schedule_Wed", null));
        result.put("schedule_Thu", sharedPref.getString("schedule_Thu", null));
        result.put("schedule_Fri", sharedPref.getString("schedule_Fri", null));

        return result;
    }
}
