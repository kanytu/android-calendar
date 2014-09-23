package com.poliveira.apps.calendar;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by poliveira on 29/07/2014.
 */
class DayMapper
{
    private int mDay;
    private int mMonth;
    private int mYear;
    private int mViewId;

    public DayMapper(int mDay, int mMonth, int mYear, int mViewId)
    {
        this.mDay = mDay;
        this.mMonth = mMonth;
        this.mYear = mYear;
        this.mViewId = mViewId;
    }

    public int getmDay()
    {
        return mDay;
    }

    public void setmDay(int mDay)
    {
        this.mDay = mDay;
    }

    public int getmMonth()
    {
        return mMonth;
    }

    public void setmMonth(int mMonth)
    {
        this.mMonth = mMonth;
    }

    public int getmYear()
    {
        return mYear;
    }

    public void setmYear(int mYear)
    {
        this.mYear = mYear;
    }

    public int getmViewId()
    {
        return mViewId;
    }

    public void setmViewId(int mViewId)
    {
        this.mViewId = mViewId;
    }

    public Calendar toCalendar()
    {
        return new GregorianCalendar(mYear, mMonth - 1, mDay);
    }
}
