package com.hiking.trendview1109;

/**
 * Created by Administrator on 2016/11/14.
 */
public class tagLocalStockData {
    public static final int MAX_HIS_NUM = 4;
    private tagLocalTrendData[] trendData;        //走势数据
    private int trendDataNum;    //走势数量
    public tagHisTrendData[] hisTrendData;    //历史走势
    public int hisDays;

    public byte tradetimenum;    //交易段数,最多三段
    public int[] tradetime;        //交易时间 hour*60+minute[6]

    // 历史走势
    public int getHisTrendNum() {
        //return hisTrendData.size();
        return hisDays;
    }

    public int getTradeMinute() {
        int num = 0;
        if (tradetime[0] > tradetime[1])
            num += tradetime[1] + 24 * 60 - tradetime[0] + 1;
        else
            num += tradetime[1] - tradetime[0] + 1;

        if (tradetime[2] > tradetime[3])
            num += tradetime[3] + 24 * 60 - tradetime[2];
        else
            num += tradetime[3] - tradetime[2];

        if (tradetime[4] > tradetime[5])
            num += tradetime[5] + 24 * 60 - tradetime[4];
        else
            num += tradetime[5] - tradetime[4];

        return num;
    }

    /**
     * 获取对应时间的交易分钟数，返回值从1开始
     */
    public int getTimePoint(int hour, int minute) {
        return getTimePoint(hour * 60 + minute);
    }

    public int getTimePoint(int time) {
        int pos = 0;
        tagTimePointRet TPData = getTimePointBase(time);
        if (TPData.ret_val < 0) {
            TPData = getTimePointBase(time + 24 * 60);
            if (TPData.ret_val == 0) {
                pos = TPData.pos;
            }
        } else
            pos = TPData.pos;

        return pos;
    }

    class tagTimePointRet {
        public int ret_val = 0;
        public int pos = 0;

        public tagTimePointRet(int ret_val, int pos) {
            this.ret_val = ret_val;
            this.pos = pos;
        }
    }

    tagTimePointRet getTimePointBase(int time) {
        tagTimePointRet TPData = new tagTimePointRet(0, 0);

        if (tradetimenum > 0)    //计算第一段
        {
            if (time < tradetime[0])    //OPEN1
            {
                TPData.pos = 1;
                TPData.ret_val = -1;
                return TPData;
            } else if (time <= tradetime[1])    //CLOSE1
            {
                TPData.pos = time - tradetime[0] + 1;
                TPData.ret_val = 0;
                return TPData;
            }
        }

        int firstnum = tradetime[1] - tradetime[0] + 1;

        if (tradetimenum > 1)    //计算第二段
        {
            if (time <= tradetime[2])        //OPEN2
            {
                TPData.pos = firstnum;
                TPData.ret_val = -1;
                return TPData;
            } else if (time <= tradetime[3])    //CLOSE2
            {
                TPData.pos = firstnum + time - tradetime[2];
                TPData.ret_val = 0;
                return TPData;
            }
        } else {
            TPData.pos = firstnum;
            TPData.ret_val = -1;
            return TPData;
        }

        int secondnum = tradetime[3] - tradetime[2] + firstnum;

        if (tradetimenum > 2)    //计算第三段，最多三段
        {
            if (time <= tradetime[4])        //OPEN3
            {
                TPData.pos = secondnum;
                TPData.ret_val = -1;
                return TPData;
            } else if (time <= tradetime[5])    //CLOSE3
            {
                TPData.pos = secondnum + time - tradetime[4];
                TPData.ret_val = 0;
                return TPData;
            }
        } else {
            TPData.pos = secondnum;
            TPData.ret_val = -1;
            return TPData;
        }

        int thirdnum = tradetime[5] - tradetime[4] + secondnum;

        TPData.pos = thirdnum;
        TPData.ret_val = -1;
        return TPData;
    }
}
