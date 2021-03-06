package com.meilicat.basedemo.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.meilicat.basedemo.R;

/**
 * 标题栏
 */
public class NavigationBar extends RelativeLayout {

    private RelativeLayout mFlNaviLeft;
    private ViewGroup mFlNaviMid;
    private ViewGroup mFlNaviRight;

    public NavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mFlNaviLeft = (RelativeLayout) findViewById(R.id.fl_navi_left_layout);
        mFlNaviMid = (ViewGroup) findViewById(R.id.fl_navi_title);
        mFlNaviRight = (ViewGroup) findViewById(R.id.fl_navi_right);

        // 背景颜色
        setBackgroundColor(Color.parseColor("#ff0099"));
    }

    /**
     * 设置标题视图
     *
     * @param view
     */
    public void setMiddleView(View view) {
        mFlNaviMid.removeAllViews();
        mFlNaviMid.addView(view);
    }

    public View getMiddleView() {
        return (mFlNaviMid.getChildCount() > 0) ? mFlNaviMid.getChildAt(0) : null;
    }

    /**
     * 设置右侧视图
     *
     * @param view
     */
    public void setRightView(View view) {
        for (int i = 0; i < mFlNaviRight.getChildCount(); i++) {
            View childView = mFlNaviRight.getChildAt(i);
            childView.clearAnimation();
            mFlNaviRight.removeViewAt(i);
        }
        if (view != null) {
            mFlNaviRight.addView(view);
        }
    }

    public View getBackView() {
        return mFlNaviLeft;
    }

    public interface IProvideNavigationBar {
        NavigationBar getNavigationBar();

        void setupNavigationBar(int resId);

        // 添加顶部标题栏标题
        public void setCommonTitle(int titleId);

        public void setCommonTitle(CharSequence title);

        // 右边按钮
        public View addRightButtonText(String text, OnClickListener listener);

        public View addRightButtonText(int resId, OnClickListener listener);
    }
}
