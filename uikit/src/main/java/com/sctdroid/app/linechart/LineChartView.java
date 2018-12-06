package com.sctdroid.app.linechart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Observable;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.text.TextPaint;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by lixindong on 9/23/16.
 */

public class LineChartView extends View {

    public static final String TAG = LineChartView.class.getSimpleName();
    private final DataObserverImpl dataObserver = new DataObserverImpl();
    protected int mUnitWidth;
    protected int mFillColor;
    protected int mCoordinateColor;
    protected int mStrokeWidth;
    protected int mCoordinateWidth;
    protected int mContentPaddingTop;
    protected int mContentPaddingBottom;
    protected int mDotTextSize;
    protected int mAxisTextSize;
    protected int mAxisTextColor;

    protected int mAxisLineToCurveAreaGapHeight;
    protected int mAxisTextToLineGapHeight;

    private int mCorner;

    protected int mContentPaddingStart;
    protected int mContentPaddingEnd;

    protected int mGravity = 0;
    private TextView tvTitle;
    private TextView tvText1;
    private TextView tvText2;
    private View popupInfoWindow;

    /**
     * <flag name="top" value="0x01" />
     * <flag name="bottom" value="0x02" />
     * <flag name="start" value="0x04" />
     * <flag name="end" value="0x08" />
     * <flag name="center_vertical" value="0x10" />
     * <flag name="center_horizontal" value="0x20" />
     * <flag name="center" value="0x30" />
     */
    public static class Gravity {
        public final static int TOP = 0x01;
        public final static int BOTTOM = 0x02;
        public final static int START = 0x04;
        public final static int END = 0x08;
        public final static int CENTER_VERTICAL = 0x10;
        public final static int CENTER_HORIZONTAL = 0x20;
        public final static int CENTER = 0x30;
    }

    private boolean mShowXLine = false;
    private boolean mShowXText = false;

    protected Paint mContentPaint;
    protected Paint mCoordinatePaint;
    protected Paint mBackgroundPaint;
    protected Paint mBaseLinePaint;
    protected TextPaint mXAxisPaint;
    protected TextPaint mDotTextPaint;
    protected CornerPathEffect mCornerPathEffect;
    ArrayList<Float> xPoints = new ArrayList<>();

    private PaintFlagsDrawFilter mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    public LineChartView(Context context) {
        this(context, null);
    }

