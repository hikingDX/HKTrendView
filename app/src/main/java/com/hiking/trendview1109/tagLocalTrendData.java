package com.hiking.trendview1109;

/**
 * Created by Administrator on 2016/11/14.
 */
public class tagLocalTrendData {
    public int now;            //现价
    public int average;        //均价
    public long volume;            //成交量
    public long amount;            //成交金额
    public int lb;                //量比

    public long WBZL;            //委买总量，单位股
    public long WSZL;            //委卖总量，单位股
    public long ZLZJL;            //主力资金流，单位分

    public int LJZLDX;            //累计主力动向，放大100倍
    public int BUY_CASH_SPEED;    //买资金流速，放大100倍
    public int SELL_CASH_SPEED;//卖资金流速，放大100倍

    public short HLZ;//大盘红绿柱，INT16，存储与上一分钟之差

    public long jrccl;            //今日持仓量	单位股
    public long kc;                //开仓	单位股
    public long pc;                //平仓	单位股

    public tagLocalTrendData() {
    }

    public tagLocalTrendData(int now, int average, long volume) {
        this.now = now;
        this.average = average;
        this.volume = volume;
    }

    public tagLocalTrendData(int now, int average, long volume, long amount, int lb, long jrccl, long kc, long pc) {
        this.now = now;
        this.average = average;
        this.volume = volume;
        this.amount = amount;
        this.lb = lb;
        this.jrccl = jrccl;
        this.kc = kc;
        this.pc = pc;
    }

    public void Clear() {
        this.now = 0;
        this.average = 0;
        this.volume = 0;
        this.amount = 0;
        this.lb = 0;
        this.jrccl = 0;
        this.kc = 0;
        this.pc = 0;

        WBZL = WSZL = ZLZJL = 0;
        LJZLDX = BUY_CASH_SPEED = SELL_CASH_SPEED = 0;

        HLZ = 0;
    }

    public void Copy(tagLocalTrendData data) {
        if (data == null) {
            return;
        }
        this.now = data.now;
        this.average = data.average;
        this.volume = data.volume;
        this.amount = data.amount;
        this.lb = data.lb;
        this.jrccl = data.jrccl;
        this.kc = data.kc;
        this.pc = data.pc;

        this.WBZL = data.WBZL;
        this.WSZL = data.WSZL;
        this.ZLZJL = data.ZLZJL;
        this.LJZLDX = data.LJZLDX;
        this.BUY_CASH_SPEED = data.BUY_CASH_SPEED;
        this.SELL_CASH_SPEED = data.SELL_CASH_SPEED;
        this.HLZ = data.HLZ;

    }
}
