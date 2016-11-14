package com.hiking.trendview1109;

/**
 * Created by Administrator on 2016/11/14.
 */
final public class STD {
    public static final String getDateSringmmdd(int date)    //格式%02d%02d
    {
        StringBuffer str = new StringBuffer();
        str.append(date % 10000 + 10000);
        str.deleteCharAt(0);
        return str.toString();
    }

    public static final String getTimeSringhhmm(int time)    //格式%02d:%02d
    {
        StringBuffer str = new StringBuffer();
        str.append(time / 60 + 100);
        str.deleteCharAt(0);
        str.append(time % 60 + 100);
        str.setCharAt(2, ':');
        return str.toString();
    }
}
