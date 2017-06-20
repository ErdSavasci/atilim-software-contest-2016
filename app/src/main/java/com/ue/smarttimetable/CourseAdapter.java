package com.ue.smarttimetable;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridLayout.Spec;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Pc on 24.02.2016.
 */
public class CourseAdapter extends BaseAdapter {
    private Context context;
    private Integer courseDrawable;
    private int deviceWidth,deviceHeight;
    private DisplayMetrics displayMetrics;
    private int leftCoordinate,topCoordinate;
    private ImageView removeImage;
    private RelativeLayout relativeLayout;
    private int lessonPerDay=9,hour=9,minute=30,breakTime=10,lessonLength=50;
    private String lessonPerDayS,hourS,minuteS,breakTimeS,lessonLengthS,nextHourS,nextMinuteS;
    private TextView txtView;
    private boolean longPressed=false;

    private class CourseImage extends ImageView{
        public CourseImage(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);


        }
    }

    public CourseAdapter(Context context) {
        super();
        this.context=context;
        courseDrawable=R.drawable.course;
        displayMetrics=context.getResources().getDisplayMetrics();
        deviceHeight=displayMetrics.heightPixels;
        deviceWidth=displayMetrics.widthPixels;
    }

    public void initialize(RelativeLayout relativeLayout,ImageView removeImage){
        this.removeImage=removeImage;
        this.relativeLayout=relativeLayout;
    }

    @Override
    public int getCount() {
        return 6*(lessonPerDay+1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0 && position != 1 && position != 2 && position != 3 && position != 4 && position != 5 && position != 6 && position != 12 && position != 18
                && position != 24 && position != 30 && position != 36 && position != 42 && position != 48 && position != 54;
    }

    @Override
    public ImageView getItem(int position) {
        if(position!=0&&position!=1&&position!=2&&position!=3&&position!=4&&position!=5&&position%6!=0){
            ImageView returnImageView=new ImageView(context);
            returnImageView.setImageResource(courseDrawable);
            return returnImageView;
        }
        else
            return null;
    }

    public int getLessonPerDay(){
        return lessonPerDay;
    }

    public void refresh(){
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        lessonPerDayS=sharedPreferences.getString("lesson_per_day", "9");
        hourS=sharedPreferences.getString("start_hour","9");
        minuteS = sharedPreferences.getString("start_minute","30");
        breakTimeS=sharedPreferences.getString("break_time","10");
        lessonLengthS=sharedPreferences.getString("lesson_length", "50");

        lessonPerDay=Integer.parseInt(lessonPerDayS);
        hour=Integer.parseInt(hourS);
        minute=Integer.parseInt(minuteS);
        breakTime=Integer.parseInt(breakTimeS);
        lessonLength=Integer.parseInt(lessonLengthS);

        int densityDpi=displayMetrics.densityDpi;

        if(densityDpi==120){
            leftCoordinate=(int) ((0.75*3)+(deviceWidth/7));
            topCoordinate=(int) ((0.75*3)+(deviceWidth / (lessonPerDay / 1.1)));
        }
        else if(densityDpi==160){
            leftCoordinate=(3+(deviceWidth/7));
            topCoordinate=(int) (3+(deviceWidth / (lessonPerDay / 1.1)));
        }
        else if(densityDpi==213){
            leftCoordinate=(int) ((1.33*3)+(deviceWidth/7));
            topCoordinate=(int) ((1.33*3)+(deviceWidth / (lessonPerDay / 1.1)));
        }
        else if(densityDpi==240){
            leftCoordinate=(int) ((1.5*3)+(deviceWidth/7));
            topCoordinate=(int) ((1.5*3)+(deviceWidth / (lessonPerDay / 1.1)));
        }
        else if(densityDpi==280){
            leftCoordinate=(int) ((1.75*3)+(deviceWidth/7));
            topCoordinate=(int) ((1.75*3)+(deviceWidth / (lessonPerDay / 1.1)));
        }
        else if(densityDpi==320){
            leftCoordinate=((2*3)+(deviceWidth/7));
            topCoordinate=(int) ((2*3)+(deviceWidth / (lessonPerDay / 1.1)));
        }
        else if(densityDpi==360){
            leftCoordinate=(int) ((2.25*3)+(deviceWidth/7));
            topCoordinate=(int) ((2.25*3)+(deviceWidth / (lessonPerDay / 1.1)));
        }
        else if(densityDpi==400){
            leftCoordinate=(int) ((2.50*3)+(deviceWidth/7));
            topCoordinate=(int) ((2.50*3)+(deviceWidth / (lessonPerDay / 1.1)));
        }
        else if(densityDpi==420){
            leftCoordinate=(int) ((2.75*3)+(deviceWidth/7));
            topCoordinate=(int) ((2.75*3)+(deviceWidth / (lessonPerDay / 1.1)));
        }
        else if(densityDpi==480){
            leftCoordinate=((3*3)+(deviceWidth/7));
            topCoordinate=(int) ((3*3)+(deviceWidth / (lessonPerDay / 1.1)));
        }
        else if(densityDpi==560){
            leftCoordinate=(int) ((3.5*3)+(deviceWidth/7));
            topCoordinate=(int) ((3.5*3)+(deviceWidth / (lessonPerDay / 1.1)));
        }
        else if(densityDpi==640){
            leftCoordinate=((4*3)+(deviceWidth/7));
            topCoordinate=(int) ((4*3)+(deviceWidth / (lessonPerDay / 1.1)));
        }
    }

    public void InitializeTextView(){
        txtView = new TextView(context);
        txtView.setBackgroundResource(R.drawable.drag_drop_courses);
        txtView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        txtView.setWidth(68);
        txtView.setHeight(56);
        txtView.setBackgroundResource(courseDrawable);
        txtView.setTextColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TextView gridViewTitles;

        if(convertView==null){
            gridViewTitles= new TextView(context);
            gridViewTitles.setLayoutParams(new GridView.LayoutParams((int) (deviceWidth / (7)), (int) (deviceWidth / (lessonPerDay / 1.1))));  // divided by 7 and divided by 8.5
            gridViewTitles.setPadding(0, 0, 0, 0);
            gridViewTitles.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            gridViewTitles.setTextSize((float) (10 / (lessonPerDay * 0.125)));
            gridViewTitles.setTypeface(Typeface.DEFAULT_BOLD);

            /*courseImage.setLayoutParams(new GridView.LayoutParams( (deviceWidth / 7), (int) (deviceWidth / 8.5)));
            courseImage.setScaleType(ImageView.ScaleType.FIT_XY);
            courseImage.setPadding(0, 0, 0, 0);*/

        }
        else{
            /*if(position!=0&&position!=1&&position!=2&&position!=3&&position!=4&&position!=5&&position%6!=0){
                try{
                    courseImage=(ImageView) convertView;
                }
                catch (Exception e){
                    getView(position,null,parent);
                }
            }*/

            //gridViewTitles=(TextView) convertView;

            gridViewTitles = (TextView) convertView;
        }



        if(position==0){
            gridViewTitles.setTextColor(Color.parseColor("#0060ba"));
            gridViewTitles.setText(R.string.day_time);
            return gridViewTitles;
        }
        else if(position==1){
            gridViewTitles.setTextColor(Color.parseColor("#0060ba"));
            gridViewTitles.setText(R.string.monday);
            return gridViewTitles;
        }
        else if(position==2){
            gridViewTitles.setTextColor(Color.parseColor("#0060ba"));
            gridViewTitles.setText(R.string.tuesday);
            return gridViewTitles;
        }
        else if(position==3){
            gridViewTitles.setTextColor(Color.parseColor("#0060ba"));
            gridViewTitles.setText(R.string.wednesday);
            return gridViewTitles;
        }
        else if(position==4){
            gridViewTitles.setTextColor(Color.parseColor("#0060ba"));
            gridViewTitles.setText(R.string.thursday);
            return gridViewTitles;
        }
        else if(position==5){
            gridViewTitles.setTextColor(Color.parseColor("#0060ba"));
            gridViewTitles.setText(R.string.friday);
            return gridViewTitles;
        }
        else if((position)%6==0){
            gridViewTitles.setTextColor(Color.parseColor("#0060ba"));
            int nextHour=hour,nextMinute=minute;
            int extraHour;
            nextMinute=nextMinute+lessonLength;
            extraHour=nextMinute/60;
            nextHour=nextHour+extraHour;
            extraHour = hour / 25;
            hour = hour % 24;
            hour = hour + extraHour;
            nextMinute=nextMinute%60;

            if(hour==0){
                hourS="00";
            }
            else if(hour > 0 && hour < 10) {
                hourS="0".concat(Integer.toString(hour));
            }
            else{
                hourS=Integer.toString(hour);
            }
            if(minute==0){
                minuteS="00";
            }
            else if(minute>0&&minute<10){
                minuteS="0".concat(Integer.toString(minute));
            }
            else{
                minuteS=Integer.toString(minute);
            }
            if(nextHour==0){
                nextHourS="00";
            }
            else if(nextHour>0&&nextHour<10){
                nextHourS="0".concat(Integer.toString(nextHour));
            }
            else{
                nextHourS=Integer.toString(nextHour);
            }
            if(nextMinute==0){
                nextMinuteS="00";
            }
            else if(nextMinute>0&&nextMinute<10){
                nextMinuteS="0".concat(Integer.toString(nextMinute));
            }
            else{
                nextMinuteS=Integer.toString(nextMinute);
            }

            gridViewTitles.setText(new StringBuilder().append(hourS).append(".").append(minuteS).append("\n-\n")
                    .append(nextHourS).append(".").append(nextMinuteS));


            minute=nextMinute+breakTime;
            extraHour=minute/60;
            hour=nextHour+extraHour;
            extraHour = hour / 25;
            hour = hour % 24;
            hour = hour + extraHour;
            minute=minute%60;

            return gridViewTitles;
        }
        else{
            gridViewTitles.setTextColor(Color.WHITE);
            boolean isFull;
            for(int i=7;i<(6*(lessonPerDay+1));i++){
                if(i==position){
                    SharedPreferences sharedPreferences=context.getSharedPreferences("courseAtPosition",Context.MODE_PRIVATE);
                    isFull=sharedPreferences.getBoolean(Integer.toString(i),false);

                    Log.i("Index",Integer.toString(i));
                    Log.i("IsFull",Boolean.toString(isFull));
                    Log.i("Course Name",sharedPreferences.getString(Integer.toString(i).concat("_course_name"), ""));

                    if(isFull) {
                        gridViewTitles.setText(sharedPreferences.getString(Integer.toString(i).concat("_course_name"), ""));
                        final Vibrator vibrator=(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

                        GestureDetector.SimpleOnGestureListener gD = new GestureDetector.SimpleOnGestureListener(){
                            private boolean found = false;
                            private boolean isMove;
                            private int i, j = 1, row, col;
                            private int preferenceKey = position;
                            private String courseName = gridViewTitles.getText().toString();
                            private SharedPreferences sharedPreferences;
                            private SharedPreferences.Editor she;

                            @Override
                            public void onLongPress(MotionEvent e) {
                                super.onLongPress(e);
                                sharedPreferences = context.getSharedPreferences("courseAtPosition", Context.MODE_PRIVATE);
                                she = sharedPreferences.edit();
                                she.putBoolean(Integer.toString(position), false);
                                she.putString(Integer.toString(position).concat("_course_name"), "");
                                she.apply();
                                MainActivity.refreshGridView();

                                txtView.setText(courseName);
                                txtView.setX(e.getRawX() - deviceWidth / 10);
                                txtView.setY(e.getRawY() - deviceHeight / 15);
                                relativeLayout.addView(txtView);
                                longPressed = true;

                                txtView.setOnTouchListener(new View.OnTouchListener() {
                                    private SharedPreferences.Editor she;

                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        longPressed = false;

                                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                            isMove = false;

                                            return true;
                                        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                            isMove = true;

                                            v.setX((int) (event.getRawX() - deviceWidth / 10));
                                            v.setY((int) (event.getRawY() - deviceHeight / 15));

                                            for (i = 1; i <= lessonPerDay; i++) {
                                                for (j = 1; j <= 5; j++) {
                                                    if (txtView.getX() >= leftCoordinate * j && txtView.getX() <= leftCoordinate * (j + 1) && txtView.getY() >= topCoordinate * i - 30 && txtView.getY() < topCoordinate * (i + 1) - 30) {
                                                        v.setBackgroundResource(courseDrawable);
                                                        txtView.setTextColor(Color.parseColor("#FFFFFF"));
                                                        removeImage.setVisibility(View.GONE);
                                                        row = i;
                                                        col = j;
                                                        found = true;

                                                        break;
                                                    } else {
                                                        found = false;
                                                    }
                                                }
                                                if (found) {
                                                    break;
                                                }
                                            }

                                            if (!found) {
                                                v.setBackgroundResource(R.drawable.drag_drop_courses);
                                                removeImage.setVisibility(View.VISIBLE);
                                                txtView.setTextColor(Color.parseColor("#0060ba"));
                                            }

                                            if (Math.abs(event.getRawX() - deviceWidth / 2) <= deviceWidth / 3.6 && Math.abs(event.getRawY() - deviceHeight) <= deviceHeight / 3.2)
                                                removeImage.setImageResource(R.drawable.remove_hover);
                                            else
                                                removeImage.setImageResource(R.drawable.remove);

                                            return true;
                                        } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                            if (Math.abs(event.getRawX() - deviceWidth / 2) <= deviceWidth / 3.6 && Math.abs(event.getRawY() - deviceHeight) <= deviceHeight / 3.2) {
                                                removeImage.getLayoutParams().width = 75;
                                                removeImage.getLayoutParams().height = 75;
                                                relativeLayout.removeView(v);
                                                removeImage.setVisibility(View.GONE);
                                                vibrator.vibrate(100);

                                                sharedPreferences = context.getSharedPreferences("courseAtPosition", Context.MODE_PRIVATE);
                                                she = sharedPreferences.edit();
                                                she.putBoolean(Integer.toString(preferenceKey), false);
                                                she.putString(Integer.toString(preferenceKey).concat("_course_name"), "");
                                                she.apply();
                                            }

                                            if (found) {
                                                vibrator.vibrate(100);

                                                sharedPreferences = context.getSharedPreferences("courseAtPosition", Context.MODE_PRIVATE);
                                                she = sharedPreferences.edit();
                                                she.putBoolean(Integer.toString(preferenceKey), false);
                                                she.putString(Integer.toString(preferenceKey).concat("_course_name"), "");
                                                she.apply();

                                                preferenceKey = (row * 6) + col;

                                                sharedPreferences = context.getSharedPreferences("courseAtPosition", Context.MODE_PRIVATE);
                                                she = sharedPreferences.edit();
                                                she.putBoolean(Integer.toString(preferenceKey), true);
                                                she.putString(Integer.toString(preferenceKey).concat("_course_name"), courseName);
                                                she.apply();

                                                MainActivity.refreshGridView();

                                                relativeLayout.removeView(txtView);
                                            }

                                            return true;
                                        }

                                        return false;
                                    }
                                });

                            }

                            @Override
                            public boolean onSingleTapConfirmed(MotionEvent e) {
                                longPressed = false;

                                int row = preferenceKey / 6 - 1;

                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
                                    minute = minute % 60;

                                    minute = minute + breakTime;
                                    extrahour = minute / 60;
                                    hour = hour + extrahour;
                                    minute = minute % 60;
                                }

                                String newHour = Integer.toString(hour), newMinute = Integer.toString(minute);
                                if (hour == 0) {
                                    newHour = "00";
                                } else if (hour > 0 && hour <= 9) {
                                    newHour = "0".concat(Integer.toString(hour));
                                }
                                if (minute == 0) {
                                    newMinute = "00";
                                } else if (minute > 0 && minute <= 9) {
                                    newMinute = "0".concat(Integer.toString(minute));
                                }

                                AddCourseActivity.setCourse(preferenceKey, courseName, Integer.parseInt(newHour), Integer.parseInt(newMinute));
                                Intent startAddCourseIntent = new Intent(context, AddCourseActivity.class);
                                startAddCourseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(startAddCourseIntent);

                                return super.onSingleTapConfirmed(e);
                            }


                        };

                        final GestureDetector gestureDetector = new GestureDetector(context,gD);

                        gridViewTitles.setOnTouchListener(new View.OnTouchListener() {
                            String courseName;
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                InitializeTextView();
                                gestureDetector.onTouchEvent(event);

                                Log.i("Event ",Float.toString(event.getX()));

                                return true; //true
                            }
                        });
                    }
                    else
                        gridViewTitles.setText("");

                    break;
                }
            }
            gridViewTitles.setBackgroundResource(courseDrawable);
            return gridViewTitles;
        }
    }
}

