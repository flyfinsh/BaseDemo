package com.meilicat.basedemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.meilicat.basedemo.R;


/**
 * 用于个人资料item的显示 将几个系统原生的控件组合到一起，这样创建出的控件就被称为组合控件
 *
 * @author lzm
 */
public class UIPerfectlyItem extends DividerLineLayout {
    private TextView leftTextView;
    private TextView rightTextView;

    public UIPerfectlyItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UIItem);

        int iconResuorce = a.getResourceId(R.styleable.UIItem_leftIcon, R.mipmap.app_setting);
        String leftText = a.getString(R.styleable.UIItem_leftText);
        String rightText = a.getString(R.styleable.UIItem_rightText);
        a.recycle();
        inflate(context, R.layout.item_ui_perfectly, this);

        ImageView leftIconIv = (ImageView) findViewById(R.id.item_lefticon_perfectly);
        leftTextView = (TextView) findViewById(R.id.item_tvLeft_perfectly);
        rightTextView = (TextView) findViewById(R.id.item_tvRight_perfectly);
        //
        leftIconIv.setImageResource(iconResuorce);
        leftTextView.setText(leftText);
        rightTextView.setText(rightText);
    }

    public void setLeftText(String text) {
        leftTextView.setText(text);
    }

    public void setRightText(String text) {
        rightTextView.setText(text);
    }

    public TextView getLeftTextView() {
        return leftTextView;
    }
    public TextView getRightTextView() {
        return rightTextView;
    }

}
