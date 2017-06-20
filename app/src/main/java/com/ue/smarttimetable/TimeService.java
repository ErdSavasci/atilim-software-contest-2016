package com.ue.smarttimetable;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Pc on 18.04.2016.
 */
public class TimeService extends BroadcastReceiver {
    boolean isFirstTime = true;
    boolean changed=false;
    int ringerMode;
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        int hourNow = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteNow = calendar.get(Calendar.MINUTE);
        int dayNumber = calendar.get(Calendar.DAY_OF_WEEK); //2,3,4,5,6

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if(dayNumber==2||dayNumber==3||dayNumber==4||dayNumber==5||dayNumber==6){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            String estimatedTimeBeforeSilence = sharedPreferences.getString("time_before_silence","5");
            int estimatedTimeBeforeSilence_Integer = Integer.parseInt(estimatedTimeBeforeSilence);

            String hourS=sharedPreferences.getString("start_hour","9");
            String minuteS = sharedPreferences.getString("start_minute","30");
            String lessonPerDayS=sharedPreferences.getString("lesson_per_day", "9");
            String breakTimeS=sharedPreferences.getString("break_time","10");
            String lessonLengthS=sharedPreferences.getString("lesson_length", "50");

            int startHour = Integer.parseInt(hourS);
            int startMinute = Integer.parseInt(minuteS);
            int lessonPerDay = Integer.parseInt(lessonPerDayS);
            int breakTime = Integer.parseInt(breakTimeS);
            int lessonLength = Integer.parseInt(lessonLengthS);

            int finishHour;
            int finishMinute;
            int extrahour;

            SharedPreferences sharedPreferencesCourse = context.getSharedPreferences("courseAtPosition",Context.MODE_PRIVATE);

            for(int i=1;i<=lessonPerDay;i++){
                finishMinute = startMinute + lessonLength;
                extrahour = finishMinute / 60;
                finishHour = startHour + extrahour;
                extrahour = startHour / 25;
                startHour = startHour % 24;
                startHour = startHour + extrahour;
                finishMinute = finishMinute % 60;

                boolean isFull = sharedPreferencesCourse.getBoolean(Integer.toString((dayNumber-1)+(i*6)),false);

                if(hourNow==startHour && Math.abs(startMinute-minuteNow) <= estimatedTimeBeforeSilence_Integer && isFull){
                    Log.i("Mute settings are set","OK");
                    ringerMode = audioManager.getRingerMode();
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    changed = true;
                }
                else if(hourNow == finishHour && minuteNow == finishMinute && changed){
                    audioManager.setRingerMode(ringerMode);
                    changed = false;
                }

                startMinute = finishMinute + breakTime;
                extrahour = startMinute / 60;
                startHour = finishHour + extrahour;
                extrahour = startHour / 25;
                startHour = startHour % 24;
                startHour = startHour + extrahour;
                startMinute = startMinute % 60;

            }

            if(changed){
                audioManager.setRingerMode(ringerMode);
                changed = false;
            }
        }else{
            if(changed){
                audioManager.setRingerMode(ringerMode);
                changed = false;
            }
        }


        Log.i("TimeService","Service is started");
    }
}
