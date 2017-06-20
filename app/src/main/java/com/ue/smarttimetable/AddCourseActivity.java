package com.ue.smarttimetable;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddCourseActivity extends Activity {
    private TextView textViewCourseName,textViewCourseStartTime,textViewCourseFinishTime;
    private EditText editTextCourseName;
    private static int courseHour=0,courseMinute=0,courseLength;
    private static String courseNAME="",courseLengthS;
    private static int preferenceKey;
    private ImageButton btnCourseInfoSave,imgButtonCourseDelete;
    private SharedPreferences.Editor e;
    private SharedPreferences sharedPreferencesSettings;
    private String lessonPerDayS,hourS,minuteS,breakTimeS,lessonLengthS;
    private int lessonPerDay,hour,minute,breakTime,lessonLength,lessonPerDayTemp,hourTemp,minuteTemp,breakTimeTemp,extrahour;
    private int finishPosition=0;
    private boolean onceTouch=true;
    private Spinner spinnerCourseStartTime,spinnerCourseFinishTime;
    private int pos = -1;
    private boolean firstTime = true;

    public static void setCourse(int prferenceKey,String courseName,int hour,int minute){
        courseNAME=courseName;
        courseHour=hour;
        courseMinute=minute;
        preferenceKey=prferenceKey;
    }

    private void setSpinners(){
        sharedPreferencesSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        lessonPerDayS=sharedPreferencesSettings.getString("lesson_per_day", "9");
        hourS=sharedPreferencesSettings.getString("start_hour", "9");
        minuteS = sharedPreferencesSettings.getString("start_minute", "30");
        breakTimeS=sharedPreferencesSettings.getString("break_time", "10");
        lessonLengthS=sharedPreferencesSettings.getString("lesson_length", "50");

        lessonPerDay=Integer.parseInt(lessonPerDayS);
        hour=Integer.parseInt(hourS);
        minute=Integer.parseInt(minuteS);
        breakTime=Integer.parseInt(breakTimeS);
        lessonLength=Integer.parseInt(lessonLengthS);

        String []startTimes = new String[lessonPerDay];
        String []finishTimes = new String[lessonPerDay];

        int index = 0;

        Log.i("Finish Position",Integer.toString(finishPosition));

        for(int i=0;i<lessonPerDay;i++){
            String newHour=Integer.toString(hour),newMinute=Integer.toString(minute);
            if (hour == 0) {
                newHour = "00";
            }
            else if (hour>0&&hour<=9) {
                newHour = "0".concat(Integer.toString(hour));
            }
            if (minute == 0) {
                newMinute = "00";
            }
            else if (minute>0&&minute<=9) {
                newMinute = "0".concat(Integer.toString(minute));
            }

            startTimes[i]=newHour.concat(".").concat(newMinute);

            minute = minute + lessonLength;
            int extrahour = minute / 60;
            hour = hour + extrahour;
            extrahour = hour / 25;
            hour = hour % 24;
            hour = hour + extrahour;
            minute = minute % 60;

            newHour=Integer.toString(hour);
            newMinute=Integer.toString(minute);

            if (hour == 0) {
                newHour = "00";
            }
            else if (hour>0&&hour<=9) {
                newHour = "0".concat(Integer.toString(hour));
            }
            if (minute == 0) {
                newMinute = "00";
            }
            else if (minute>0&&minute<=9) {
                newMinute = "0".concat(Integer.toString(minute));
            }

            if(i>=finishPosition){
                finishTimes[index]=newHour.concat(".").concat(newMinute);
                index++;
            }

            minute = minute + breakTime;
            extrahour = minute / 60;
            hour = hour + extrahour;
            extrahour = hour / 25;
            hour = hour % 24;
            hour = hour + extrahour;
            minute = minute % 60;
        }

        String []newFinishTimes = new String[lessonPerDay - finishPosition];
        System.arraycopy(finishTimes, 0, newFinishTimes, 0, newFinishTimes.length);

        spinnerCourseStartTime.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, startTimes));
        spinnerCourseFinishTime.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, newFinishTimes));

        spinnerCourseStartTime.setSelection(finishPosition);
        spinnerCourseFinishTime.setSelection(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        spinnerCourseStartTime = (Spinner) findViewById(R.id.spinnerCourseStartTime);
        spinnerCourseFinishTime = (Spinner) findViewById(R.id.spinnerCourseFinishTime);

        spinnerCourseStartTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(pos != position){
                    finishPosition = spinnerCourseStartTime.getSelectedItemPosition();
                    setSpinners();
                    pos = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setSpinners();

        textViewCourseName = (TextView) findViewById(R.id.textViewCourseName);
        textViewCourseStartTime = (TextView) findViewById(R.id.textViewCourseStartTime);
        textViewCourseFinishTime = (TextView) findViewById(R.id.textViewCourseFinishTime);

        editTextCourseName = (EditText) findViewById(R.id.editTextCourseName);

        btnCourseInfoSave = (ImageButton) findViewById(R.id.imgButtonCourseInfoSave);
        btnCourseInfoSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(preferenceKey!=0&&preferenceKey!=-1){ //If the course isn't placed then finish activity
                    SharedPreferences sharedPreferencesSaveCourse=getSharedPreferences("courseAtPosition", Context.MODE_PRIVATE);
                    e=sharedPreferencesSaveCourse.edit();
                    String deleteAllOccurrenses = editTextCourseName.getText().toString();

                    int temp = preferenceKey % 6;
                    temp = temp + 6;

                    if(!deleteAllOccurrenses.equals("")){
                        for(int i=0;i<lessonPerDay;i++){
                            if(deleteAllOccurrenses.equals(sharedPreferencesSaveCourse.getString(Integer.toString(temp).concat("_course_name"), ""))){
                                e.putBoolean(Integer.toString(temp), false);
                                e.putString(Integer.toString(temp).concat("_course_name"), "");
                                e.apply();
                            }
                            temp = temp + 6;
                        }
                    }

                    sharedPreferencesSaveCourse=getSharedPreferences("courseAtPosition", Context.MODE_PRIVATE);
                    e=sharedPreferencesSaveCourse.edit();

                    temp = preferenceKey % 6;
                    temp = temp + 6;

                    for(int i=0;i<lessonPerDay;i++){
                        if(i==spinnerCourseStartTime.getSelectedItemPosition()){
                            for(int j=0;j<=spinnerCourseFinishTime.getSelectedItemPosition();j++){
                                e.putBoolean(Integer.toString(temp),true);
                                e.putString(Integer.toString(temp).concat("_course_name"), editTextCourseName.getText().toString());
                                e.apply();

                                temp = temp + 6;
                            }
                        }
                        else
                            temp = temp + 6;
                    }
                }
                else if(preferenceKey==-1){
                    String newLine = "\n";
                    File userAddedCourses = new File(getFilesDir(),"userAddedCourses.txt");
                    try {
                        FileOutputStream userAddedCoursesFileOutputStream = openFileOutput("userAddedCourses.txt",Context.MODE_APPEND);
                        userAddedCoursesFileOutputStream.write(editTextCourseName.getText().toString().getBytes());
                        userAddedCoursesFileOutputStream.write(newLine.getBytes());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                finish();
            }
        });

        imgButtonCourseDelete = (ImageButton) findViewById(R.id.imgButtonCourseDelete);
        imgButtonCourseDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(preferenceKey!=0&&preferenceKey!=-1){ //If the course isn't placed then finish activity
                    SharedPreferences sharedPreferencesSaveCourse=getSharedPreferences("courseAtPosition", Context.MODE_PRIVATE);
                    e=sharedPreferencesSaveCourse.edit();
                    e.putBoolean(Integer.toString(preferenceKey),false);
                    e.apply();
                }

                finish();
            }
        });

        if(!courseNAME.equals("")){
            editTextCourseName.setText(courseNAME);
            if(courseHour!=0&&courseMinute!=0){
                int i;
                for(i=0;i<lessonPerDay;i++){
                    if(spinnerCourseStartTime.getItemAtPosition(i).equals(Integer.toString(courseHour).concat(".").concat(Integer.toString(courseMinute))))
                        break;
                }

                if(i!=lessonPerDay)
                    spinnerCourseStartTime.setSelection(i);

                SharedPreferences sharedPreferencesSettings= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                courseLengthS=sharedPreferencesSettings.getString("lesson_length","50");
                courseLength= Integer.parseInt(courseLengthS);

                extrahour = 0;
                courseMinute=courseMinute+courseLength;
                extrahour=courseMinute/60;
                courseHour=courseHour+extrahour;
                courseMinute=courseMinute%60;

                for(i=0;i<lessonPerDay;i++){
                    if(spinnerCourseFinishTime.getItemAtPosition(i).equals(Integer.toString(courseHour).concat(".").concat(Integer.toString(courseMinute))))
                        break;
                }

                if(i!=lessonPerDay)
                    spinnerCourseFinishTime.setSelection(i);
            }
        }
        else if(preferenceKey!=-1){
            int i;

            for(i=0;i<lessonPerDay;i++){
                if(spinnerCourseStartTime.getItemAtPosition(i).equals(Integer.toString(courseHour).concat(".").concat(Integer.toString(courseMinute))))
                    break;
            }

            if(i!=lessonPerDay)
                spinnerCourseStartTime.setSelection(i);

            SharedPreferences sharedPreferencesSettings= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            courseLengthS=sharedPreferencesSettings.getString("lesson_length","50");
            courseLength= Integer.parseInt(courseLengthS);

            int extrahour;
            courseMinute=courseMinute+courseLength;
            extrahour=courseMinute/60;
            courseHour=courseHour+extrahour;
            courseMinute=courseMinute%60;

            for(i=0;i<lessonPerDay;i++){
                if(spinnerCourseFinishTime.getItemAtPosition(i).equals(Integer.toString(courseHour).concat(".").concat(Integer.toString(courseMinute))))
                    break;
            }

            if(i!=lessonPerDay)
                spinnerCourseFinishTime.setSelection(i);
        }
        else{
            editTextCourseName.setText("");
            textViewCourseStartTime.setVisibility(View.GONE);
            textViewCourseFinishTime.setVisibility(View.GONE);
            spinnerCourseStartTime.setVisibility(View.GONE);
            spinnerCourseFinishTime.setVisibility(View.GONE);
        }
    }
}
