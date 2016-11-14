package com.hiking.trendview1109;

import android.graphics.Canvas;
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
    //在指定位置，以指定的颜色、对齐方式显示一串文本，返回显示高度
	public static void	DrawText(Canvas canvas, String text, int left, int right, int top, int bottom, Paint paint)
	{
		Paint.FontMetrics fm = paint.getFontMetrics();
		int y = top + (int)(bottom-top - fm.ascent)/2;
		if(bottom <= top)
		{
			int h = (int)(Math.ceil(fm.descent-fm.top) + 2);
			y = top + (int)(h - fm.ascent)/2;
		}

		Paint.Align  align = paint.getTextAlign();
		if(align == Paint.Align.LEFT)
		{
			canvas.drawText(text, left, y, paint);
		}
		else if(align == Paint.Align.RIGHT)
		{
			canvas.drawText(text, right, y, paint);
		}
		else if(align == Paint.Align.CENTER)
		{
			canvas.drawText(text, (left+right)>>1, y, paint);
		}
	}
}
