package com.meilicat.basedemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.utils.ImageLoaderUtil;
import com.meilicat.basedemo.utils.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cj on 2016/3/7.
 */
public class CustomBuyerCommontView extends LinearLayout {
    @Bind(R.id.item_order_common_divi)
    ImageView mItemOrderCommonDivi;
    @Bind(R.id.item_order_common_ordernum)
    TextView mItemOrderCommonOrdernum;
    @Bind(R.id.item_order_common_goodsimage)
    ImageView mItemOrderCommonGoodsimage;
    @Bind(R.id.item_order_common_goodstype)
    ImageView mItemOrderCommonGoodstype;
    @Bind(R.id.item_order_common_paytime)
    TextView mItemOrderCommonPaytime;
    @Bind(R.id.item_order_common_sumnum)
    TextView mItemOrderCommonSumnum;
    @Bind(R.id.item_order_common_sumprice)
    TextView mItemOrderCommonSumprice;
    @Bind(R.id.item_order_common_goodsname)
    TextView mItemOrderCommonGoodsname;
    @Bind(R.id.item_order_common_down)
    ImageView mItemOrderCommonDown;
    @Bind(R.id.item_order_common_goodsnum)
    TextView mItemOrderCommonGoodsnum;
    @Bind(R.id.item_order_common_goodscolor)
    TextView mItemOrderCommonGoodscolor;
    @Bind(R.id.item_order_common_goodsize)
    TextView mItemOrderCommonGoodsize;
    @Bind(R.id.item_order_common_goodsprice)
    TextView mItemOrderCommonGoodsprice;

    public CustomBuyerCommontView(Context context) {
        this(context, null);
    }

    public CustomBuyerCommontView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomBuyerCommontView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_order_commont_other, this);
        ButterKnife.bind(this);
    }


    public void setOrderNumber(String number) {
        mItemOrderCommonOrdernum.setText("订单号：" + number);
    }


    public void setPayTime(String time) {
        mItemOrderCommonPaytime.setText(time);
    }


    public void setSumNum(int goodsNum) {
        mItemOrderCommonSumnum.setText(goodsNum + "件");
    }


    public void setOrderPrice(double price) {
        mItemOrderCommonSumprice.setText("￥" + price);
    }

    public void setGoodsName(String name) {
        mItemOrderCommonGoodsname.setText(name);
    }


    public void setGoodsNumber(String number) {
        mItemOrderCommonGoodsnum.setText(number);
    }


    public void setGoodsColor(String color) {
        mItemOrderCommonGoodscolor.setText(color);
    }


    public void setGoodsSize(String siez) {
        mItemOrderCommonGoodsize.setText(siez);
    }


    public void setGoodPrice(double price) {
        mItemOrderCommonGoodsprice.setText("￥"+price);
    }


    public void setGoodsImage(String url) {
        //ImageLoader内部有线程池
        ImageLoaderUtil.getInstance(UIUtils.getContext()).displayImage(url,mItemOrderCommonGoodsimage);
    }


    public void changeImageView(boolean isDown) {
        if (isDown) {
            //说明是向下的
            mItemOrderCommonDown.setImageResource(R.mipmap.order_commont_up);
        } else {
            mItemOrderCommonDown.setImageResource(R.mipmap.order_commont_down);
        }
    }


    public void isVisiable(boolean isVisi) {
        if (isVisi) {
            //说明是可见的
            mItemOrderCommonDivi.setVisibility(View.VISIBLE);
        } else {
            //不可见
            mItemOrderCommonDivi.setVisibility(View.GONE);
        }
    }


    public void hindArrow() {
        mItemOrderCommonDown.setVisibility(View.GONE);
    }


    public void changeGoodsType(boolean isFirst){
        if (isFirst){
            mItemOrderCommonGoodstype.setImageResource(R.mipmap.order_type_first);
        }else {
            mItemOrderCommonGoodstype.setImageResource(R.mipmap.order_type_fan);
        }
    }


}
