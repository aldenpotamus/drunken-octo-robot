package com.example.aldenroberts.testproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.*;

/**
 * Created by jamesknight on 12/10/14.
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAG", "Received Notification");

        // Get today:
        java.util.Calendar calendar = java.util.Calendar.getInstance();

        // If its Sunday-Thursday:
        if (calendar.get(java.util.Calendar.DAY_OF_WEEK) <= java.util.Calendar.FRIDAY) {
            CalendarNotificationManager manager = CalendarNotificationManager.getInstance();
            Notification notification = manager.buildNotification(context);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        }
    }
}
