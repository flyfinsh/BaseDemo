package com.meilicat.basedemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.utils.ImageLoaderUtil;
import com.meilicat.basedemo.utils.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cj on 2016/2/15.
 * 这是买手模块的订单界面的可复用的厂家部分的view
 */
public class CustomBuyerBrandView extends LinearLayout {
    @Bind(R.id.brand_logo)
    ImageView mBrandLogo;
    @Bind(R.id.brand_name)
    TextView mBrandName;
    @Bind(R.id.brand_type)
    TextView mBrandType;
    @Bind(R.id.brand_address)
    TextView mBrandAddress;
    @Bind(R.id.brand_saler)
    TextView mBrandSaler;
    @Bind(R.id.brand_number)
    TextView mBrandNumber;
    @Bind(R.id.brand_arrow)
    ImageView mBrandArrow;
    @Bind(R.id.brand_divi)//这是分割线
    ImageView mBrandDivi;
    @Bind(R.id.brand_checkbox)
    CheckBox mBrandCheckbox;
    @Bind(R.id.brand_time)
    TextView mBrandTime;
    @Bind(R.id.brand_num)
    TextView mBrandNum;

    public CustomBuyerBrandView(Context context) {
        this(context, null);
    }

    public CustomBuyerBrandView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomBuyerBrandView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.buyer_custom_commont_view, this);
        ButterKnife.bind(this);

    }

    /**提供一系列的方法，用于更改那啥 */

    /**
     * 设置品牌的logo
     */
    public void setBrandLogo(String url) {
        ImageLoaderUtil.getInstance(UIUtils.getContext()).displayImage(url,mBrandLogo);
    }

    /**
     * 设置品牌的名字
     */
    public void setBrandName(String name) {
        mBrandName.setText(name);
    }


    /**
     * 设置工厂的地址
     */
    public void setBrandAddress(String address) {
        mBrandAddress.setText("地址：" + address);
    }

    /**
     * 设置销售联系人
     */
    public void setBrandSaler(String name) {
        mBrandSaler.setText("销售联系人：" + name);
    }

    /**
     * 设置联系电话
     */
    public void setBrandNumber(String number) {
        mBrandNumber.setText("电话：" + number);
    }

    public void changeImageView(boolean isDown) {
        if (isDown) {
            //说明是向下的
            mBrandArrow.setImageResource(R.mipmap.order_commont_up);
        } else {
            mBrandArrow.setImageResource(R.mipmap.order_commont_down);
        }
    }

    /**
     * 提供一个隐藏分割线的方法
     */
    public void hintDivi(boolean isHint) {
        if (isHint) {
            mBrandDivi.setVisibility(View.GONE);
        } else {
            mBrandDivi.setVisibility(View.VISIBLE);
        }
    }

    public void hintArrow() {
        mBrandArrow.setVisibility(View.GONE);
    }

    public void showCheckBox() {
        mBrandCheckbox.setVisibility(View.VISIBLE);
    }

    public void checkBoxCheckStateChangeListener(CheckBox.OnCheckedChangeListener listener) {
        mBrandCheckbox.setOnCheckedChangeListener(listener);
    }

    public void setBrandCheck(boolean isChecked) {
        mBrandCheckbox.setChecked(isChecked);
    }

    public void hintCheckBox() {
        mBrandCheckbox.setVisibility(View.GONE);
    }

    public void setBrandTime(String name){
        mBrandTime.setText(name);
    }

    public void setBrandNum(int number){
        mBrandNum.setText(number+"件");
    }

    public void setIsSign(boolean isSign){
        if (isSign){
            //说明是qiany签约商户
            mBrandType.setVisibility(View.VISIBLE);
        }else {
            mBrandType.setVisibility(View.GONE);
        }
    }


}
