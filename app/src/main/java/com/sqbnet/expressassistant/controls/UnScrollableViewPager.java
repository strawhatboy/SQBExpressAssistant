package com.sqbnet.expressassistant.controls;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Andy on 6/26/2015.
 */
public class UnScrollableViewPager extends ViewPager {

    private boolean isCanScroll = false;
    public UnScrollableViewPager(Context context) {
        super(context);
    }

    public UnScrollableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScanScroll(boolean isCanScroll){
        this.isCanScroll = isCanScroll;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isCanScroll) {
            return super.onTouchEvent(event);
        }

        return false;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isCanScroll) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }
}
