package com.hiking.trendview1109;

import android.graphics.Paint;

/**
 * Created by Administrator on 2016/11/9.
 */
public final class ViewTools {
    public static int getFontHeight(float fontSize){
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();

        return (int) (Math.ceil(fm.descent-fm.top)+2);
    }
}
