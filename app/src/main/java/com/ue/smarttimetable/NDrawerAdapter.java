package com.ue.smarttimetable;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Pc on 25.02.2016.
 */
public class NDrawerAdapter extends ArrayAdapter<String> {
    private Context context;
    private int pos=1;
    private int type;

    public NDrawerAdapter(Context context, int resource, ArrayList<String> object) {
        super(context, resource, object);
        this.context=context;

        for(int i=0;i<object.size();i++){
            if(object.get(i).equals("Refresh to Load\nDepartment Courses")){
                pos = 2;
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        //Log.i("Position", Integer.toString(position));

        if(pos==1){
            if(position>0){
                type = 1;
            }
            else
                type = 0;
        }
        else{
            if(position>1){
                type = 1;
            }
            else
                type = 0;
        }

        return type;
    }

    public NDrawerAdapter(Context context, int resource) {
        super(context, resource);
        this.context=context;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final String courseName=getItem(position);

        LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            convertView=layoutInflater.inflate(R.layout.drawer_list_item,parent,false);
        }

        DisplayMetrics displayMetrics=context.getResources().getDisplayMetrics();
        int deviceWidth=displayMetrics.widthPixels;

        final TextView courseTextView =(TextView) convertView.findViewById(R.id.nDrawerCourseNames);
        courseTextView.setText(courseName);
        courseTextView.setTextColor(Color.parseColor("#0060ba"));
        courseTextView.setBackgroundResource(R.drawable.drag_drop_courses);

        if (type==1) {
            TextView courseDeleteTextView = (TextView) convertView.findViewById(R.id.nDrawerDeleteCourseName);
            courseDeleteTextView.setBackgroundResource(R.drawable.drag_drop_courses);
            courseDeleteTextView.setText("-");
            courseDeleteTextView.setTextColor(Color.RED);
            courseDeleteTextView.setTextSize(20);

            courseDeleteTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("DELETE POSITION",Integer.toString(position));
                    //Toast.makeText(context, Integer.toString(position - pos), Toast.LENGTH_LONG).show();
                    MainActivity.deleteFromNavigationDrawer(position, pos);
                }
            });
        }

        /*final View finalConvertView = convertView;
        courseTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    RelativeLayout relativeLayout=(RelativeLayout) finalConvertView.findViewById(R.id.relativeLayout);
                    TextView txtView = new TextView(getContext());
                    txtView.setText(courseName);
                    txtView.setBackgroundResource(R.drawable.drag_drop_courses);
                    txtView.setX(courseTextView.getX());
                    txtView.setY(courseTextView.getY());
                    relativeLayout.addView(txtView);
                    return true;
                }
                else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    txtView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                return true;
                            }
                            else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                txtView.setX(event.getRawX());
                                txtView.setY(event.getRawY());
                                return true;
                            }
                            else if(event.getAction()==MotionEvent.ACTION_UP){
                                RelativeLayout relativeLayout=(RelativeLayout) finalConvertView.findViewById(R.id.relativeLayout);
                                relativeLayout.removeView(v);
                                return true;
                            }

                            return false;
                        }
                    });

                    return true;
                }
                else if(event.getAction()==MotionEvent.ACTION_UP){
                    return true;
                }

                return false;
            }
        });*/

        return convertView;
    }
}
