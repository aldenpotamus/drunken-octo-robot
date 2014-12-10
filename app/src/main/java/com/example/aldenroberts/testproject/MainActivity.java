package com.example.aldenroberts.testproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.NotificationCompat;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RemoteViews;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private ArrayAdapter<String> locationsAdapter;

    private static final String TAG = "MyActivity";

    private String locationText = "";

    private Cursor mCursor = null;
    private static final String[] COLS = new String[]
            { CalendarContract.Calendars.NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());

        buttonConfig();
    }

    protected void buttonConfig() {
        final Button createNotificationButton = (Button) findViewById(R.id.notificationButton);
        createNotificationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG,"Fire Zee Notifications!");

                RemoteViews remoteViews = new RemoteViews(getPackageName(),
                                                          R.layout.notification);

                remoteViews.setTextViewText(R.id.notification_button1, locationsAdapter.getCount() > 0 ? locationsAdapter.getItem(0) : "");
                remoteViews.setTextViewText(R.id.notification_button2, locationsAdapter.getCount() > 1 ? locationsAdapter.getItem(1) : "");
                remoteViews.setTextViewText(R.id.notification_button3, locationsAdapter.getCount() > 2 ? locationsAdapter.getItem(2) : "");
                remoteViews.setTextViewText(R.id.notification_button4, "...");

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(MainActivity.this)
                                  .setContent(remoteViews)
                                  .setSmallIcon(R.drawable.ic_launcher);

                Intent buttonOneIntent = new Intent(MainActivity.this, NotificationIntentService.class);
                buttonOneIntent.putExtra(NotificationIntentService.PARAM_OFFICE_NAME, locationsAdapter.getCount() > 0 ? locationsAdapter.getItem(0) : "");
                remoteViews.setOnClickPendingIntent(R.id.notification_button1, PendingIntent.getService(MainActivity.this, 0, buttonOneIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                Intent buttonTwoIntent = new Intent(MainActivity.this, NotificationIntentService.class);
                buttonTwoIntent.putExtra(NotificationIntentService.PARAM_OFFICE_NAME, locationsAdapter.getCount() > 1 ? locationsAdapter.getItem(1) : "");
                remoteViews.setOnClickPendingIntent(R.id.notification_button2, PendingIntent.getService(MainActivity.this, 1, buttonTwoIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                Intent buttonThreeIntent = new Intent(MainActivity.this, NotificationIntentService.class);
                buttonThreeIntent.putExtra(NotificationIntentService.PARAM_OFFICE_NAME, locationsAdapter.getCount() > 2 ? locationsAdapter.getItem(2) : "");
                remoteViews.setOnClickPendingIntent(R.id.notification_button3, PendingIntent.getService(MainActivity.this, 2, buttonThreeIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                Intent buttonCustomIntent = new Intent(MainActivity.this, NotificationIntentService.class);
                buttonCustomIntent.putExtra(NotificationIntentService.PARAM_OFFICE_NAME, "CUSTOM");
                remoteViews.setOnClickPendingIntent(R.id.notification_button4, PendingIntent.getService(MainActivity.this, 3, buttonCustomIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(1, mBuilder.build());
            }
        });

        ListView listView = (ListView) findViewById(R.id.locationListView);
        listView.setAdapter(locationsAdapter);

        final Button addLocationButton = (Button) findViewById(R.id.addLocationButton);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Title");

                final EditText input = new EditText(MainActivity.this);

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        locationText = input.getText().toString().toUpperCase();
                        locationsAdapter.add(locationText);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.show();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
