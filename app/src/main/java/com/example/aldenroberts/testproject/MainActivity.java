package com.example.aldenroberts.testproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
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
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private SharedPreferences sharedPref;

    private ArrayAdapter<String> locationsAdapter;
    private ArrayList<String> mLocations;

    private ListView mListView;

    private static final String TAG = "MyActivity";

    private String locationText = "";

    private Cursor mCursor = null;
    private static final String[] COLS = new String[]
            { CalendarContract.Calendars.NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = this.getBaseContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        mLocations = new ArrayList<String>();

        locationsAdapter = new ArrayAdapter<String>(this, R.layout.location_list_item, R.id.locationName, mLocations);

        locationsAdapter.registerDataSetObserver(new DataSetObserver() {
            public void onChanged() {
                String site1 = null;
                String site2 = null;
                String site3 = null;

                if(locationsAdapter.getCount() > 0) site1 = locationsAdapter.getItem(0);
                if(locationsAdapter.getCount() > 1) site2 = locationsAdapter.getItem(1);
                if(locationsAdapter.getCount() > 2) site3 = locationsAdapter.getItem(2);

                setSitesPref(site1, site2, site3);
            }
        });

        ((TextView)findViewById(R.id.calendarNameStatic)).setText( getCalendarPref() );
        ((TextView)findViewById(R.id.usernameStatic)).setText( getUsernamePref() );
        ((TextView)findViewById(R.id.reminderTime)).setText( getReminderTimePref()+"" );
        ((SeekBar)findViewById(R.id.reminderSeekBar)).setProgress( getReminderTimePref() );

        List<String> sites = getSitesPref();

        locationsAdapter.setNotifyOnChange(false);

        for(int i = 0; i < sites.size(); i++) {
            if (sites.get(i) != null) {
                locationsAdapter.add(sites.get(i));
            }
        }

        locationsAdapter.setNotifyOnChange(true);

        buttonConfig();
    }

    protected String getCalendarPref() {
        return sharedPref.getString(getString(R.string.cal_name_pref), "");
    }

    protected void setCalendarPref(String cal_name) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.cal_name_pref), cal_name);
        editor.commit();
    }

    protected String getUsernamePref() {
        return sharedPref.getString(getString(R.string.username_pref), "");
    }

    protected void setUsernamePref(String cal_name) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.username_pref), cal_name);
        editor.commit();
    }

    protected List<String> getSitesPref() {
        List<String> result = new ArrayList<String>();

        result.add(sharedPref.getString(getString(R.string.site1_pref), null));
        result.add(sharedPref.getString(getString(R.string.site2_pref), null));
        result.add(sharedPref.getString(getString(R.string.site3_pref), null));

        return result;
    }

    protected void setSitesPref(String site1, String site2, String site3) {
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(getString(R.string.site1_pref), site1);
        editor.putString(getString(R.string.site2_pref), site2);
        editor.putString(getString(R.string.site3_pref), site3);

        editor.commit();
    }

    protected int getReminderTimePref() {
        return sharedPref.getInt(getString(R.string.reminder_time_pref), 18);
    }

    protected void setReminderTimePref(int reminderTime) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.reminder_time_pref), reminderTime);
        editor.commit();
    }

    protected void buttonConfig() {
        final Button createNotificationButton = (Button) findViewById(R.id.notificationButton);
        createNotificationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Fire Zee Notifications!");
                CalendarNotificationManager manager = CalendarNotificationManager.getInstance();
                Notification notification = manager.buildNotification(MainActivity.this);
            }
        });

        mListView = (ListView) findViewById(R.id.locationListView);
        mListView.setAdapter(locationsAdapter);

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

        final Button saveCalendarNameButton = (Button) findViewById(R.id.saveCalendar);
        saveCalendarNameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Title");

                final EditText input = new EditText(MainActivity.this);

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setCalendarPref(input.getText().toString());
                        ((TextView)findViewById(R.id.calendarNameStatic)).setText(getCalendarPref());
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

        final Button saveUsernameButton = (Button) findViewById(R.id.saveUsername);
        saveUsernameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Title");

                final EditText input = new EditText(MainActivity.this);

                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setUsernamePref(input.getText().toString());
                        ((TextView)findViewById(R.id.usernameStatic)).setText(getUsernamePref());
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

        SeekBar sb = (SeekBar)findViewById(R.id.reminderSeekBar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView) findViewById(R.id.reminderTime)).setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("TAG", "SEEK BAR STOPPED: " + seekBar.getProgress());
                setReminderTimePref(seekBar.getProgress());
                CalendarNotificationManager.getInstance().scheduleNotificationAtHour(seekBar.getProgress(), MainActivity.this.getApplicationContext());
            }
        });
    }

    //TODO : Make this actually work
    private String toAMPM(int time) {
        String postfix;

        if(time > 12) {
            postfix = "PM";
        } else {
            postfix = "AM";
        }

        return (time % 12)+postfix;
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

    public void onDeleteButtonClick(View view) {
        // Clicked View is Button, its parent is the row in the list view:
        final int position = mListView.getPositionForView((View) view.getParent());
        mLocations.remove(position);

        this.locationsAdapter.notifyDataSetChanged();
    }
}
