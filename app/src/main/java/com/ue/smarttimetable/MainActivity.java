package com.ue.smarttimetable;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends Activity {
    private static GridView timeTable;
    private static CourseAdapter courseAdapter;
    private static NDrawerAdapter ndArrayAdapter;
    private static DrawerLayout nDrawerLayout;
    private static ArrayList<String> courseList;
    private static ListView nDListView;
    private static Handler nDHandler,twiceBackPressedCheckHandler;
    private static String departmentCoursesUrl=null ,fileName;
    private static ConnectivityManager connectivityManager;
    private static Vibrator vibrator;
    private static RelativeLayout relativeLayout;
    private static ImageView removeImage;
    private static String source_of_webpage=null;
    private static Context mainContext;
    private static int leftCoordinate=0,topCoordinate=0,deviceHeight,deviceWidth,densityDpi;
    private ImageButton menuButton;
    private AlphaAnimation alphaAnimation;
    private TextView txtView;
    private boolean twiceBackPressed=false;
    private static boolean isUserChanged = false;

    private static class NDrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            nDListView.setItemChecked(position, true);

            if(position!=1){
                if(!courseList.get(2).equals("Refresh to Load\nDepartment Courses")){
                    final TextView txtView = new TextView(mainContext);
                    txtView.setText(courseList.get(position - 1));
                    txtView.setBackgroundResource(R.drawable.drag_drop_courses);
                    txtView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    txtView.setWidth(68);
                    txtView.setHeight(56);
                    txtView.setX(30);
                    txtView.setY(30);
                    txtView.setTextColor(Color.parseColor("#0060ba"));

                    Log.i("Left Coordinate", Integer.toString(leftCoordinate));
                    Log.i("Top Coordinate",Integer.toString(topCoordinate));

                    relativeLayout.addView(txtView);

                    txtView.setOnTouchListener(new View.OnTouchListener() {
                        private boolean found=false;
                        private boolean isMove;
                        private int i,j=1,row,col;
                        private SharedPreferences.Editor e;
                        private SharedPreferences sharedPreferencesSaveCourse;
                        private int preferenceKey=0;
                        private String courseName=courseList.get(position-1);

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if(event.getAction()==MotionEvent.ACTION_DOWN){
                                isMove=false;

                                return true;
                            }
                            else if(event.getAction()==MotionEvent.ACTION_MOVE){
                                isMove=true;
                                txtView.setX((int) (event.getRawX() - deviceWidth / 10)); //7.2
                                txtView.setY((int) (event.getRawY() - deviceHeight / 15)); //13.3

                                Log.i("GetRawX", Float.toString(event.getRawX()));
                                Log.i("GetRawY", Float.toString(event.getRawY()));

                                Log.i("TextViewX",Float.toString(txtView.getX()));
                                Log.i("TextViewY",Float.toString(txtView.getY()));

                                for(i=1;i<=courseAdapter.getLessonPerDay();i++){
                                    for(j=1;j<=timeTable.getNumColumns()-1;j++){
                                        if(txtView.getX()>=leftCoordinate*j &&txtView.getX()<=leftCoordinate*(j+1)&&txtView.getY()>=topCoordinate*i - 30 &&txtView.getY()<=topCoordinate*(i+1) - 30){
                                            txtView.setTextColor(Color.WHITE);
                                            txtView.setBackgroundResource(R.drawable.course);
                                            removeImage.setVisibility(View.GONE);

                                            found=true;

                                            Log.i("Coordinate", Integer.toString(i).concat(":").concat(Integer.toString(j)));

                                            row=i;
                                            col=j;

                                            break;
                                        }
                                        else {
                                            found = false;
                                        }
                                    }
                                    if(found){
                                        break;
                                    }
                                }

                                if(!found){
                                    txtView.setTextColor(Color.parseColor("#0060ba"));
                                    txtView.setBackgroundResource(R.drawable.drag_drop_courses);
                                    removeImage.setVisibility(View.VISIBLE);
                                }

                                if(Math.abs(event.getRawX()-deviceWidth/2)<=deviceWidth/3.6&&Math.abs(event.getRawY()-deviceHeight)<=deviceHeight/3.2)
                                    removeImage.setImageResource(R.drawable.remove_hover);
                                else
                                    removeImage.setImageResource(R.drawable.remove);

                                return true;
                            }
                            else if(event.getAction()==MotionEvent.ACTION_UP){
                                if(Math.abs(event.getRawX()-deviceWidth/2)<=deviceWidth/3.6&&Math.abs(event.getRawY()-deviceHeight)<=deviceHeight/3.2){
                                    removeImage.getLayoutParams().width=75;
                                    removeImage.getLayoutParams().height=75;
                                    relativeLayout.removeView(v);
                                    removeImage.setVisibility(View.GONE);
                                    vibrator.vibrate(100);

                                    if(preferenceKey!=0){
                                        sharedPreferencesSaveCourse = mainContext.getSharedPreferences("courseAtPosition", Context.MODE_PRIVATE);
                                        e= sharedPreferencesSaveCourse.edit();
                                        e.putBoolean(Integer.toString(preferenceKey), false);
                                        e.putString(Integer.toString(preferenceKey).concat("_course_name"), "");
                                        e.apply();
                                    }
                                }

                                if(found){
                                    vibrator.vibrate(100);

                                    preferenceKey = (row*6)+col;

                                    sharedPreferencesSaveCourse = mainContext. getSharedPreferences("courseAtPosition", Context.MODE_PRIVATE);
                                    e= sharedPreferencesSaveCourse.edit();
                                    e.putBoolean(Integer.toString(preferenceKey), true);
                                    e.putString(Integer.toString(preferenceKey).concat("_course_name"), courseName);
                                    e.apply();

                                    relativeLayout.removeView(v);
                                    courseAdapter.refresh();
                                    timeTable.setAdapter(courseAdapter);
                                    timeTable.invalidateViews();
                                }

                                if(!isMove){
                                    AddCourseActivity.setCourse(0,courseName,0,0);
                                    Intent startAddCourseIntent = new Intent(mainContext,AddCourseActivity.class);
                                    mainContext.startActivity(startAddCourseIntent);
                                }

                                return true;
                            }

                            return false;
                        }
                    });
                }
                else if(position==2){
                    if(departmentCoursesUrl!=null&&connectivityManager.getActiveNetworkInfo()!=null&&connectivityManager.getActiveNetworkInfo().isConnected())
                        try {
                            new GetSourceCode().execute(new URL(departmentCoursesUrl));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                }
            }
            else{
                AddCourseActivity.setCourse(-1,"",0,0);
                Intent startAddCourseIntent = new Intent(mainContext,AddCourseActivity.class);
                startAddCourseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mainContext.startActivity(startAddCourseIntent);
            }

            nDHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nDListView.setItemChecked(position, false);
                    nDrawerLayout.closeDrawer(nDListView);

                }
            }, 500);
        }
    }

    private static class GetSourceCode extends AsyncTask<URL,Void,Void>{
        private URLConnection urlConnection=null;
        private BufferedReader bufferedReader;
        private StringBuilder stringBuilder;
        private String departmentId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(URL... params) {
            try {
                departmentId=params[0].toString().substring(params[0].toString().length() - 3, params[0].toString().length()-1);
                urlConnection=params[0].openConnection();
                urlConnection.connect();
                bufferedReader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
                stringBuilder=new StringBuilder("");
                String s;
                while((s=bufferedReader.readLine())!=null){
                    stringBuilder.append(s).append("\n");
                }
                bufferedReader.close();
                source_of_webpage=stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String course;
            courseList=new ArrayList<>();

            ArrayList<String> drawerCoursesList = new ArrayList<>();

            Log.i("DepartmentId", departmentId);

            if(source_of_webpage!=null){
                source_of_webpage = source_of_webpage.substring(source_of_webpage.indexOf("Ders Listesi"));
                source_of_webpage = source_of_webpage.substring(source_of_webpage.indexOf("<table>"));

                while(source_of_webpage.contains("<a href"))
                {
                    source_of_webpage = source_of_webpage.substring(source_of_webpage.indexOf("<a href"));
                    source_of_webpage = source_of_webpage.substring(source_of_webpage.indexOf('>') + 1);
                    course = source_of_webpage.substring(0, source_of_webpage.indexOf('-') - 1);
                    courseList.add(course);
                }

                courseList.remove(courseList.size()-1);
            }

            if(courseList.size()>0){
                File departmentCoursesFile=new File(mainContext.getFilesDir(),fileName + ".txt");
                try {
                    FileOutputStream departmentCoursesFileOutputStream = mainContext.openFileOutput(fileName + ".txt", Context.MODE_PRIVATE);
                    String newLine = "\n";
                    for (int i = 0; i < courseList.size(); i++) {
                        departmentCoursesFileOutputStream.write(courseList.get(i).getBytes());
                        departmentCoursesFileOutputStream.write(newLine.getBytes());
                    }
                    departmentCoursesFileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                departmentCoursesFile=new File(mainContext.getFilesDir(),fileName + "_user.txt");
                try {
                    FileOutputStream departmentCoursesFileOutputStream = mainContext.openFileOutput(fileName + "_user.txt", Context.MODE_PRIVATE);
                    String newLine = "\n";
                    for (int i = 0; i < courseList.size(); i++) {
                        departmentCoursesFileOutputStream.write(courseList.get(i).getBytes());
                        departmentCoursesFileOutputStream.write(newLine.getBytes());
                    }
                    departmentCoursesFileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                drawerCoursesList.add("+");

                File userAddedCoursesFile = new File(mainContext.getFilesDir(),"userAddedCourses.txt");
                if(userAddedCoursesFile.exists()){
                    try{
                        BufferedReader bufferedReader=new BufferedReader(new FileReader(userAddedCoursesFile));

                        while((course=bufferedReader.readLine())!=null){
                            drawerCoursesList.add(course);
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                for(int i=0;i<courseList.size();i++){
                    drawerCoursesList.add(courseList.get(i));
                }


            }
            else{
                drawerCoursesList.add("+");
                drawerCoursesList.add("Refresh to Load\nDepartment Courses");
            }

            ndArrayAdapter=new NDrawerAdapter(mainContext.getApplicationContext(),R.layout.drawer_list_item,drawerCoursesList);
            nDListView.setAdapter(ndArrayAdapter);
            nDListView.setOnItemClickListener(new NDrawerItemClickListener());
        }
    }

    public static void refreshGridView(){
        courseAdapter.refresh();
        timeTable.setAdapter(courseAdapter);
        timeTable.invalidateViews();
    }

    public static void changeNavigationDrawer(){
        isUserChanged = true;

        courseList = new ArrayList<>();
        courseList.add("+");
        String course;

        Log.i("CHANGE","CHANGE");

        boolean initialized=false;

        File userAddedCoursesFile = new File(mainContext.getFilesDir(),"userAddedCourses.txt");
        if(userAddedCoursesFile.exists()){
            try{
                BufferedReader bufferedReader=new BufferedReader(new FileReader(userAddedCoursesFile));

                while((course=bufferedReader.readLine())!=null){
                    courseList.add(course);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        SharedPreferences sharedPreferencesSettings=PreferenceManager.getDefaultSharedPreferences(mainContext);
        String department=sharedPreferencesSettings.getString("department", "COMPE");

        switch (department) {
            case "COMPE":
                departmentCoursesUrl = "http://compe.atilim.edu.tr/academicprograms/courseList/id/12/";
                fileName="COMPE";
                break;
            case "ISE":
                departmentCoursesUrl = "http://ise.atilim.edu.tr/academicprograms/courseList/id/27/";
                fileName="ISE";
                break;
            case "EE":
                departmentCoursesUrl = "http://eee.atilim.edu.tr/academicprograms/courseList/id/30/";
                fileName="EE";
                break;
            case "IE":
                departmentCoursesUrl = "http://ie.atilim.edu.tr/academicprograms/courseList/id/11/";
                fileName="IE";
                break;
            case "ENE":
                departmentCoursesUrl = "http://energy.atilim.edu.tr/academicprograms/courseList/id/33/";
                fileName="ENE";
                break;
            case "MFGE":
                departmentCoursesUrl = "http://mfge.atilim.edu.tr/academicprograms/courseList/id/8/";
                fileName="MFGE";
                break;
            case "CE":
                departmentCoursesUrl = "http://ce.atilim.edu.tr/academicprograms/courseList/id/50/";
                fileName="CE";
                break;
            case "CEAC":
                departmentCoursesUrl = "http://ceac.atilim.edu.tr/academicprograms/courseList/id/32/";
                fileName="COMPE";
                break;
            case "ME":
                departmentCoursesUrl = "http://me.atilim.edu.tr/academicprograms/courseList/id/47/";
                fileName="ME";
                break;
            case "MATE":
                departmentCoursesUrl = "http://mate.atilim.edu.tr/academicprograms/courseList/id/34/";
                fileName="MATE";
                break;
            case "MECE":
                departmentCoursesUrl = "http://mechatronics.atilim.edu.tr/academicprograms/courseList/id/17/";
                fileName="MECE";
                break;
            case "AE":
                departmentCoursesUrl = "http://ae.atilim.edu.tr/academicprograms/courseList/id/36/";
                fileName="AE";
                break;
            case "SE":
                departmentCoursesUrl = "http://se.atilim.edu.tr/academicprograms/courseList/id/15/";
                fileName="SE";
                break;
            case "LAW":
                departmentCoursesUrl = "http://law.atilim.edu.tr/academicprograms/courseList/id/45";
                fileName="LAW";
                break;
        }

        File departmentCoursesFile = new File(mainContext.getFilesDir(),fileName+".txt");
        if(departmentCoursesFile.exists()){
            try{
                BufferedReader bufferedReader=new BufferedReader(new FileReader(departmentCoursesFile));

                while((course=bufferedReader.readLine())!=null){
                    Log.i("COURSE 1",course);
                    courseList.add(course);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            if(departmentCoursesUrl!=null&&connectivityManager.getActiveNetworkInfo()!=null&&connectivityManager.getActiveNetworkInfo().isConnected()) {
                try {
                    new GetSourceCode().execute(new URL(departmentCoursesUrl));
                    initialized=true;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            else{
                courseList.add("Refresh to Load\nDepartment Courses");
            }
        }

        if(!initialized){
            ndArrayAdapter=new NDrawerAdapter(mainContext,R.layout.drawer_list_item,courseList);
            nDListView.setAdapter(ndArrayAdapter);
            nDListView.setOnItemClickListener(new NDrawerItemClickListener());
        }
    }

    public static void deleteFromNavigationDrawer(int position,int howMany){
        courseList = new ArrayList<>();
        String course;

        File userAddedCoursesFile = new File(mainContext.getFilesDir(),"userAddedCourses.txt");
        if(userAddedCoursesFile.exists()){
            try{
                BufferedReader bufferedReader=new BufferedReader(new FileReader(userAddedCoursesFile));

                while((course=bufferedReader.readLine())!=null){
                    courseList.add(course);
                }
                bufferedReader.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        if(courseList.size()>0&&position < howMany + courseList.size()){
            position = position - howMany;

            try{
                FileOutputStream userAddedCoursesFileOutputStream = mainContext.openFileOutput("userAddedCourses.txt",Context.MODE_PRIVATE);
                String newLine = "\n";
                for(int i=0;i<courseList.size();i++){
                    if(i!=position){
                        userAddedCoursesFileOutputStream.write(courseList.get(i).getBytes());
                        userAddedCoursesFileOutputStream.write(newLine.getBytes());
                    }
                }
                userAddedCoursesFileOutputStream.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            int increment = 0;

            position = position - howMany - courseList.size();

            courseList = new ArrayList<>();

            File departmentCoursesFile = new File(mainContext.getFilesDir(),fileName+"_user.txt");
            if(departmentCoursesFile.exists()){
                try{
                    BufferedReader bufferedReader=new BufferedReader(new FileReader(departmentCoursesFile));

                    while((course=bufferedReader.readLine())!=null){
                        if(increment!=position){
                            courseList.add(course);
                        }
                        increment++;
                    }

                    bufferedReader.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                try{
                    FileOutputStream allCoursesFileOutputStream = mainContext.openFileOutput(fileName+"_user.txt",Context.MODE_PRIVATE);
                    String newLine = "\n";
                    for(int i=0;i<courseList.size();i++){
                        allCoursesFileOutputStream.write(courseList.get(i).getBytes());
                        allCoursesFileOutputStream.write(newLine.getBytes());
                    }
                    allCoursesFileOutputStream.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        courseList = new ArrayList<>();
        courseList.add("+");

        userAddedCoursesFile = new File(mainContext.getFilesDir(),"userAddedCourses.txt");
        if(userAddedCoursesFile.exists()){
            try{
                BufferedReader bufferedReader=new BufferedReader(new FileReader(userAddedCoursesFile));

                while((course=bufferedReader.readLine())!=null){
                    courseList.add(course);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        userAddedCoursesFile = new File(mainContext.getFilesDir(),fileName+"_user.txt");
        if(userAddedCoursesFile.exists()){
            try{
                BufferedReader bufferedReader=new BufferedReader(new FileReader(userAddedCoursesFile));

                while((course=bufferedReader.readLine())!=null){
                    courseList.add(course);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            courseList.add("Refresh to Load\nDepartment Courses");
        }

        ndArrayAdapter=new NDrawerAdapter(mainContext,R.layout.drawer_list_item,courseList);
        nDListView.setAdapter(ndArrayAdapter);
        nDListView.setOnItemClickListener(new NDrawerItemClickListener());
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean initialized=false;

        Log.i("MainActivity.class", "Activity is resumed");

        SharedPreferences sharedPreferencesSettings=PreferenceManager.getDefaultSharedPreferences(this);

        boolean isEnabled = sharedPreferencesSettings.getBoolean("isAutoSilenceChecked",false);
        Log.i("IsEnabled",Boolean.toString(isEnabled));

        Intent intent = new Intent(getApplicationContext(), TimeService.class);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        //PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_NO_CREATE);

        if(alarmIntent==null) {
            alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
            Log.i("PendingIntent", "PendingIntent is null ");
        }
        else
            Log.i("PendingIntent","PendingIntent is not null");

        if(isEnabled) {
            Calendar calendar = Calendar.getInstance();
            int minute = calendar.get(Calendar.MINUTE);
            int targetTime = (60 - calendar.get(Calendar.SECOND)) * 1000;
            alarmManager.setInexactRepeating(AlarmManager.RTC, targetTime, 1000 * 60, alarmIntent);
        }
        else{
            alarmManager.cancel(alarmIntent);
            Log.i("PendingIntent","PendingIntent is cancelled");
        }
        /*else if(pendingIntent != null){
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.i("PendingIntent","PendingIntent is cancelled");
        }*/

        courseAdapter.refresh();
        timeTable.setAdapter(courseAdapter);
        timeTable.invalidateViews();

        String department=sharedPreferencesSettings.getString("department", "COMPE");

        Log.i("Selected Department ", department);

        switch (department) {
            case "COMPE":
                departmentCoursesUrl = "http://compe.atilim.edu.tr/academicprograms/courseList/id/12/";
                fileName="COMPE";
                break;
            case "ISE":
                departmentCoursesUrl = "http://ise.atilim.edu.tr/academicprograms/courseList/id/27/";
                fileName="ISE";
                break;
            case "EE":
                departmentCoursesUrl = "http://eee.atilim.edu.tr/academicprograms/courseList/id/30/";
                fileName="EE";
                break;
            case "IE":
                departmentCoursesUrl = "http://ie.atilim.edu.tr/academicprograms/courseList/id/11/";
                fileName="IE";
                break;
            case "ENE":
                departmentCoursesUrl = "http://energy.atilim.edu.tr/academicprograms/courseList/id/33/";
                fileName="ENE";
                break;
            case "MFGE":
                departmentCoursesUrl = "http://mfge.atilim.edu.tr/academicprograms/courseList/id/8/";
                fileName="MFGE";
                break;
            case "CE":
                departmentCoursesUrl = "http://ce.atilim.edu.tr/academicprograms/courseList/id/50/";
                fileName="CE";
                break;
            case "CEAC":
                departmentCoursesUrl = "http://ceac.atilim.edu.tr/academicprograms/courseList/id/32/";
                fileName="COMPE";
                break;
            case "ME":
                departmentCoursesUrl = "http://me.atilim.edu.tr/academicprograms/courseList/id/47/";
                fileName="ME";
                break;
            case "MATE":
                departmentCoursesUrl = "http://mate.atilim.edu.tr/academicprograms/courseList/id/34/";
                fileName="MATE";
                break;
            case "MECE":
                departmentCoursesUrl = "http://mechatronics.atilim.edu.tr/academicprograms/courseList/id/17/";
                fileName="MECE";
                break;
            case "AE":
                departmentCoursesUrl = "http://ae.atilim.edu.tr/academicprograms/courseList/id/36/";
                fileName="AE";
                break;
            case "SE":
                departmentCoursesUrl = "http://se.atilim.edu.tr/academicprograms/courseList/id/15/";
                fileName="SE";
                break;
            case "LAW":
                departmentCoursesUrl = "http://law.atilim.edu.tr/academicprograms/courseList/id/45";
                fileName="LAW";
                break;
        }

        courseList=new ArrayList<>();
        courseList.add("+");

        File fileControl = new File(getFilesDir(),"userAddedCourses.txt");
        if(fileControl.exists()){

            try{
                BufferedReader bufferedReader=new BufferedReader(new FileReader(fileControl));
                String course;

                while((course=bufferedReader.readLine())!=null){
                    courseList.add(course);
                }

                bufferedReader.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        if(isUserChanged){
            fileControl = new File(getFilesDir(),fileName+".txt");
            isUserChanged = false;
        }
        else
            fileControl = new File(getFilesDir(),fileName+"_user.txt");

        if(fileControl.exists()){

            try{
                BufferedReader bufferedReader=new BufferedReader(new FileReader(fileControl));
                String course;

                while((course=bufferedReader.readLine())!=null){
                    courseList.add(course);
                }

                bufferedReader.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            if(departmentCoursesUrl!=null&&connectivityManager.getActiveNetworkInfo()!=null&&connectivityManager.getActiveNetworkInfo().isConnected()) {
                try {
                    new GetSourceCode().execute(new URL(departmentCoursesUrl));
                    initialized = true;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            else{
                courseList.add("Refresh to Load\nDepartment Courses");
            }
        }

        if(!initialized){
            ndArrayAdapter=new NDrawerAdapter(getApplicationContext(),R.layout.drawer_list_item,courseList);
            nDListView.setAdapter(ndArrayAdapter);
            nDListView.setOnItemClickListener(new NDrawerItemClickListener());
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String lessonPerDayS=sharedPreferences.getString("lesson_per_day", "9");
        int lessonPerDay = Integer.parseInt(lessonPerDayS);

        if(densityDpi==120){
            leftCoordinate=(int) ((0.75*3)+(deviceWidth/7));
            topCoordinate=(int) ((0.75*3)+(deviceWidth/(lessonPerDay/1.1)));
        }
        else if(densityDpi==160){
            leftCoordinate=(1*3+(deviceWidth/7));
            topCoordinate=(int) (1*3+(deviceWidth/(lessonPerDay/1.1)));
        }
        else if(densityDpi==213){
            leftCoordinate=(int) ((1.33*3)+(deviceWidth/7));
            topCoordinate=(int) ((1.33*3)+(deviceWidth/(lessonPerDay/1.1)));
        }
        else if(densityDpi==240){
            leftCoordinate=(int) ((1.5*3)+(deviceWidth/7));
            topCoordinate=(int) ((1.5*3)+(deviceWidth/(lessonPerDay/1.1)));
        }
        else if(densityDpi==280){
            leftCoordinate=(int) ((1.75*3)+(deviceWidth/7));
            topCoordinate=(int) ((1.75*3)+(deviceWidth/(lessonPerDay/1.1)));
        }
        else if(densityDpi==320){
            leftCoordinate=((2*3)+(deviceWidth/7));
            topCoordinate=(int) ((2*3)+(deviceWidth/(lessonPerDay/1.1)));
        }
        else if(densityDpi==360){
            leftCoordinate=(int) ((2.25*3)+(deviceWidth/7));
            topCoordinate=(int) ((2.25*3)+(deviceWidth/(lessonPerDay/1.1)));
        }
        else if(densityDpi==400){
            leftCoordinate=(int) ((2.50*3)+(deviceWidth/7));
            topCoordinate=(int) ((2.50*3)+(deviceWidth/(lessonPerDay/1.1)));
        }
        else if(densityDpi==420){
            leftCoordinate=(int) ((2.75*3)+(deviceWidth/7));
            topCoordinate=(int) ((2.75*3)+(deviceWidth/(lessonPerDay/1.1)));
        }
        else if(densityDpi==480){
            leftCoordinate=((3*3)+(deviceWidth/7));
            topCoordinate=(int) ((3*3)+(deviceWidth/(lessonPerDay/1.1)));
        }
        else if(densityDpi==560){
            leftCoordinate=(int) ((3.5*3)+(deviceWidth/7));
            topCoordinate=(int) ((3.5*3)+(deviceWidth/(lessonPerDay/1.1)));
        }
        else if(densityDpi==640){
            leftCoordinate=((4*3)+(deviceWidth/7));
            topCoordinate=(int) ((4*3)+(deviceWidth/(lessonPerDay/1.1)));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i("MainActivity.class", "Activity is stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("MainActivity.class", "Activity is destroyed");
    }



    @Override
    public void onBackPressed() {
        if(twiceBackPressed){
            twiceBackPressed=false;
            twiceBackPressedCheckHandler.removeCallbacks(null);
            finish();
        }

        Toast.makeText(getApplicationContext(),"Çıkmak için bir kere daha basınız",Toast.LENGTH_SHORT).show();

        twiceBackPressed=true;

        twiceBackPressedCheckHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                twiceBackPressed=false;
            }
        },2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayout=(RelativeLayout) findViewById(R.id.relativeLayout);
        alphaAnimation=new AlphaAnimation(1f,0.5f);
        nDrawerLayout=(DrawerLayout) findViewById(R.id.nDrawerLayout);
        nDListView=(ListView) findViewById(R.id.nDListView);
        removeImage =(ImageView) findViewById(R.id.imgRemoveView);
        removeImage.setVisibility(View.GONE);
        txtView = new TextView(getApplicationContext());
        txtView.setText(R.string.nDrawerHeaderText);
        txtView.setTextSize(25);
        txtView.setTextColor(Color.WHITE);
        nDListView.addHeaderView(txtView);
        timeTable=(GridView) findViewById(R.id.gridView);
        courseAdapter=new CourseAdapter(this);
        courseAdapter.refresh();
        courseAdapter.initialize(relativeLayout, removeImage);
        timeTable.setAdapter(courseAdapter);
        nDHandler =new Handler();
        twiceBackPressedCheckHandler=new Handler();
        vibrator=(Vibrator) getSystemService(VIBRATOR_SERVICE);
        mainContext = getApplicationContext();
        connectivityManager=(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        /*File userAddedCoursesFile = new File(mainContext.getFilesDir(),"userAddedCourses.txt");
        if(userAddedCoursesFile.exists())
            userAddedCoursesFile.delete();*/

        DisplayMetrics displayMetrics=getResources().getDisplayMetrics();
        deviceHeight=displayMetrics.heightPixels;
        deviceWidth=displayMetrics.widthPixels;
        densityDpi=displayMetrics.densityDpi;

        timeTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.startAnimation(alphaAnimation);

                int row = position / 6 - 1;

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String hourS = sharedPreferences.getString("start_hour", "9");
                String minuteS = sharedPreferences.getString("start_minute", "30");
                String breakTimeS = sharedPreferences.getString("break_time", "10");
                String lessonLengthS = sharedPreferences.getString("lesson_length", "50");

                int hour = Integer.parseInt(hourS);
                int minute = Integer.parseInt(minuteS);
                int breakTime = Integer.parseInt(breakTimeS);
                int lessonLength = Integer.parseInt(lessonLengthS);

                for (int i = 0; i < row; i++) {
                    int extrahour;
                    minute = minute + lessonLength;
                    extrahour = minute / 60;
                    hour = hour + extrahour;
                    extrahour = hour / 25;
                    hour = hour % 24;
                    hour = hour + extrahour;
                    minute = minute % 60;

                    minute = minute + breakTime;
                    extrahour = minute / 60;
                    hour = hour + extrahour;
                    extrahour = hour / 25;
                    hour = hour % 24;
                    hour = hour + extrahour;
                    minute = minute % 60;
                }

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

                AddCourseActivity.setCourse(position, "", Integer.parseInt(newHour), Integer.parseInt(newMinute));
                Intent startAddCourseIntent = new Intent(getApplicationContext(), AddCourseActivity.class);
                startActivity(startAddCourseIntent);
            }
        });

        menuButton=(ImageButton) findViewById(R.id.imgButtonMenu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(alphaAnimation);
                Intent startSettingsActivity = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(startSettingsActivity);
            }
        });
    }
}
