package com.meilicat.basedemo.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.UIUtils;

/**
 * Created by cj on 2016/3/16.
 */
public class CustomSureDialog  extends Dialog{

    private EditText mNumEt;
    private TextView mTimeEt;
    private EditText mInfoEt;
    private TextView mCancelTv;
    private TextView mComfirmTv;


    public CustomSureDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }


    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(UIUtils.getContext());
        View view = inflater.inflate(R.layout.dialog_comfirm_handler, null);

        mNumEt = (EditText) view.findViewById(R.id.dialog_comfirm_number);
        mTimeEt = (TextView) view.findViewById(R.id.dialog_comfirm_time);
        mInfoEt = (EditText) view.findViewById(R.id.dialog_comfirm_info);

        mCancelTv = (TextView) view.findViewById(R.id.dialog_comfirm_cancel);
        mComfirmTv = (TextView) view.findViewById(R.id.dialog_comfirm_comfirm);
        setContentView(view);
    }

    public void setCancelOnClick(View.OnClickListener listener){
        mCancelTv.setOnClickListener(listener);
    }
    public void setComfirmOnClick(View.OnClickListener listener){
        mComfirmTv.setOnClickListener(listener);
    }

    public void changeTime(String date){
        LogUtils.i("设置time为-----"+date);
        mTimeEt.setText(" "+date);
    }

    public void setTimeOnClickListener(View.OnClickListener listener){
        mTimeEt.setOnClickListener(listener);
    }

    public String getNum(){
        return mNumEt.getText().toString().trim();
    }

    public String getInfo(){
        return mInfoEt.getText().toString().trim();
    }

}
