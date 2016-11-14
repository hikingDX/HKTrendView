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
    public static final String LongtoString(long text)
    {
        StringBuffer str = new StringBuffer();
        return str.append(text).toString();
    }
    //数字转换成指定小数点位数的字符串, data放大10000倍
    public	static String DataToString(long data, int dotlen)
    {
        StringBuffer str = new StringBuffer();
        DataToString(str, data, dotlen);
        return str.toString();
    }
    public	static void DataToString(StringBuffer str, long data, int dotlen)
    {
        if (data < 0)
        {
            str.append('-');
            DataToString(str, -data, dotlen);
            return;
        }

        if(dotlen <= 0)
        {
            str.append((data+5000)/10000);
            return;
        }

        str.append(data/10000);

        long times = getNumberPound(dotlen);
        if(times > 10000)
            times = 10000;
        long temp	= 10000 / times;

        long newdata = data % 10000;
        int len = str.length();
        str.append((newdata+temp/2)/temp + times);
        str.setCharAt(len, '.');
    }
    public	static final long NUMBER_POUND_long[] = {10, 100, 1000, 10000, 100000,
            1000000, 10000000, 100000000, 1000000000l, 10000000000l,
            100000000000l, 1000000000000l, 10000000000000l, 100000000000000l, 1000000000000000l,
            10000000000000000l, 100000000000000000l, 1000000000000000000l};

    public	static final long getNumberPound(int num)
    {
        if (num <= 0)
        {
            return 1;
        }
        else if (num <= NUMBER_POUND_long.length)
        {
            return NUMBER_POUND_long[num-1];
        }
        else
        {
            return NUMBER_POUND_long[NUMBER_POUND_long.length-1];
        }
    }
}
