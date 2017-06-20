package com.ue.smarttimetable;


import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Context context;
    private static int startHour=9,startMinute=30;
    private static TimePickerDialog timePickerDialog;
    private static Calendar calendar;

    private static class OnTimeSet implements TimePickerDialog.OnTimeSetListener{
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startHour=hourOfDay;
            startMinute=minute;
            SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor e=sharedPreferences.edit();
            e.putString("start_hour", Integer.toString(startHour));
            e.putString("start_minute", Integer.toString(startMinute));
            e.apply();

            Log.i("start_hour", Integer.toString(startHour));
            Log.i("start_minute", Integer.toString(startMinute));
        }
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            Log.i("Value", stringValue);

            SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                if(listPreference.getEntries().length==12){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("lesson_length", listPreference.getEntries()[index].toString());
                    editor.apply();
                }
                if(listPreference.getEntries().length==13){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("break_time", listPreference.getEntries()[index].toString());
                    editor.apply();
                }
                else if(listPreference.getEntries().length==14){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("department", listPreference.getEntries()[index].toString());
                    editor.apply();

                    MainActivity.changeNavigationDrawer();
                }
                else if(listPreference.getEntries().length==15){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("lesson_per_day", listPreference.getEntries()[index].toString());
                    editor.apply();
                }
                else if(listPreference.getEntries().length==16){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("time_before_silence", listPreference.getEntries()[index].toString());
                    editor.apply();
                }

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            }
            else
                preference.setSummary(stringValue);
                // For all other preferences, set the summary to the value's
                // simple string representation.

            return true;
        }
    };


    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        String startHourS,startMinuteS;

        context=getApplicationContext();
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
        startHourS=sharedPreferences.getString("start_hour","9");
        startMinuteS=sharedPreferences.getString("start_minute","30");

        startHour=Integer.parseInt(startHourS);
        startMinute=Integer.parseInt(startMinuteS);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    SharedPreferences.OnSharedPreferenceChangeListener listener=new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("list_departments")){

            }
        }
    };

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("list_departments"));
            bindPreferenceSummaryToValue(findPreference("list_lesson_per_day"));
            bindPreferenceSummaryToValue(findPreference("list_break_time"));
            bindPreferenceSummaryToValue(findPreference("list_lesson_length"));
            bindPreferenceSummaryToValue(findPreference("list_time_before_silence"));

            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            final Preference timePreference = (Preference) findPreference("start_hour_minute");
            calendar=Calendar.getInstance();
            timePickerDialog = new TimePickerDialog(getActivity(), new OnTimeSet(), startHour, startMinute, true);
            timePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    timePickerDialog.setTitle("Set Course Start Time");
                    timePickerDialog.show();
                    return false;
                }
            });

            final SwitchPreference switch_mute = (SwitchPreference) findPreference("switch_mute");
            switch_mute.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean old_value = sharedPreferences.getBoolean("isAutoSilenceChecked",false);
                    boolean isChecked;

                    if(old_value){
                        switch_mute.setChecked(false);
                        isChecked = false;
                    }
                    else{
                        switch_mute.setChecked(true);
                        isChecked = true;
                    }

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isAutoSilenceChecked", isChecked);
                    editor.apply();

                    return false;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
}
