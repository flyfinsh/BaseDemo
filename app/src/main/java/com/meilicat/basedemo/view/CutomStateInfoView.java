package com.meilicat.basedemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cj on 2016/2/16.
 * 这是自定义的控制订单流程样式的view
 */
public class CutomStateInfoView extends LinearLayout {

    @Bind(R.id.child_test_tv)
    TextView mChildTestTv;//是否有额外的信息
    @Bind(R.id.child_state2)
    TextView mChildState2;//商品备齐的时间
    @Bind(R.id.child_state3_info)
    TextView mChildState3Info;//签收备注
    @Bind(R.id.state_result)
    LinearLayout mStateResult;//是否签收，未签收设置隐藏
    @Bind(R.id.state_confirm)
    TextView mStateConfirm;//确认签收的按钮，未签收显示这个
    @Bind(R.id.state_button)
    LinearLayout mStateButton;


    public CutomStateInfoView(Context context) {
        this(context, null);
    }

    public CutomStateInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CutomStateInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.cutom_state_info, this);
        ButterKnife.bind(this);
    }

    /**
     * 设置是否有备齐的额外信息
     * */
    public void setSignInfo(boolean hasInfo,String info){
        if (hasInfo){
            //有额外的信息
            mChildTestTv.setVisibility(View.VISIBLE);
            mChildTestTv.setText(info);
        }else {
            //没有额外的信息
            mChildTestTv.setVisibility(View.GONE);
        }
    }

    /**
     * 设置备齐的时间
     * */
    public void setReadyTime(String time){
        mChildState2.setText("货品标记于"+time+"备齐");
    }

    /**
     * 是否已签收，显示不同的view
     * */
    public void isAccept(boolean isAccept){
        if (isAccept){
            //说明已经签收了
            LogUtils.i("显示签收结果。。。。");
            mStateResult.setVisibility(View.VISIBLE);
            mStateConfirm.setVisibility(View.GONE);

        }else {
            mStateConfirm.setVisibility(View.VISIBLE);
            mStateResult.setVisibility(View.GONE);

        }
    }
    /**
     * 这是设置签收备注的方法
     * */
    public void setAcceptInfo(boolean hasInfo,String acceptInfo){
        if (hasInfo){
            mChildState3Info.setText(acceptInfo);
        }else{
            mChildState3Info.setText("没有签收备注");
        }

    }

    /**
     * 设置button的点击事件
     * */
    public void setConfirmClickListener(View.OnClickListener listener){
//        LogUtils.i("设置button的点击事件");
        mStateButton.setOnClickListener(listener);

    }

}
