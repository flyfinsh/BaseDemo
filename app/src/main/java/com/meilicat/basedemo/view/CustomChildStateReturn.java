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
 * 这是订单的状态栏，退款部分的自定义view
 */
public class CustomChildStateReturn extends LinearLayout {
    @Bind(R.id.return_date)
    TextView mReturnDate;
    @Bind(R.id.return_number)
    TextView mReturnNumber;
    @Bind(R.id.return_money)
    TextView mReturnMoney;
    @Bind(R.id.retrun_reason)
    TextView mRetrunReason;

    public CustomChildStateReturn(Context context) {
        this(context, null);
    }

    public CustomChildStateReturn(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomChildStateReturn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();

    }

    private void initView() {
        LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.custom_child_state_return, this);
        ButterKnife.bind(this);
    }

    public void setReturnDate(String date) {
        mReturnDate.setText("退款时间：" + date);
    }

    public void setReturnNumber(String number) {
        mReturnNumber.setText("退款件数：" + number + "件");
    }

    public void setReturnMoney(String money) {
        mReturnMoney.setText("退款金额：￥ " + money);
    }
    public void setReturnReason(String reason) {
        mRetrunReason.setText("退款原因：" + reason);
    }

}
