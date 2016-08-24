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
public class CustomSignView extends LinearLayout {

    @Bind(R.id.sign_view_name)
    TextView mSignViewName;
    @Bind(R.id.sign_view_time)
    TextView mSignViewTime;
    @Bind(R.id.sign_view_info)
    TextView mSignViewInfo;

    public CustomSignView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSignView(Context context) {
        this(context, null);
    }

    public CustomSignView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.goods_state_sign_view, this);
        ButterKnife.bind(this);
    }

    public void setSignName(String name){
        mSignViewName.setText(name);
    }

    public void setSignTime(String time){
        mSignViewTime.setText(time);
    }
    public void setSignViewInfo(String info){
        mSignViewInfo.setText("签收备注："+info);
    }

}
