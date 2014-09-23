package com.poliveira.apps.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by poliveira on 29/07/2014.
 */
public class Day extends TextView implements View.OnClickListener
{
    private static final String WEEKEND_COLOR = "#fafafa";
    private static final int BOOKING_DAY_COLOR = 0Xffe9e7e8;
    private Paint mPaint;
    private int mMorningColor;
    private int mAfternoonColor;
    private boolean hasMorning;
    private boolean hasAfternoon;
    private int mColor;
    private Point point1_draw;
    private Point point2_draw;
    private Point point3_draw;
    private Path mMorningPath;
    private Path mAfternoonPath;
    private Path mFullDayPath;

    public void select()
    {
        //isSelected = !isSelected;
        isSelected = true;
        if (isWeekend)
            setBackgroundColor(Color.parseColor(WEEKEND_COLOR));
        else
            setBackgroundColor(getContext().getResources().getColor(android.R.color.white));

        if (mCallbacks != null)
            mCallbacks.onStateChanged(isSelected, this);
        invalidate();
    }

    public void markAsMorning(int color)
    {
        hasMorning = true;
        mMorningColor = color;
        invalidate();
    }

    public void markAsAfternoon(int color)
    {
        hasAfternoon = true;
        mAfternoonColor = color;
        invalidate();
    }

    public void markAsMorning()
    {
        hasMorning = true;
        invalidate();
    }

    public void markAsAfternoon()
    {
        hasAfternoon = true;
        invalidate();
    }

    public boolean isWeekend()
    {
        return isWeekend;
    }

    public void setWeekend(boolean isWeekend)
    {
        this.isWeekend = isWeekend;
    }

    public void setColor(int _color)
    {
        mColor = _color;
    }

    public interface ICalendarDayCallbacks
    {
        void onSelected(int day, int month, int year, View v);

        void onStateChanged(boolean isSelected, View me);
    }

    private int mDay;
    private int mYear;
    private int mMonth;
    private ICalendarDayCallbacks mCallbacks;
    private boolean isSelected;
    private boolean isWeekend;


    public Day(Context context, int day, int month, int year, ICalendarDayCallbacks callbacks, boolean _isWeekend)
    {
        super(context);
        mYear = year;
        mDay = day;
        mMonth = month;
        setText((day > 0) ? day + "" : "");
        mCallbacks = callbacks;
        isWeekend = _isWeekend;
        setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                return true;
            }
        });
        init();
    }

    public Day(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public Day(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    {
        setOnClickListener(this);
        if (Calendar.getInstance().get(Calendar.YEAR) == mYear && Calendar.getInstance().get(Calendar.MONTH) == mMonth - 1 && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == mDay)
            setTextColor(Color.parseColor("#ff444444"));//today color
        else
            setTextColor(Color.BLACK);
        setPadding(30, 30, 30, 30);
        setGravity(Gravity.CENTER);
        if (isWeekend)
            setBackgroundColor(Color.parseColor(WEEKEND_COLOR));
        else
            setBackgroundColor(getContext().getResources().getColor(android.R.color.white));

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);
        point1_draw = new Point(0, 0);
        point2_draw = new Point(0, 0);
        point3_draw = new Point(0, 0);
        mMorningPath = new Path();
        mAfternoonPath = new Path();
        mFullDayPath = new Path();
    }


    @Override
    protected void onDraw(Canvas canvas)
    {

        point1_draw.set(0, 0);
        point2_draw.set(0, getHeight());
        point3_draw.set(getWidth(), 0);
        mMorningPath.setFillType(Path.FillType.EVEN_ODD);
        mMorningPath.moveTo(point1_draw.x, point1_draw.y);
        mMorningPath.lineTo(point2_draw.x, point2_draw.y);
        mMorningPath.lineTo(point3_draw.x, point3_draw.y);
        mMorningPath.lineTo(point1_draw.x, point1_draw.y);
        mMorningPath.close();
        point1_draw.set(getWidth(), getHeight());
        point2_draw.set(0, getHeight());
        point3_draw.set(getWidth(), 0);
        mAfternoonPath.setFillType(Path.FillType.EVEN_ODD);
        mAfternoonPath.moveTo(point1_draw.x, point1_draw.y);
        mAfternoonPath.lineTo(point2_draw.x, point2_draw.y);
        mAfternoonPath.lineTo(point3_draw.x, point3_draw.y);
        mAfternoonPath.lineTo(point1_draw.x, point1_draw.y);
        mAfternoonPath.close();
        point1_draw.set(getWidth(), getHeight());
        point2_draw.set(0, getHeight());
        point3_draw.set(getWidth(), 0);
        mFullDayPath.setFillType(Path.FillType.EVEN_ODD);
        mFullDayPath.moveTo(point1_draw.x, point1_draw.y);
        mFullDayPath.lineTo(point2_draw.x, point2_draw.y);
        mFullDayPath.lineTo(0, 0);
        mFullDayPath.lineTo(point3_draw.x, point3_draw.y);
        mFullDayPath.lineTo(point1_draw.x, point1_draw.y);
        mFullDayPath.close();
        if (mColor != 0)
            mPaint.setColor(mColor);
        else
            mPaint.setColor(BOOKING_DAY_COLOR);
        if (isSelected)
        {
            canvas.drawPath(mFullDayPath, mPaint);
        } else
        {
            if (hasAfternoon)
            {
                if (mAfternoonColor != 0)
                    mPaint.setColor(mAfternoonColor);
                canvas.drawPath(mAfternoonPath, mPaint);
            }
            if (hasMorning)
            {
                if (mMorningColor != 0)
                    mPaint.setColor(mMorningColor);
                canvas.drawPath(mMorningPath, mPaint);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public void onClick(View view)
    {

        if (mDay != 0)
        {
            if (mCallbacks != null)
            {
                mCallbacks.onSelected(mDay, mMonth, mYear, view);
                mCallbacks.onStateChanged(isSelected, view);
            }
            select();
        }
    }

    public ICalendarDayCallbacks getmCallbacks()
    {
        return mCallbacks;
    }

    public void setmCallbacks(ICalendarDayCallbacks mCallbacks)
    {
        this.mCallbacks = mCallbacks;
    }

}
