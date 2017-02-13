package com.vf.volkswagan.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by tinfimate on 11-Feb-17.
 */

public class CustomVerticalViewPager extends ViewPager {
    private ViewPagerScroller viewPagerScroller;
    private int mScrollDuration = 800;

    public CustomVerticalViewPager(Context context) {
        super(context);
        init();
    }

    public CustomVerticalViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        setPageTransformer(true, new VerticalPageTransformer());
        setOverScrollMode(OVER_SCROLL_IF_CONTENT_SCROLLS);
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);
            viewPagerScroller = new ViewPagerScroller(getContext(), new DecelerateInterpolator());
            scroller.set(this, viewPagerScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void postInitViewPager() {
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);
            viewPagerScroller = new ViewPagerScroller(getContext(), new DecelerateInterpolator());
            scroller.set(this, viewPagerScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set smooth scroller duration in millisecs
     *
     * @param duration
     */
    public void setSmoothScrollDuration(int duration) {
        mScrollDuration = duration;
    }

    public class ViewPagerScroller extends Scroller {


        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }
    }

    private class VerticalPageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View view, float position) {

            if (position < -1) { // [-Infinity,-1)
                 view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                view.setAlpha(1);

                 view.setTranslationX(view.getWidth() * -position);

                 float yPosition = position * view.getHeight();
                view.setTranslationY(yPosition);

            } else {  
                view.setAlpha(0);
            }
        }
    }

    /**
     * Swaps the X and Y coordinates of touch event.
     */
    private MotionEvent swapXY(MotionEvent ev) {
        float width = getWidth();
        float height = getHeight();

        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;

        ev.setLocation(newX, newY);

        return ev;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
        swapXY(ev); // return touch coordinates to original reference frame for any child views
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapXY(ev));
    }
}
