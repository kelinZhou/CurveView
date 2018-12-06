package com.sctdroid.app.curveview;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sctdroid.app.uikit.CurveView;
import com.sctdroid.app.uikit.CurveView.Gravity;
import com.sctdroid.app.uikit.ItemDecoration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private MyAdapter mAdapter1 = new MyAdapter( Color.parseColor("#E8394F"));
    private MyAdapter mAdapter2 = new MyAdapter( Color.parseColor("#5FA5F2"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CurveView curveView = (CurveView) findViewById(R.id.curve_view);
        curveView.setAdapter(mAdapter1, mAdapter2);


        TextView button = (TextView) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter1.notifyDataSetChanged();
                mAdapter2.notifyDataSetChanged();
            }
        });
    }

    private class MyAdapter extends CurveView.Adapter {

        private List<Integer> list = new ArrayList<>(31);
        private int color;

        public MyAdapter(@ColorInt int color) {
            this.color = color;
            createData();
        }

        private void createData() {
            for (int i = 0; i < 31; i++) {
                list.add((int) (Math.random() * 20));
            }
        }

        @Override
        public void notifyDataSetChanged() {
            list.clear();
            createData();
            super.notifyDataSetChanged();
        }

        /**
         * @return 点的数量
         */
        @Override
        public int getCount() {
            return list.size();
        }

        /**
         * level 是 y 轴高度，在 minLevel 和 maxLevel 之间
         *
         * @param position
         * @return 返回当前 position 的 level
         */
        @Override
        public int getLevel(int position) {
            return list.get(position);
        }

        /**
         * 设置点上的文字，每个mark是一个，可同时设置点的 8 个方向的文字
         * 注意: Gravity 应使用 CurveView.Gravity 类
         *
         * @param position
         * @return
         */
        @Override
        public Collection<CurveView.Mark> onCreateMarks(int position) {
            Set<CurveView.Mark> marks = new HashSet<CurveView.Mark>();
            CurveView.Mark mark = new CurveView.Mark("");
            marks.add(mark);
            return marks;
        }

        /**
         * 获取第 i 个点 x 轴上的文字
         */
        @Override
        public void onSetXAxisText(ItemDecoration itemDecoration, int i) {
            itemDecoration.setXAxisText(String.valueOf(i + 1) + "日", (i & 1) == 0);
        }

        @Override
        @ColorInt
        public int getColor() {
            return color;
        }
    }
}