    public LineChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("InflateParams")
    public LineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
        popupInfoWindow = LayoutInflater.from(context).inflate(R.layout.layout_popup_info_window, null);
        tvTitle = (TextView) popupInfoWindow.findViewById(R.id.tvTitle);
        tvText1 = (TextView) popupInfoWindow.findViewById(R.id.tvText1);
        tvText2 = (TextView) popupInfoWindow.findViewById(R.id.tvText2);
    }

    private void init() {
        mCornerPathEffect = new CornerPathEffect(mCorner);

        mContentPaint = new Paint();
        mContentPaint.setStyle(Paint.Style.STROKE);
        mContentPaint.setStrokeWidth(mStrokeWidth);
        mContentPaint.setPathEffect(mCornerPathEffect);

        mCoordinatePaint = new Paint();
        mCoordinatePaint.setStyle(Paint.Style.STROKE);
        mCoordinatePaint.setColor(mCoordinateColor);
        mCoordinatePaint.setStrokeWidth(mCoordinateWidth);
        mCoordinatePaint.setPathEffect(mCornerPathEffect);

        mBaseLinePaint = new Paint();
        mBaseLinePaint.setStyle(Paint.Style.STROKE);
        mBaseLinePaint.setColor(Color.parseColor("#E38E1B"));
        mBaseLinePaint.setStrokeWidth(1);

        mBackgroundPaint = new Paint();
//        mBackgroundPaint.setColor(mFillColor);

        mXAxisPaint = new TextPaint();
        mXAxisPaint.setColor(mAxisTextColor);
        mXAxisPaint.setTextSize(mAxisTextSize);

        mDotTextPaint = new TextPaint();
        mDotTextPaint.setTextSize(mDotTextSize);

        //        mMaxVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        mMaxVelocity = 3000;
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LineChartView, 0, 0);
        try {
            mUnitWidth = a.getDimensionPixelSize(R.styleable.LineChartView_unitWidth, 120);
            mFillColor = a.getColor(R.styleable.LineChartView_backgroundColor, Color.TRANSPARENT);
            mCoordinateColor = a.getColor(R.styleable.LineChartView_xyCoordinateColor, Color.BLACK);
            mStrokeWidth = a.getDimensionPixelSize(R.styleable.LineChartView_strokeWidth, 10);
            mCoordinateWidth = a.getDimensionPixelSize(R.styleable.LineChartView_coordinateWidth, 10);
            mContentPaddingTop = a.getDimensionPixelSize(R.styleable.LineChartView_contentPaddingTop, 0);
            mContentPaddingBottom = a.getDimensionPixelSize(R.styleable.LineChartView_contentPaddingBottom, 0);
            mDotTextSize = a.getDimensionPixelSize(R.styleable.LineChartView_dotTextSize, 60);
            mAxisTextSize = a.getDimensionPixelSize(R.styleable.LineChartView_axisTextSize, 40);
            mAxisTextColor = a.getColor(R.styleable.LineChartView_axisTextColor, Color.BLACK);
            mMinLevel = a.getInteger(R.styleable.LineChartView_minLevel, 0);
            mMaxLevel = a.getInteger(R.styleable.LineChartView_maxLevel, 0);
            mCorner = a.getDimensionPixelSize(R.styleable.LineChartView_corner, 0);

            mContentPaddingStart = a.getDimensionPixelSize(R.styleable.LineChartView_contentPaddingStart, 0);
            mContentPaddingEnd = a.getDimensionPixelSize(R.styleable.LineChartView_contentPaddingEnd, 0);

            mShowXLine = a.getBoolean(R.styleable.LineChartView_showXLine, false);
            mShowXText = a.getBoolean(R.styleable.LineChartView_showXText, false);

            mGravity = a.getInteger(R.styleable.LineChartView_dotTextGravity, 0);

            mAxisTextToLineGapHeight = a.getDimensionPixelSize(R.styleable.LineChartView_axisTextToLineGapHeight, 0);
            mAxisLineToCurveAreaGapHeight = a.getDimensionPixelSize(R.styleable.LineChartView_axisLineToCurveAreaGapHeight, 0);

        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Y轴刻度字体的最大宽度。
        float scaleWidth = mDotTextPaint.measureText("10000");
        // 设置抗锯齿
        canvas.setDrawFilter(mPaintFlagsDrawFilter);

        canvas.drawColor(mFillColor);

        boolean renewable = false;
        int maxDecorationsSize = 0;
        if (mAdapters != null) {
            for (Adapter adapter : mAdapters) {
                if (adapter.mDecorations != null) {
                    int size = adapter.mDecorations.size();
                    if (size != 0) {
                        renewable = true;
                        maxDecorationsSize = size > maxDecorationsSize ? size : maxDecorationsSize;
                    }
                }
            }
        }
        if (!renewable) {
            return;
        }

        int curveAreaHeight = getHeight()
                - mContentPaddingTop
                - mContentPaddingBottom
                - mAxisLineToCurveAreaGapHeight
                - mAxisTextToLineGapHeight
                - mAxisTextSize
                - mStrokeWidth;
        int axisY = getHeight() - mContentPaddingBottom - mAxisTextToLineGapHeight - mAxisTextSize;

        int totalLevel = mMaxLevel - mMinLevel;
        int heightPerLevel = curveAreaHeight / totalLevel;

        float scaleX = 1f;
        int unitWidth = (int) ((getWidth() - mContentPaddingStart - mContentPaddingEnd - scaleWidth) / (maxDecorationsSize - 1));

        xPoints.clear();
        for (Adapter adapter : mAdapters) {
            mContentPaint.setColor(adapter.getColor());
            mDotTextPaint.setColor(adapter.getColor());
            if (adapter.mContentPath.isEmpty() || mForceUpdate) {
                mForceUpdate = false;
                adapter.mContentPath.moveTo(scaleWidth + 40, axisY - (adapter.mDecorations.get(0).getLevel() - mMinLevel) * heightPerLevel);
                for (int i = 1; i < adapter.mDecorations.size(); i++) {
                    float x = scaleWidth + i * unitWidth * scaleX + 40;
                    adapter.mContentPath.lineTo(x, axisY - (adapter.mDecorations.get(i).getLevel() - mMinLevel) * heightPerLevel);
                }
            }
            canvas.drawPath(adapter.mContentPath, mContentPaint);
            for (int i = 0; i < adapter.mDecorations.size(); i++) {
                ItemDecoration decoration = adapter.mDecorations.get(i);

                float dotX = (unitWidth * scaleX * i) + scaleWidth + 40;
                float dotY = axisY - (adapter.mDecorations.get(i).getLevel() - mMinLevel) * heightPerLevel;

                // draw x axis text
                if (mShowXText && decoration.isXAxisTextVisible()) {
                    int offsetX = getTextOffsetX(mXAxisPaint, decoration.getXAxisText(), Gravity.CENTER_HORIZONTAL);
                    canvas.drawText(decoration.getXAxisText(), dotX + offsetX, getHeight() - mContentPaddingBottom, mXAxisPaint);
                }
                // draw mark text on dot
                for (Mark mark : decoration.getMarks()) {
                    int offsetX = getTextOffsetX(mDotTextPaint, mark.content, mark.gravity) + mark.marginStart - mark.marginEnd;
                    int offsetY = getTextOffsetY(mDotTextPaint, mark.gravity) + mark.marginTop - mark.marginBottom;

                    canvas.drawCircle(dotX, dotY, 6, mDotTextPaint);
                    if (!xPoints.contains(dotX)) {
                        xPoints.add(dotX);
                    }
                    if (mark.content != null && !mark.content.trim().isEmpty()) {
                        canvas.drawText(mark.content, dotX + offsetX, dotY + offsetY, mDotTextPaint);
                    }
                }
            }
        }

        //当前的Y轴刻度。
        int curYScale = 0;
        if (mShowXLine) {
            canvas.drawText("0", scaleWidth - mDotTextPaint.measureText("0") - 10, axisY, mXAxisPaint);
            float startX = scaleWidth + 10;
            canvas.drawLine(startX, axisY, getWidth(), axisY, mCoordinatePaint);

            //Y轴的刻度数。
            int scaleY = totalLevel / 5;
            for (int i = 1; i <= scaleY; i++) {
                //下面的乘以10是表示没个刻度代表10
                curYScale = axisY - i * heightPerLevel * 5;
                String scaleText = String.valueOf(i * 5);
                canvas.drawText(scaleText, scaleWidth - mDotTextPaint.measureText("0") - 10, curYScale, mXAxisPaint);
                canvas.drawLine(startX, curYScale, getWidth(), curYScale, mCoordinatePaint);
            }
        }

        if (curTouchX != -1) {
            float x;
            float realX = x = curTouchX - scaleWidth + 40;
            int position = -1;
            for (int i = 0; i < xPoints.size(); i++) {
                float point = xPoints.get(i);
                if ((i == 0 && x < point) || (i == xPoints.size() - 1 && x > point)) {
                    realX = point;
                    position = i;
                    break;
                } else if (i < xPoints.size() - 1) {
                    Float nextPoint = xPoints.get(i + 1);
                    if (x > point && x < nextPoint) {
                        if (x - point > nextPoint - x) {
                            realX = nextPoint;
                            position = i + 1;
                        } else {
                            realX = point;
                            position = i;
                        }
                        break;
                    }
                }
            }
            if (position != -1) {
                canvas.drawLine(realX, curYScale, realX, axisY, mBaseLinePaint);
                Bitmap bitmap = getPopupInfoBitmap(position);
                int bitWidth = bitmap.getWidth();
                int left = curTouchX + 60 + bitWidth > (getWidth() - mContentPaddingEnd) ? curTouchX - 60 - bitWidth : curTouchX + 60;
                canvas.drawBitmap(bitmap, left, curYScale + 10, mBaseLinePaint);
            }
        }
    }

    private Bitmap getPopupInfoBitmap(int position) {
        ItemDecoration item1 = mAdapters[0].mDecorations.get(position);
        ItemDecoration item2 = mAdapters[1].mDecorations.get(position);
        try {
            tvTitle.setText(item1 == null ? item2.getXAxisText() : item1.getXAxisText());
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvText1.setText(String.format(Locale.CHINA, "面试数：%d", item1 == null ? 0 : item1.getLevel()));
        tvText2.setText(String.format(Locale.CHINA, "入职数：%d", item2 == null ? 0 : item2.getLevel()));
        popupInfoWindow.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        int measuredWidth = popupInfoWindow.getMeasuredWidth();
        int measuredHeight = popupInfoWindow.getMeasuredHeight();

        popupInfoWindow.layout(0, 0, measuredWidth, measuredHeight);


        Bitmap r = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
        r.eraseColor(Color.TRANSPARENT);

        popupInfoWindow.draw(new Canvas(r));
        return r;
    }

    private int getTextOffsetY(TextPaint paint, int gravity) {
        int height = (int) (paint.getFontMetrics().descent - paint.getFontMetrics().ascent);
        int offset = (int) (paint.getFontMetrics().descent + paint.getFontMetrics().ascent) / 2;
        if ((gravity & Gravity.CENTER_VERTICAL) != 0) {
            offset += height / 2;
        } else if ((gravity & Gravity.BOTTOM) != 0) {
            offset += height;
        }
        return offset;
    }

    private int getTextOffsetX(TextPaint paint, String s, int gravity) {
        int width = (int) paint.measureText(s);
        int offset = 0;
        if ((gravity & Gravity.CENTER_HORIZONTAL) != 0) {
            offset = -width / 2;
        } else if ((gravity & Gravity.START) != 0) {
            offset = -width;
        }

        return offset;
    }

    int curTouchX = -1;
    int mMaxVelocity;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                curTouchX = (int) event.getX();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                curTouchX = (int) event.getX();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                curTouchX = -1;
                invalidate();
                break;
        }
        return true;
    }

    private Adapter[] mAdapters;

    public void setAdapter(Adapter... adapters) {
        if (mAdapters != null) {
            for (Adapter adapter : mAdapters) {
                adapter.unregisterDataSetObserver(dataObserver);
            }
        }
        mAdapters = adapters;
        int max = 0;
        for (Adapter adapter : adapters) {
            adapter.registerDataSetObserver(dataObserver);
            if (mMaxLevel == 0) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    int l = adapter.getLevel(i);
                    max = l > max ? l : max;
                }
            }
        }
        mMaxLevel = max == 0 ? 100 : max % 5 > 0 ? (max + 5) / 5 * 5 : max;
        updateAdapterData();
    }

    private int mMinLevel;
    private int mMaxLevel;

    private boolean mForceUpdate = false;


    private void updateAdapterData() {
        mForceUpdate = true;
        clearData();
        if (mAdapters == null) {
            return;
        }

        for (Adapter adapter : mAdapters) {
            for (int i = 0; i < adapter.getCount(); i++) {
                adapter.onUpdate(i);
            }
        }
    }

    private void clearData() {
        for (Adapter adapter : mAdapters) {
            for (int i = 0; i < adapter.getCount(); i++) {
                adapter.onClear();
            }
        }
    }

    public abstract static class Adapter {

        Path mContentPath = new Path();
        private SparseArray<ItemDecoration> mDecorations = new SparseArray<>();
        private final DataObservable mDataSetObservable = new DataObservable();

        void registerDataSetObserver(DataObserver observer) {
            mDataSetObservable.registerObserver(observer);
        }

        void unregisterDataSetObserver(DataObserver observer) {
            mDataSetObservable.unregisterObserver(observer);
        }

        /**
         * Notifies the attached observers that the underlying data has been changed
         * and any View reflecting the data set should refresh itself.
         */
        public void notifyDataSetChanged() {
            mDataSetObservable.notifyChanged();
        }

        /**
         * @return 点的数量
         */
        public abstract int getCount();


        /**
         * 添加自定义数据, 未完成
         *
         * @param position
         */
        void onUpdate(int position) {
            ItemDecoration decoration = new ItemDecoration();
            decoration.setMarks(onCreateMarks(position));
            onSetXAxisText(decoration, position);
            decoration.setLevel(getLevel(position));
            mDecorations.append(position, decoration);
        }

        void onClear() {
            mDecorations.clear();
            mContentPath.reset();
        }

        /**
         * 设置点上的文字，每个mark是一个，可同时设置点的 8 个方向的文字
         * 注意: Gravity 应使用 CurveView.Gravity 类
         *
         * @param position
         * @return
         */
        public Collection<Mark> onCreateMarks(int position) {
            return Collections.emptySet();
        }

        /**
         * level 是 y 轴高度，在 minLevel 和 maxLevel 之间
         *
         * @param position
         * @return 返回当前 position 的 level
         */
        public abstract int getLevel(int position);

        /**
         * 获取第 i 个点 x 轴上的文字
         *
         * @param i
         * @return
         */
        public void onSetXAxisText(ItemDecoration itemDecoration, int i) {
        }

        @ColorInt
        public abstract int getColor();
    }

    public static class Mark {
        final String content;
        final int gravity;
        final int marginStart;
        final int marginEnd;
        final int marginTop;
        final int marginBottom;
        final TextAppearanceSpan textAppearanceSpan;

        public Mark(String content) {
            this(content, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        }

        public Mark(String content, int gravity) {
            this(content, gravity, 0, 0, 0, 0);
        }

        public Mark(String content, int gravity, int marginStart, int marginTop, int marginEnd, int marginBottom) {
            this(content, gravity, marginStart, marginTop, marginEnd, marginBottom, null);
        }

        public Mark(String content, int gravity, int marginStart, int marginTop, int marginEnd, int marginBottom, TextAppearanceSpan mTextAppearanceSpan) {
            this.content = content;
            this.gravity = gravity;
            this.marginStart = marginStart;
            this.marginEnd = marginEnd;
            this.marginTop = marginTop;
            this.marginBottom = marginBottom;
            this.textAppearanceSpan = mTextAppearanceSpan;
        }
    }

    public interface DataObserver {
        void onChanged();
    }

    private class DataObserverImpl implements DataObserver {
        @Override
        public void onChanged() {
            updateAdapterData();
            invalidate();
        }
    }

    public static class DataObservable extends Observable<DataObserver> {
        public void notifyChanged() {
            synchronized (mObservers) {
                // since onChanged() is implemented by the app, it could do anything, including
                // removing itself from {@link mObservers} - and that could cause problems if
                // an iterator is used on the ArrayList {@link mObservers}.
                // to avoid such problems, just march thru the list in the reverse order.
                for (int i = mObservers.size() - 1; i >= 0; i--) {
                    mObservers.get(i).onChanged();
                }
            }
        }
    }
}
