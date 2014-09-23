package com.poliveira.apps.calendar;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by poliveira on 29/07/2014.
 */
public class CalendarView extends RelativeLayout implements Day.ICalendarDayCallbacks
{

    public interface ICalendarCallbacks
    {
        void onFinishInitialization();
    }

    private Calendar mFirstInterval = null;
    private List<DayMapper> mapperList;


    public CalendarView(Context context)
    {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void initialize(ICalendarCallbacks callbacks)
    {
        setBackgroundColor(getContext().getResources().getColor(android.R.color.white));
        new UiWorker(callbacks).execute();
    }

    private class UiWorker extends AsyncTask<Void, Object, Void>
    {

        private final ICalendarCallbacks mCallbacks;

        public UiWorker(ICalendarCallbacks callbacks)
        {
            mCallbacks = callbacks;
        }

        @Override
        protected Void doInBackground(Void... para)
        {
            mapperList = new ArrayList<DayMapper>();
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
            View mMonthView = null;
            for (int i = year; i < year + 2; i++)
            {
                int a = 1;
                if (year == i)
                    a = month;
                for (; a <= 12; a++)
                {

                    LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if (mMonthView != null)
                    {
                        int id = mMonthView.getId();
                        params.addRule(BELOW, id);
                    }

                    Calendar c = new GregorianCalendar(i, a - 1, 1);
                    mMonthView = new RelativeLayout(getContext());
                    mMonthView.setId(Functions.generateId());

                    LinearLayout weekHeader = new LinearLayout(getContext());
                    LinearLayout monthNameHeader = new LinearLayout(getContext());
                    LayoutParams par = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    monthNameHeader.setId(Functions.generateId());
                    par.addRule(BELOW, monthNameHeader.getId());

                    monthNameHeader.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    TextView monthName = new TextView(getContext());
                    monthName.setPadding(30, 30, 30, 30);
                    monthName.setTextColor(Color.WHITE);
                    monthName.setTextSize(20);
                    monthName.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    monthName.setGravity(Gravity.CENTER);
                    publishProgress(monthNameHeader, monthName);
                    publishProgress(mMonthView, monthNameHeader);

                    weekHeader.setLayoutParams(par);
                    weekHeader.setOrientation(LinearLayout.HORIZONTAL);
                    weekHeader.setBackgroundColor(Color.parseColor("#5EC6CF"));
                    monthNameHeader.setBackgroundColor(Color.parseColor("#5EC6CF"));
                    publishProgress(weekHeader, getTextViewForWeekday(2));
                    publishProgress(weekHeader, getTextViewForWeekday(3));
                    publishProgress(weekHeader, getTextViewForWeekday(4));
                    publishProgress(weekHeader, getTextViewForWeekday(5));
                    publishProgress(weekHeader, getTextViewForWeekday(6));
                    publishProgress(weekHeader, getTextViewForWeekday(7));
                    publishProgress(weekHeader, getTextViewForWeekday(1));
                    weekHeader.setId(Functions.generateId());
                    publishProgress(mMonthView, weekHeader);
                    TableLayout calendarDays = new TableLayout(getContext());
                    calendarDays.setStretchAllColumns(true);
                    LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.addRule(RelativeLayout.BELOW, weekHeader.getId());
                    monthName.setText(c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + i);
                    int first_week_day = c.get(Calendar.DAY_OF_WEEK) - 1;
                    if (first_week_day == 0)
                        first_week_day = 7;
                    TableRow week = new TableRow(getContext());
                    week.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    if (first_week_day != 1)
                        for (int z = 1; z < first_week_day; z++)
                        {
                            publishProgress(week, new Day(getContext(), 0, a, i, CalendarView.this, false));
                        }
                    for (int y = 1; y <= c.getActualMaximum(Calendar.DAY_OF_MONTH); y++)
                    {
                        if ((y + first_week_day - 2) % 7 == 0)
                        {
                            calendarDays.addView(week);
                            week = new TableRow(getContext());
                            week.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        }
                        Calendar tmp = new GregorianCalendar(i, a - 1, y);
                        Day d = new Day(getContext(), y, a, i, CalendarView.this, tmp.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || tmp.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
                        d.setId(Functions.generateId());
                        mapperList.add(new DayMapper(y, a, i, d.getId()));
                        tmp.clear();
                        publishProgress(week, d);
                    }
                    publishProgress(calendarDays, week);
                    calendarDays.setLayoutParams(layoutParams);
                    publishProgress(mMonthView, calendarDays);
                    mMonthView.setLayoutParams(params);
                    publishProgress(null, mMonthView);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values)
        {
            if (values[0] == null)
                addView((View) values[1]);
            else
                ((ViewGroup) values[0]).addView((View) values[1]);
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if (mCallbacks != null)
                mCallbacks.onFinishInitialization();
        }
    }


//    private View getMonthView(int year, int month) {
//        Calendar c = new GregorianCalendar(year, month - 1, 1);
//        RelativeLayout mMonthView = new RelativeLayout(getContext());
//        mMonthView.setId(View.generateViewId());
//
//        LinearLayout weekHeader = new LinearLayout(getContext());
//        LinearLayout monthNameHeader = new LinearLayout(getContext());
//        LayoutParams par = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        monthNameHeader.setId(View.generateViewId());
//        par.addRule(BELOW, monthNameHeader.getId());
//
//        monthNameHeader.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        TextView monthName = new TextView(getContext());
//        monthName.setPadding(30, 30, 30, 30);
//        monthName.setTextColor(Color.WHITE);
//        monthName.setTextSize(20);
//        monthName.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        monthName.setGravity(Gravity.CENTER);
//
//        monthNameHeader.addView(monthName);
//
//        mMonthView.addView(monthNameHeader);
//        weekHeader.setLayoutParams(par);
//        weekHeader.setOrientation(LinearLayout.HORIZONTAL);
//        weekHeader.setBackgroundColor(Color.parseColor("#5EC6CF"));
//        monthNameHeader.setBackgroundColor(Color.parseColor("#5EC6CF"));
//        weekHeader.addView(getTextViewForWeekday(2));
//        weekHeader.addView(getTextViewForWeekday(3));
//        weekHeader.addView(getTextViewForWeekday(4));
//        weekHeader.addView(getTextViewForWeekday(5));
//        weekHeader.addView(getTextViewForWeekday(6));
//        weekHeader.addView(getTextViewForWeekday(7));
//        weekHeader.addView(getTextViewForWeekday(1));
//        weekHeader.setId(View.generateViewId());
//
//        mMonthView.addView(weekHeader);
//
//        TableLayout calendarDays = new TableLayout(getContext());
//        calendarDays.setStretchAllColumns(true);
//        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        layoutParams.addRule(RelativeLayout.BELOW, weekHeader.getId());
//
//
//        monthName.setText(c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + year);
//        int first_week_day = c.get(Calendar.DAY_OF_WEEK) - 1;
//        if (first_week_day == 0)
//            first_week_day = 7;
//        TableRow week = new TableRow(getContext());
//        week.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        if (first_week_day != 1)
//            for (int i = 1; i < first_week_day; i++) {
//                week.addView(new Day(getContext(), 0, month, year, this,false));
//            }
//        for (int a = 1; a <= c.getActualMaximum(Calendar.DAY_OF_MONTH); a++) {
//            if ((a + first_week_day - 2) % 7 == 0) {
//                calendarDays.addView(week);
//                week = new TableRow(getContext());
//                week.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            }
//            Calendar tmp = new GregorianCalendar(year, month - 1, a);
//            Day d = new Day(getContext(), a, month, year, this, tmp.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || tmp.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
//            d.setId(View.generateViewId());
//            mapperList.add(new DayMapper(a, month, year, d.getId()));
//            tmp.clear();
//            week.addView(d);
//        }
//        calendarDays.addView(week);
//        mMonthView.addView(calendarDays, layoutParams);
//        return mMonthView;
//    }

    private TextView getTextViewForWeekday(int w)
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        TextView txtWeek = new TextView(getContext());
        txtWeek.setLayoutParams(params);
        txtWeek.setText(DateFormatSymbols.getInstance(Locale.getDefault()).getShortWeekdays()[w]);
        txtWeek.setTextColor(Color.WHITE);
        txtWeek.setPadding(10, 30, 10, 30);
        txtWeek.setGravity(Gravity.CENTER);
        return txtWeek;
    }

    public void drawDateSelection(Calendar beginning, Calendar end, int color)
    {

        for (DayMapper m : mapperList)
        {
            if (m.toCalendar().before(end) && m.toCalendar().after(beginning))
            {
                ((Day) findViewById(m.getmViewId())).setColor(color);
                ((Day) findViewById(m.getmViewId())).select();
            } else if (m.toCalendar().compareTo(beginning) == 0)
            {
                ((Day) findViewById(m.getmViewId())).setColor(color);
                ((Day) findViewById(m.getmViewId())).markAsAfternoon(color);
            } else if (m.toCalendar().compareTo(end) == 0)
            {
                ((Day) findViewById(m.getmViewId())).setColor(color);
                ((Day) findViewById(m.getmViewId())).markAsMorning(color);
            }
        }
    }

    public void drawDateSelection(Calendar beginning, Calendar end)
    {

        for (DayMapper m : mapperList)
            if (m.toCalendar().before(end) && m.toCalendar().after(beginning))
            {
                ((Day) findViewById(m.getmViewId())).select();
            } else if (m.toCalendar().compareTo(beginning) == 0)
                ((Day) findViewById(m.getmViewId())).markAsAfternoon();
            else if (m.toCalendar().compareTo(end) == 0)
                ((Day) findViewById(m.getmViewId())).markAsMorning();
    }

    @Override
    public void onSelected(int day, int month, int year, View v)
    {
        if (mFirstInterval == null)
            mFirstInterval = new GregorianCalendar(year, month, day);
        else
        {
            Calendar secondInterval = new GregorianCalendar(year, month, day);
            if (secondInterval.before(mFirstInterval))
            {
                for (DayMapper m : mapperList)
                    if (m.toCalendar().before(mFirstInterval) && m.toCalendar().after(secondInterval))
                        ((Day) findViewById(m.getmViewId())).select();
            } else
            {
                for (DayMapper m : mapperList)
                    if (m.toCalendar().after(mFirstInterval) && m.toCalendar().before(secondInterval))
                        ((Day) findViewById(m.getmViewId())).select();
            }
          /*  if(mFirstInterval.before(secondInterval))
            {
                mDateSelection.add(new long[]{mFirstInterval.getTimeInMillis(),secondInterval.getTimeInMillis()});
            }else{
                mDateSelection.add(new long[]{secondInterval.getTimeInMillis(),mFirstInterval.getTimeInMillis()});
            }*/
            mFirstInterval = null;


        }
    }

    @Override
    public void onStateChanged(boolean isSelected, View me)
    {

    }
}

