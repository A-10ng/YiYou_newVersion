package com.example.yiyou_newversion.fragment;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * author：LongSh1z
 * email：2674461089@qq.com
 * time：2019/07/18
 * desc:
 */
public class NoScrollViewPager extends ViewPager {
    private boolean noScroll = false;

    public NoScrollViewPager(Context context){
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    //设置是否能滑动换页
    public void setNoScroll(boolean isCanScroll){
        this.noScroll = isCanScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        return !noScroll && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){

        }
        return !noScroll && super.onTouchEvent(ev);
    }

    @Override
    public void setCurrentItem(int item,boolean smoothScroll) {
        super.setCurrentItem(item,false);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item,false);
    }
}
