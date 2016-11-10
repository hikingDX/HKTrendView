package com.hiking.trendview1109;

import android.content.Context;
import android.graphics.Canvas;
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

/**
 * Created by Administrator on 2016/11/9.
 */
public class TrendView extends FrameLayout {
    private final static String TAG = "TrendView";

    private int mTopHeight = 36;
    private int mTechLineH = 24;    //指标信息行高度
    private int mTechBtnW = 23;
    private int mTechBtnH = 20;

    private float mFontSize;//字体大小
    private int mFontH = 0;//字体高度
    private TrendLineView mTrendLineView;
    private Context mContext;

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
        private int mTechTopY = 0;
        private int mTechBottomY = 0;
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
        private int mTrendDays=4;            //走势线天数

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

        /**
         * 绘制走势图
         *
         * @param canvas
         */
        private void drawTrendLine(Canvas canvas) {
            //hello
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
            PathEffect effect = new DashPathEffect(new float[]{3,2},0);
            linePaint.setPathEffect(effect);
            Path path = new Path();
            int tempY = (int) (mTlineMidY - mLineSpace + 0.5);
            path.moveTo(mLineLeft,tempY);
            path.lineTo(mLineRight,tempY);
             tempY = (int) (mTlineMidY + mLineSpace + 0.5);
            path.moveTo(mLineLeft, tempY);
            path.lineTo(mLineRight, tempY);
            tempY = (int) (mTechTopY + mTechBottomY) / 2;
            path.moveTo(mLineLeft, tempY);
            path.lineTo(mLineRight, tempY);
            canvas.drawPath(path,linePaint);

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
//                    float tempX = mLineLeft + (mLineRight - mLineLeft) * mStockData.getTimePoint(mStockData.tradetime[1]) / tradenum;
//                    //    			path.moveTo(tempX, mTlineTopY);
//                    //    			path.lineTo(tempX, mTlineBottomY);
//                    //    			path.moveTo(tempX, mTechTopY);
//                    //    			path.lineTo(tempX, mTechBottomY);
//                    //    			canvas.drawPath(path, linePaint);
//                    linePaint.setPathEffect(null);
//                    canvas.drawLine(tempX, mTlineTopY, tempX, mTlineBottomY, linePaint);
//                    canvas.drawLine(tempX, mTechTopY, tempX, mTechBottomY, linePaint);
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











































