package com.meilicat.basedemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.utils.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cj on 2016/1/23.
 */
public class CustomGridItemView extends LinearLayout {

    @Bind(R.id.order_grid_item_number)
    TextView mOrderGridItemNumber;
    @Bind(R.id.order_grid_item_tv)
    TextView mOrderGridItemTv;

    public CustomGridItemView(Context context) {
        this(context, null);
    }

    public CustomGridItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomGridItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.order_grid_item, this);

        //初始化控件
        ButterKnife.bind(this);
    }

    /**
     * 该方法用于设置控件的number
     */
    public void setNumText(String number) {
        mOrderGridItemNumber.setText(number);
    }

    /**
     * 该方法用于设置控件的name
     */
    public void setNameText(int name) {
        mOrderGridItemTv.setText(name);
    }

}
