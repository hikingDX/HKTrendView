package com.hiking.trendview1109;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2016/11/9.
 */
public final class ViewTools {
    public static int getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();

        return (int) (Math.ceil(fm.descent - fm.top) + 2);
    }

    //在指定位置，以指定的颜色、对齐方式显示一串文本，返回显示高度
    public static void DrawText(Canvas canvas, String text, int left, int right, int top, int bottom, Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        int y = top + (int) (bottom - top - fm.ascent) / 2;
        if (bottom <= top) {
            int h = (int) (Math.ceil(fm.descent - fm.top) + 2);
            y = top + (int) (h - fm.ascent) / 2;
        }

        Paint.Align align = paint.getTextAlign();
        if (align == Paint.Align.LEFT) {
            canvas.drawText(text, left, y, paint);
        } else if (align == Paint.Align.RIGHT) {
            canvas.drawText(text, right, y, paint);
        } else if (align == Paint.Align.CENTER) {
            canvas.drawText(text, (left + right) >> 1, y, paint);
        }
    }

    //在指定位置，以指定的对齐方式、指定小数位数，显示价格
    //flag: true--使用paint设置
    public static void DrawPrice(Canvas canvas, int x, int y, int price, int now, int yesterday, int dotlen, Paint paint, boolean flag) {
        DrawPrice(canvas, x, y, price, now, yesterday, dotlen, paint, flag, 0);
    }

    public static void DrawPrice(Canvas canvas, int x, int y, int price, int now, int yesterday, int dotlen, Paint paint, boolean flag, int xsws) {
        DrawPrice(canvas, x, y, 0, price, now, yesterday, dotlen, paint, flag, xsws);
    }

    public static void DrawPrice(Canvas canvas, int x, int top, int bottom, int price, int now, int yesterday, int dotlen, Paint paint, boolean
            flag, int xsws) {
        String str = getStringByPrice(price, now, dotlen, xsws);
        if (flag == false) {
            paint.setColor(getColor(price, yesterday));
            paint.setTextAlign(Paint.Align.RIGHT);
        }
        DrawText(canvas, str, 0, x, top, bottom, paint);
    }//根据小数点位数返回价格的字符串

    public static String getStringByPrice(int price, int now, int dotlen, int xsws) {
        if (price == 0) {
            return "----";
        }

        int maxdec = 4;
        if (price >= 100000000)    //5.1
        {
            maxdec = 1;
        } else if (price >= 10000000)    //4.2
        {
            maxdec = 2;
        } else if (price >= 1000000)    //3.3
        {
            maxdec = 3;
        }

        if (dotlen > maxdec) {
            dotlen = maxdec;
        }

        if (dotlen < 0) {
            dotlen = 2;
        } else if (dotlen == 0) {
            STD.LongtoString(price);
        }
        String temp = STD.DataToString(price, dotlen);

        temp = handlePoint(temp, xsws);

        return temp;
    }

    //显示颜色
    public	static int getColor(int data)
    {
        return getColor(data, 0);
    }
    public	static int getColor(int data, int base)
    {
        int color = COLOR.PRICE_EQUAL;
        if(data == 0 && base != 0)
        {
            color	= COLOR.DATA_NULL;
        }
        else if(data > base)
        {
            color	= COLOR.PRICE_UP;
        }
        else if (data < base)
        {
            color	= COLOR.PRICE_DOWN;
        }
        return color;
    }
    public	static int getColor(long data)
    {
        return getColor(data, 0);
    }

    public	static int getColor(long data, long base)
    {
        int color = COLOR.PRICE_EQUAL;
        if(data == 0 && base != 0)
        {
            color	= COLOR.DATA_NULL;
        }
        else if(data > base)
        {
            color	= COLOR.PRICE_UP;
        }
        else if (data < base)
        {
            color	= COLOR.PRICE_DOWN;
        }
        else
        {
            //			L.e("ViewTools", "data = " + data + ", base = " + base);
        }
        return color;
    }
    /**
     * 处理显示位数
     * @param str	所需处理字符串
     * @param xsws	显示位数
     * @return
     */
    private static String handlePoint(String str, int xsws) {

        if(xsws != 0){
            String temp[] = str.split("\\.");
            if(temp.length > 1){
                if(null != temp[1]){
                    String endStr = temp[1];
                    if(endStr.length() > xsws){
                        //						double strDouble = Double.parseDouble(str);

                        BigDecimal b = new BigDecimal(str);
                        b = b.setScale(xsws, BigDecimal.ROUND_HALF_UP);
                        return b.toString();
                    }
                }
            }
        }

        return str;
    }
    //在指定位置，显示涨跌幅
    //flag:是否显示百分号
    //isSign:是否显示符号，只有负数才显示符号
    public	static void	DrawZDF(Canvas canvas, int x, int y, int zd, int now, int yesterday, boolean flag, boolean isSign, Paint paint, boolean pFlag)
    {
        String str = getZDF_Ex(zd, yesterday, now, flag, isSign);
        if(pFlag == false)
        {
            paint.setColor(getColor(zd));
            paint.setTextAlign(Paint.Align.RIGHT);
        }
        DrawText(canvas, str, 0, x, y, 0, paint);
    }
    //由涨跌获取涨跌幅
    public	static int getZDF(int zd, int yesterday)
    {
        if (yesterday == 0)
        {
            return 0;
        }
        double add = (zd>0) ? 0.5 : (-0.5);
        return (int)(zd * 10000.0 / yesterday + add);
    }
    //由涨跌获取涨跌幅 字符串,固定2位小数
    //flag:是否显示百分号
    //isSign:是否显示符号，只有负数才显示符号
    public static String getZDF(int zd, int yesterday, int now, boolean flag, boolean isSign)
    {
        int zdf = getZDF(zd, yesterday);

        if(zdf == 0 && now == 0) {
            return "----";
        }
        //不显示符号
        if(isSign==false && zdf<0) {
            zdf = -zdf;
        }

        StringBuffer str = new StringBuffer();
        STD.DataToString(str, (long)zdf*100, 2);

        if (flag) {
            str.append("%");
        }
        return str.toString();
    }
    //由涨跌获取涨跌幅 字符串, 幅度大于10时显示1位小数
    //flag:是否显示百分号
    //isSign:是否显示符号，只有负数才显示符号
    public static String getZDF_Ex(int zd, int yesterday, int now, boolean flag, boolean isSign)
    {
        int zdf = getZDF(zd, yesterday);

        if(zdf == 0 && now == 0) {
            return "----";
        }
        //不显示符号
        if(isSign==false && zdf<0) {
            zdf = -zdf;
        }

        StringBuffer str = new StringBuffer();
        if((zdf>=0&&zdf<1000) || (zdf<0&&zdf>-1000))
            STD.DataToString(str, (long)zdf*100, 2);
        else
            STD.DataToString(str, (long)zdf*100, 1);

        if (flag) {
            str.append("%");
        }
        return str.toString();
    }
}
