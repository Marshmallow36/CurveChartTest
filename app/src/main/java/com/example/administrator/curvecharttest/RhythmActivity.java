package com.example.administrator.curvecharttest;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RhythmActivity extends AppCompatActivity {

    private LinearLayout customCurveChart1;
    int mYear, mMonth, mDay;
    Button btn;
    TextView dateDisplay;
    final int DATE_DIALOG = 1;
    SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rhythm);
        customCurveChart1 = (LinearLayout) findViewById(R.id.customCurveChart1);
        btn = (Button) findViewById(R.id.change_date);
        dateDisplay = (TextView) findViewById(R.id.today_date);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });

        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        display();
        String birth = set_birthDay();
        initCurveChart1(birth);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    /**
     * 设置日期
     */
    public void display() {
        Calendar curDate = Calendar.getInstance();
        curDate.set(mYear, mMonth, mDay);
        String date = formatter.format(curDate.getTime());
        dateDisplay.setText(date);
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            display();
            String birth1 = set_birthDay();
            initCurveChart1(birth1);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private String set_birthDay (){
        String birthday = "1995-10-05";
        return birthday;
    }

    /**
     * 初始化曲线图数据
     */
    private void initCurveChart1(String birthday) {
        double[] physical = new double [31];
        double[] mood = new double [31];
        double[] brains = new double [31];
        String[] xLabel = new String[31];
        String[] yLabel = {"0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"};
        Calendar curDate = Calendar.getInstance();
        curDate.set(mYear, mMonth, mDay);
        String date = formatter.format(curDate.getTime());

        long howDays = 0;
        howDays = getHowDaysFromBirthday(birthday,date);
        //Log.v("tag",""+howDays);

        if (howDays <= 0) {
            return;
        }
        else {
            for (int i = -15; i<= 15; i++){
                Calendar calendar = Calendar.getInstance();
                calendar.set(mYear, mMonth, mDay);
                int a_physical = (int)(howDays + i) % 23;
                physical[i+15] = getY(a_physical,23);
                int a_mood = (int)((howDays + i) % 28);
                mood[i+15] = getY(a_mood,28);
                int a_brains = (int)((howDays + i) % 33);
                brains[i+15] = getY(a_brains,33);

                calendar.add(Calendar.DATE,+i);
                xLabel[i+15] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                calendar.clear();
            }

            TextView tvp = (TextView) findViewById(R.id.today_physical);
            tvp.setText(String.valueOf(savetwoY(physical[15])));
            TextView tvm = (TextView) findViewById(R.id.today_mood);
            tvm.setText(String.valueOf(savetwoY(mood[15])));
            TextView tvb = (TextView) findViewById(R.id.today_brains);
            tvb.setText(String.valueOf(savetwoY(brains[15])));


            List<double[]> data = new ArrayList<>();
            List<Integer> color = new ArrayList<>();
            data.add(brains);
            color.add(R.color.color13);
            data.add(mood);
            color.add(R.color.color14);
            data.add(physical);
            color.add(R.color.colorPrimary);
            customCurveChart1.removeAllViews();
            customCurveChart1.addView(new CustomCurveChart(this, xLabel, yLabel, data, color, false));
        }
    }
    private long getHowDaysFromBirthday(String from, String to) {

        if (from == null || to == null || to.equals("") || from.equals("")) {
            return -1;
        }
        try {
            Date date1 = formatter.parse(from);
            Date date2 = formatter.parse(to);
            long howDays = (date2.getTime()-date1.getTime())/(60*60*1000*24);
            return howDays;
        } catch (Exception e) {
            return 0;
        }
    }

    private  double getY (int phase, int cycle){
        double y = Math.sin(2 * Math.PI / cycle * (phase)) * 50 + 50;
        return y;
    }
    private double savetwoY (double f){
        BigDecimal b = new BigDecimal(f);
        //保留2位小数
        double y1 = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        return y1;
    }



}
