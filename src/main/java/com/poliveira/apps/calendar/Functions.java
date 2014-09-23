package com.poliveira.apps.calendar;

import android.os.Build;
import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by poliveira on 23/09/2014.
 */
public class Functions
{
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * generate a unique id for a view. Has API level validation
     *
     * @return
     */
    public static int generateId()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            for (; ; )
            {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue))
                    return result;
            }
        else
        {
            return View.generateViewId();
        }
    }
}
