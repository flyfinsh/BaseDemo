package com.meilicat.basedemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.meilicat.basedemo.utils.LogUtils;

/**
 * Created by cj on 2016/2/16.
 * 这是expandableListView的子条目的listview
 * 需要重写onMeasure
 */
public class SubListView extends ListView{

    public SubListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
         protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        LogUtils.i("SubListView------" + expandSpec);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
