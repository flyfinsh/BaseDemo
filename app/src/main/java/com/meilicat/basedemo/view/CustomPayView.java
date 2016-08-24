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
 * Created by cj on 2016/3/5.
 */
public class CustomPayView extends LinearLayout {

    @Bind(R.id.pay_view_date)
    TextView mPayViewDate;
    @Bind(R.id.pay_view_num)
    TextView mPayViewNum;

    public CustomPayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomPayView(Context context) {
        this(context, null);
    }

    public CustomPayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.goods_state_pay_view, this);
        ButterKnife.bind(this);
    }

    public void setPayDate(String date){
        mPayViewDate.setText(date);
    }

    public void setPayNum(String num){
        mPayViewNum.setText("￥ "+num);
    }
}
