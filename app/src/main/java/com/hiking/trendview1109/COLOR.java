package com.hiking.trendview1109;

import android.graphics.Color;

/**
 * Created by Administrator on 2016/11/14.
 */
//颜色定义
public final class COLOR {

    public static final int
            PRICE_UP = 0xFFF44949,        //涨,#F44949
            PRICE_DOWN = 0xFF339900,        //跌,#339900
            PRICE_EQUAL = Color.WHITE,        //平
            KLINE_UP = 0xFFF44949,        //阳线
            KLINE_DOWN = Color.CYAN,        //阴线
            DATA_NULL = Color.WHITE,        //无数据

    COLOR_YELLOW = 0xFFEEEC81,        //黄色
            COLOR_BLUE = 0xFF38BBFF,        //蓝色
            COLOR_VOL = COLOR_YELLOW,        //量
            COLOR_AMT = COLOR_YELLOW,    //额
            COLOR_BOLL = Color.CYAN,        //BOLL线

    COLOR_TREND = Color.WHITE,                //走势线颜色
            COLOR_AVG = Color.rgb(255, 166, 0),    //均线颜色
            COLOR_TIME = Color.rgb(255, 126, 0),    //时间框背景色
            COLOR_LB = Color.rgb(255, 200, 0),    //量比
            COLOR_CCL = Color.rgb(255, 200, 0),    //持仓量

    COLOR_INFOBG = Color.argb(200, 195, 62, 88), //信息栏背景色 #1C3E58

    COLOR_TECH0 = Color.WHITE,                //白
            COLOR_TECH1 = COLOR_YELLOW,            //黄
            COLOR_TECH2 = Color.MAGENTA,            //紫

    COLOR_SHADE = Color.rgb(16, 38, 55),    //走势阴影：#102637
            COLOR_END = Color.BLACK,                //：#030e13

    RADAR_DATE = Color.rgb(232, 117, 2);    //雷达 日期颜色#e87502
}
