package com.sctdroid.app.linechart;

import java.util.ArrayList;
import java.util.Collection;

/**
 * **描述:** 描述数据信息。
 * <p>
 * **创建人:** kelin
 * <p>
 * **创建时间:** 2018/12/6  上午11:01
 * <p>
 * **版本:** v 1.0.0
 */
public class ItemDecoration {
    private Collection<LineChartView.Mark> mMarks = new ArrayList<>();
    private int mLevel;
    private String mXAxisText;
    private boolean mIsXAxisTextVisible;

    public void setXAxisText(String text, boolean visible) {
        mXAxisText = text;
        mIsXAxisTextVisible = visible;
    }

    public String getXAxisText() {
        return mXAxisText;
    }

    boolean isXAxisTextVisible() {
        return mIsXAxisTextVisible;
    }

    public void setLevel(int level) {
        this.mLevel = level;
    }

    int getLevel() {
        return mLevel;
    }

    public Collection<LineChartView.Mark> getMarks() {
        return mMarks;
    }

    public void setMarks(Collection<LineChartView.Mark> mMarks) {
        this.mMarks = mMarks;
    }
}
