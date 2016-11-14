package com.hiking.trendview1109;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/11/9.
 */
public class TrendView extends FrameLayout {
    private final static String TAG = "TrendView";
    private boolean mPopinfoFlag;
    private int mTotalNum;            //总数 包括历史走势

    private int mTopHeight = 36;
    private int mTechLineH = 24;    //指标信息行高度
    private int mTechBtnW = 23;
    private int mTechBtnH = 20;

    private float mFontSize;//字体大小
    private int mFontH = 0;//字体高度
    private TrendLineView mTrendLineView;
    private Context mContext;

    /***
     * 数据
     **/
    private tagLocalStockData mStockData;

    public TrendView(Context context) {
        this(context, null);
    }

    public TrendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        mFontSize = getResources().getDimension(R.dimen.font_small);
        mFontH = ViewTools.getFontHeight(mFontSize);
        //创建走势图
        mTrendLineView = new TrendLineView(mContext);
        FrameLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        addView(mTrendLineView, lp);
    }

    class TrendLineView extends View {
        private Paint linePaint = new Paint();//画线时用的画笔
        private Paint mPaint;//画视图的画笔
        private Rect mClientRect;//窗口矩形
        private PathEffect mEffects;//路径效果
        /**
         * 位置相关
         **/
        private int mLeft = 0;
        private int mRight = 0;
        private int mLineLeft = 0;
        private int mLineRight = 0;
        private int mTlineTopY = 0;        //走势Y轴上坐标
        private int mTlineMidY = 0;        //走势Y中轴坐标
        private int mTlineBottomY = 0;    //走势Y轴下坐标
        private int mTechTopY = 0;          //指示框顶部Y
        private int mTechBottomY = 0;       //指示框底部Y
        private double mLineSpace = 0.0;    //走势Y间距

        private double mXScales = 0.0;        //横轴单根走势占用宽度
        private double mYScales = 0.0;        //纵轴价格比例

        private int mPriceOffset;        //坐标偏移价格

        /**
         * 颜色相关
         **/
        private int mFrameColor;        //边框颜色
        private int mTLineColor;        //走势线颜色
        private int mAvgColor;            //均线颜色
        private int mVolLineColor;        //成交量线 颜色
        private int mBgHistColor;        //历史走势背景色
        private int mLBColor;            //量比颜色

        private int mMinutes;            //交易分钟数
        private int mTrendDays = 1;            //走势线天数

        /**
         * 构造方法
         *
         * @param context
         */
        public TrendLineView(Context context) {
            super(context);
            initView();
        }

        /**
         * 初始化视图
         */
        private void initView() {
            mClientRect = new Rect();
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mEffects = new CornerPathEffect(3);

            initColor();
        }

        /**
         * 初始化颜色
         */
        private void initColor() {
            //颜色
            mFrameColor = getResources().getColor(R.color.bg_frame);
            mTLineColor = getResources().getColor(R.color.trend);
            mAvgColor = getResources().getColor(R.color.avgline);
            mVolLineColor = getResources().getColor(R.color.volline);
            mBgHistColor = getResources().getColor(R.color.bg_histrend);
            mLBColor = getResources().getColor(R.color.lbline);
        }

        /**
         * 程序先走onLayout函数,再走onDraw函数
         */
        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            //当位置发生改变时 修改视图的位置
            if (changed) {
                Log.e(TAG, "onlayout" + right);
                mClientRect.set(left, top, right, bottom);
                drawInit();
                dataInit();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Log.e(TAG, "onDraw");
            drawNow(canvas);
        }

        /**
         * 绘制当前走势图
         *
         * @param canvas
         */
        private void drawNow(Canvas canvas) {
            //绘制背景
            drawBackground(canvas);
            //绘制走势图
            drawTrendLine(canvas);
        }

        private void drawLine(Canvas canvas) {
            if (mTotalNum <= 0)
                return;

            Path path_now = new Path();
            Path path_avg = new Path();
            double midY = mTlineMidY;//中线的位置
            double y = 0;
            double last = mLineLeft + 1;
            double x = last;
            float y_now = 0, y_avg = 0;
            boolean bfirst = true;
            float y_now_last = 0, y_avg_last = 0;
            if (mTrendDays > 1) {    // 历史走势
                for (int i = mStockData.getHisTrendNum() - 1; i >= 0; i--) {
                    last = mLineLeft + 1 + mXScales * mMinutes * (mStockData.getHisTrendNum() - 1 - i);
                    tagHisTrendData tHisTrend = mStockData.hisTrendData[i];
                    for (int j = 0; j < mMinutes; j++) {
                        x = last;
                        if (tHisTrend.getTrendNum() == 0) {
                            if (y_now_last == 0 || tHisTrend.date == 0) {
                                bfirst = true;
                                break;
                            }
                            y_now = y_now_last;
                            y_avg = y_avg_last;
                        } else {
                            tagLocalTrendData trendData = tHisTrend.getTrendData(j);
                            if (trendData == null)
                                continue;
                            y_now = (float) (mTlineMidY - (trendData.now - mStockData.yesterday)
                                    * mYScales);
                            y_avg = (float) (mTlineMidY - (trendData.average - mStockData.yesterday)
                                    * mYScales);
                            //红绿柱
                            if (trendData.HLZ != 0) {
                                int value = (int) ((long) (trendData.HLZ * (tHisTrend.high - tHisTrend.low) + 75 / 2) / 75);
                                y = (float) (mTlineMidY - value * mYScales);
                                linePaint.setColor(trendData.HLZ > 0 ? COLOR.PRICE_UP : COLOR.PRICE_DOWN);
                                canvas.drawLine((int) x, (int) midY, (int) x, (int) y, linePaint);
                            }
                        }

                        if (bfirst == true) {
                            bfirst = false;
                            path_now.moveTo((float) x, y_now);
                            path_avg.moveTo((float) x, y_avg);
                        } else {
                            x += mXScales;
                            path_now.lineTo((float) x, y_now);
                            path_avg.lineTo((float) x, y_avg);
                        }
                        last = x;
                    }
                    y_now_last = y_now;
                    y_avg_last = y_avg;
                }
            }

            last = mLineLeft + 1 + mXScales * mMinutes * (mTrendDays - 1);
            for (int i = 0; i < mDataNum; i++) {
                x = last;

                tagLocalTrendData TrendData = mStockData.getTrendData(i);
                if (TrendData == null)
                    continue;

                y_now = (float) (mTlineMidY - (TrendData.now - mStockData.yesterday)
                        * mYScales);
                y_avg = (float) (mTlineMidY - (TrendData.average - mStockData.yesterday)
                        * mYScales);

                //红绿柱
                if (TrendData.HLZ != 0) {
                    int value = (int) ((long) (TrendData.HLZ * (mStockData.high - mStockData.low) + 75 / 2) / 75);
                    y = (float) (mTlineMidY - value * mYScales);
                    linePaint.setColor(TrendData.HLZ > 0 ? COLOR.PRICE_UP : COLOR.PRICE_DOWN);
                    canvas.drawLine((int) x, (int) midY, (int) x, (int) y, linePaint);
                }

                if (bfirst == true) {
                    bfirst = false;
                    path_now.moveTo((float) x, y_now);
                    path_avg.moveTo((float) x, y_avg);
                } else {
                    x += mXScales;
                    path_now.lineTo((float) x, y_now);
                    path_avg.lineTo((float) x, y_avg);
                }

                last = x;

            }


            Paint linePaint = new Paint();
            linePaint.setAntiAlias(true);
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setPathEffect(mEffects);
            linePaint.setStrokeWidth(1);
            linePaint.setColor(mAvgColor);
            if (!mStockData.IsWaiHui() && !mStockData.IsQH_GuoWai() && !mStockData.isQQZS() && !mStockData.isUS())
                canvas.drawPath(path_avg, linePaint);

            linePaint.setColor(mTLineColor);
            canvas.drawPath(path_now, linePaint);
        }

        /**
         * 绘制标尺
         *
         * @param canvas
         */
        private void drawRule(Canvas canvas) {
            //显示标尺
            if (mPopinfoFlag == true) {
                int indexX = (int) (mLineLeft + 1 + mXScales * m_iIndex);
                linePaint.setStrokeWidth(1);
                linePaint.setColor(Color.GRAY);
                PathEffect effect = new DashPathEffect(new float[]{3, 2}, 0);
                linePaint.setPathEffect(effect);
                Path path = new Path();
                path.moveTo(indexX, mTlineTopY);
                path.lineTo(indexX, mTlineBottomY);
                path.moveTo(indexX, mTechTopY);
                path.lineTo(indexX, mTechBottomY);
                canvas.drawPath(path, linePaint);
                //
                tagLocalTrendData tData = getTrendData(m_iIndex);
                if (tData == null)
                    return;
                double indexY = (double) (mTlineMidY - (tData.now - mStockData.yesterday) * mYScales);

                linePaint.setColor(Color.WHITE);
                linePaint.setStrokeWidth(5);
                linePaint.setPathEffect(null);
                canvas.drawPoint(indexX, (int) indexY, linePaint);
            }
        }

        /**
         * 绘制涨跌幅和价格
         *
         * @param canvas
         */
        private void drawPriceAndZDF(Canvas canvas) {
            //Y坐标
            mPaint.setTextSize(mFontSize);
            mPaint.setTextAlign(Paint.Align.RIGHT);
            mPaint.setColor(Color.WHITE);
            int tempY = mTlineMidY - mFontH / 3;
            ViewTools.DrawPrice(canvas, mLineLeft - 2, tempY, mStockData.yesterday, 1, mStockData.yesterday, mStockData.pricedot, mPaint, false,
                    mStockData.xsws);
            ViewTools.DrawZDF(canvas, mRight - 2, tempY, 0, 1, mStockData.yesterday, true, true, mPaint, false);
            //Y1
            tempY = (int) (mTlineMidY - mFontH / 3 - mLineSpace + 0.5);
            ViewTools.DrawPrice(canvas, mLineLeft - 2, tempY, mStockData.yesterday + mPriceOffset, 1, mStockData.yesterday, mStockData.pricedot,
                    mPaint, false, mStockData.xsws);
            ViewTools.DrawZDF(canvas, mRight - 2, tempY, mPriceOffset, 1, mStockData.yesterday, true, false, mPaint, false);
            //Y2
            tempY = mTlineTopY;
            ViewTools.DrawPrice(canvas, mLineLeft - 2, tempY, mStockData.yesterday + mPriceOffset * 2, 1, mStockData.yesterday, mStockData
                    .pricedot, mPaint, false, mStockData.xsws);
            ViewTools.DrawZDF(canvas, mRight - 2, tempY, mPriceOffset * 2, 1, mStockData.yesterday, true, false, mPaint, false);
            //-Y1
            tempY = (int) (mTlineMidY - mFontH / 3 + mLineSpace + 0.5);
            int price = (mStockData.yesterday > mPriceOffset) ? (mStockData.yesterday - mPriceOffset) : 0;
            int zdf = (mStockData.yesterday > mPriceOffset) ? (-mPriceOffset) : 0;
            ViewTools.DrawPrice(canvas, mLineLeft - 2, tempY, price, 1, mStockData.yesterday, mStockData.pricedot, mPaint, false, mStockData.xsws);
            ViewTools.DrawZDF(canvas, mRight - 2, tempY, zdf, 1, mStockData.yesterday, true, false, mPaint, false);
            //-Y2
            tempY = mTlineBottomY - mFontH / 3;
            price = (mStockData.yesterday > mPriceOffset * 2) ? (mStockData.yesterday - mPriceOffset * 2) : 0;
            zdf = (mStockData.yesterday > mPriceOffset * 2) ? (-mPriceOffset * 2) : 0;
            ViewTools.DrawPrice(canvas, mLineLeft - 2, tempY, price, 1, mStockData.yesterday, mStockData.pricedot, mPaint, false, mStockData.xsws);
            ViewTools.DrawZDF(canvas, mRight - 2, tempY, zdf, 1, mStockData.yesterday, true, false, mPaint, false);
        }

        private void drawTrendTime(Canvas canvas) {
            //时间
            mPaint.setTextSize(mFontSize);
            double width = 0.0;
            if (mTrendDays > 1) {
                mPaint.setColor(Color.GRAY);
                mPaint.setTextAlign(Paint.Align.LEFT);
                width = (double) (mLineRight - mLineLeft) / mTrendDays;
                // 历史日期显示
                for (int i = 0; i < mStockData.getHisTrendNum(); i++) {
                    int tempX = mLineLeft + (int) (width * i);
                    int date = mStockData.hisTrendData[mStockData.getHisTrendNum() - 1 - i].date;
                    String dateStr = STD.getDateSringmmdd(date);
                    ViewTools.DrawText(canvas, dateStr, tempX, mRight, mTechBottomY, 0, mPaint);
                }
            }

            mPaint.setColor(Color.WHITE);
            int tradenum = mStockData.getTradeMinute();
            if (tradenum > 1) {
                String temp = STD.getTimeSringhhmm(mStockData.getMinuteFromPoint_New(0));
                //开盘时间
                if (mStockData.isUS()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    try {
                        Date date = sdf.parse(temp);
                        date.setHours(date.getHours() + 12);
                        temp = temp + "(北京" + sdf.format(date) + ")";
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                mPaint.setTextAlign(Paint.Align.LEFT);
                ViewTools.DrawText(canvas, temp, (int) (mLineLeft + width * (mTrendDays - 1)), mRight, mTechBottomY, 0, mPaint);
                if (mStockData.tradetimenum > 1) {
                    //2段
                    mPaint.setTextAlign(Paint.Align.CENTER);
                    temp = STD.getTimeSringhhmm(mStockData.tradetime[1]);
                    float t_width = mPaint.measureText(temp);
                    float left = mLineLeft + (mLineRight - mLineLeft) * mStockData.getTimePoint(mStockData.tradetime[1]) / tradenum - t_width / 2;
                    ViewTools.DrawText(canvas, temp, (int) left, (int) (left + t_width), mTechBottomY, 0, mPaint);

                    //3段
                    if (mStockData.tradetimenum == 3) {
                        L.d("TAG", " ---- 6666");
                        mPaint.setTextAlign(Paint.Align.CENTER);
                        temp = STD.getTimeSringhhmm(mStockData.tradetime[3]);
                        t_width = mPaint.measureText(temp);
                        left = mLineLeft + (mLineRight - mLineLeft) * mStockData.getTimePoint(mStockData.tradetime[3]) / tradenum - t_width / 2;
                        ViewTools.DrawText(canvas, temp, (int) left, (int) (left + t_width), mTechBottomY, 0, mPaint);
                    }
                }
                mPaint.setTextAlign(Paint.Align.RIGHT);
                temp = STD.getTimeSringhhmm(mStockData.getMinuteFromPoint_New(tradenum));
                ViewTools.DrawText(canvas, temp, mLineLeft, mLineRight, mTechBottomY, 0, mPaint);
            } else {
                /**
                 * 当日只显示时间段
                 */
                if (mTrendDays > 1) {
                    mPaint.setTextAlign(Paint.Align.LEFT);
                    ViewTools.DrawText(canvas, "09:30", (int) (mLineLeft + width * (mTrendDays - 1)), mRight, mTechBottomY, 0, mPaint);
                } else {
                    mPaint.setTextAlign(Paint.Align.RIGHT);
                    ViewTools.DrawText(canvas, "09:30", mLeft, mLineLeft, mTechBottomY, 0, mPaint);
                    mPaint.setTextAlign(Paint.Align.CENTER);
                    ViewTools.DrawText(canvas, "11:30", mLineLeft, mLineRight, mTechBottomY, 0, mPaint);
                }
                mPaint.setTextAlign(Paint.Align.LEFT);
                ViewTools.DrawText(canvas, "15:00", mLineRight, mRight, mTechBottomY, 0, mPaint);
            }
        }

        /**
         * 绘制走势图
         *
         * @param canvas
         */
        private void drawTrendLine(Canvas canvas) {
            drawTrendTime(canvas);
            drawPriceAndZDF(canvas);
            drawLine(canvas);
            drawRule(canvas);
        }

        /**
         * 绘制背景
         *
         * @param canvas
         */
        private void drawBackground(Canvas canvas) {
            Paint linePaint = new Paint();
            linePaint.setAntiAlias(false);
            linePaint.setPathEffect(null);
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setColor(mFrameColor);
            //分割横线
            canvas.drawLine(mClientRect.left + 2, mTlineTopY, mClientRect.right - 2, mTlineTopY, linePaint);
            canvas.drawLine(mLineLeft, mTlineMidY, mLineRight, mTlineMidY, linePaint);
            canvas.drawLine(mLineLeft, mTlineBottomY, mLineRight, mTlineBottomY, linePaint);
            canvas.drawLine(mLineLeft, mTechTopY, mLineRight, mTechTopY, linePaint);
            canvas.drawLine(mLineLeft, mTechBottomY, mLineRight, mTechBottomY, linePaint);

            //分隔竖线
            canvas.drawLine(mLineLeft, mTlineTopY, mLineLeft, mTechBottomY, linePaint);
            canvas.drawLine(mLineRight, mTlineTopY, mLineRight, mTechBottomY + 1, linePaint);
            //虚线
            PathEffect effect = new DashPathEffect(new float[]{3, 2}, 0);
            linePaint.setPathEffect(effect);
            Path path = new Path();
            int tempY = (int) (mTlineMidY - mLineSpace + 0.5);
            path.moveTo(mLineLeft, tempY);
            path.lineTo(mLineRight, tempY);
            tempY = (int) (mTlineMidY + mLineSpace + 0.5);
            path.moveTo(mLineLeft, tempY);
            path.lineTo(mLineRight, tempY);
            tempY = (int) (mTechTopY + mTechBottomY) / 2;
            path.moveTo(mLineLeft, tempY);
            path.lineTo(mLineRight, tempY);
            canvas.drawPath(path, linePaint);

            //日期大于1天时 绘制阴影 等于1天时绘制竖线
            if (mTrendDays > 1) {
                double width = (double) (mLineRight - mLineLeft) / mTrendDays;
                for (int i = 0; i < mTrendDays - 1; i++) {
                    float tempX = mLineLeft + (float) (width * (i + 1) + 0.5);
                    path.moveTo(tempX, mTlineTopY);
                    path.lineTo(tempX, mTlineBottomY);
                    path.moveTo(tempX, mTechTopY);
                    path.lineTo(tempX, mTechBottomY);
                }
                canvas.drawPath(path, linePaint);
                // 阴影
                Rect r = new Rect(mLineLeft + 1, mTlineTopY + 1, (int) (mLineLeft + width * (mTrendDays - 1) + 0.5), mTlineBottomY);
                linePaint.setPathEffect(null);
                linePaint.setStyle(Paint.Style.FILL);
                linePaint.setColor(mBgHistColor);
                canvas.drawRect(r, linePaint);
                r.set(mLineLeft + 1, mTechTopY + 1, (int) (mLineLeft + width * (mTrendDays - 1) + 0.5), mTechBottomY);
                canvas.drawRect(r, linePaint);
            } else {
                //                int tradenum = mStockData.getTradeMinute();
                //                if (tradenum > 1) {
                //                    float tempX = mLineLeft + (mLineRight - mLineLeft) * mStockData.getTimePoint(mStockData.tradetime[1]) /
                // tradenum;
                float tempX = (mLineRight - mLineLeft) / 2 + mLineLeft;
                linePaint.setPathEffect(null);
                canvas.drawLine(tempX, mTlineTopY, tempX, mTlineBottomY, linePaint);
                canvas.drawLine(tempX, mTechTopY, tempX, mTechBottomY, linePaint);
                //                }
            }

        }

        private void dataInit() {

        }

        private void drawInit() {
            mLeft = mClientRect.left;
            mRight = mClientRect.right;

            mPaint.setTextSize(mFontSize);
            mLineLeft = (int) mPaint.measureText("12345.67");
            mLineRight = mRight - 5 - (int) mPaint.measureText("10.0%");

            mTlineTopY = mClientRect.top + mTopHeight;
            mTechBottomY = mClientRect.bottom - mFontH;
            int vHeight = mTechBottomY - mFontH - mTlineTopY;
            mTlineBottomY = (int) (mTechBottomY - vHeight / 3.0 + 0.5);
            mTechTopY = mTlineBottomY + mTechLineH;
            mTlineMidY = (int) ((mTlineTopY + mTlineBottomY) / 2.0 + 0.5);
            mLineSpace = (double) (mTlineMidY - mTlineTopY) / 2.0;
            Log.e(TAG, "--" + mTechBottomY);
        }
    }
}











































