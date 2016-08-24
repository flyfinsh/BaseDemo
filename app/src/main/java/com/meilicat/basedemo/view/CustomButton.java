package com.meilicat.basedemo.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meilicat.basedemo.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cj on 2016/1/22.
 */
public class CustomButton extends LinearLayout {
    @Bind(R.id.hone_custom_btn_icontv)
    IconTextView mHoneCustomBtnIcontv;
    @Bind(R.id.home_custom_btn_tv)
    TextView mHomeCustomBtnTv;

    public CustomButton(Context context) {
        this(context, null);
    }


    public CustomButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);
    }

    private void initView(Context context) {

        LayoutInflater.from(context).inflate(R.layout.cutom_button, this);

        //主要就是提供图片和文字的颜色变化,和设置inconfont
        ButterKnife.bind(this);
    }

    /**
     * 该方法用于设置下面的文字
     * */
    public void setText(String text){
        mHomeCustomBtnTv.setText(text);
    }

    /**
     * 传入一个color的id
     * 设置下面文字的颜色
     * */
    public void changeColor(boolean isRed){
        if (isRed){
            mHomeCustomBtnTv.setTextColor(Color.parseColor("#FD0098"));
            mHoneCustomBtnIcontv.setTextColor(Color.parseColor("#FD0098"));
        }else {
            mHomeCustomBtnTv.setTextColor(Color.parseColor("#CCCCCC"));
            mHoneCustomBtnIcontv.setTextColor(Color.parseColor("#CCCCCC"));
        }
    }

    /**
     * 更改自定义button的图片
     * */
    public void setIcon(int resId){
        mHoneCustomBtnIcontv.setText(resId);
    }


}
