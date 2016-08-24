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
 * Created by cj on 2016/1/28.
 * 这是订单列表的条目的可复用的部分
 */
public class CutomCommontOrderItem extends LinearLayout {
    @Bind(R.id.item_order_common_ordernum)
    TextView mItemOrderCommonOrdernum;
    @Bind(R.id.item_order_common_goodsimage)
    ImageView mItemOrderCommonGoodsimage;
    @Bind(R.id.item_order_common_paytime)
    TextView mItemOrderCommonPaytime;
    @Bind(R.id.item_order_common_sumnum)
    TextView mItemOrderCommonSumnum;
    @Bind(R.id.item_order_common_sumprice)
    TextView mItemOrderCommonSumprice;
    @Bind(R.id.item_order_common_down)
    ImageView mItemOrderCommonDown;
    @Bind(R.id.item_order_common_goodsname)
    TextView mItemOrderCommonGoodsname;
    @Bind(R.id.item_order_common_goodsnum)
    TextView mItemOrderCommonGoodsnum;
    @Bind(R.id.item_order_common_goodscolor)
    TextView mItemOrderCommonGoodscolor;
    @Bind(R.id.item_order_common_goodsize)
    TextView mItemOrderCommonGoodsize;
    @Bind(R.id.item_order_common_goodsprice)
    TextView mItemOrderCommonGoodsprice;
    @Bind(R.id.item_order_common_divi)
    ImageView mItemOrderCommonDivi;
    @Bind(R.id.item_order_common_goodstype)
    ImageView mItemOrderCommonGoodstype;

    public CutomCommontOrderItem(Context context) {
        this(context, null);
    }

    public CutomCommontOrderItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CutomCommontOrderItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_order_commont, this);
        ButterKnife.bind(this);
    }

    /** 提供一系列的方法，用于外部设置 */

    /**
     * 设置订单号
     */
    public void setOrderNumber(String number) {
        mItemOrderCommonOrdernum.setText("订单号：" + number);
    }

    /**
     * 设置付款时间
     */
    public void setPayTime(String time) {
        mItemOrderCommonPaytime.setText(time);
    }

    /**
     * 设置数量
     */
    public void setSumNum(int goodsNum) {
        mItemOrderCommonSumnum.setText(goodsNum + "件");
    }

    /**
     * 设置总金额
     */
    public void setOrderPrice(double price) {
        mItemOrderCommonSumprice.setText("￥" + price);
    }

    /**
     * 设置名称
     */
    public void setGoodsName(String name) {
        mItemOrderCommonGoodsname.setText(name);
    }

    /**
     * 设置货号
     */
    public void setGoodsNumber(String number) {
        mItemOrderCommonGoodsnum.setText(number);
    }

    /**
     * 设置颜色
     */
    public void setGoodsColor(String color) {
        mItemOrderCommonGoodscolor.setText(color);
    }

    /**
     * 设置尺码
     */
    public void setGoodsSize(String siez) {
        mItemOrderCommonGoodsize.setText(siez);
    }

    /**
     * 设置衣服的单价
     */
    public void setGoodPrice(double price) {
        mItemOrderCommonGoodsprice.setText("￥"+price);
    }

    /**
     * 设置衣服的图片
     */

    public void setGoodsImage(String url) {
        //ImageLoader内部有线程池
//        BaseApplication.imageLoader.displayImage(url, mItemOrderCommonGoodsimage);
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

    /**
     * 提供一个方法用于设置灰色间隔可见与不可见
     */
    public void isVisiable(boolean isVisi) {
        if (isVisi) {
            //说明是可见的
            mItemOrderCommonDivi.setVisibility(View.VISIBLE);
        } else {
            //不可见
            mItemOrderCommonDivi.setVisibility(View.GONE);
        }
    }

    /**
     * 提供一个隐藏arrow的方法
     */
    public void hindArrow() {
        mItemOrderCommonDown.setVisibility(View.GONE);
    }

/*
    public void changeGoodsType(boolean isFirst){
        if (isFirst){
            mItemOrderCommonGoodstype.setImageResource(R.mipmap.order_type_first);
        }else {
            mItemOrderCommonGoodstype.setImageResource(R.mipmap.order_type_fan);
        }
    }*/

    public void hintGoodsType(){
        mItemOrderCommonGoodstype.setVisibility(View.GONE);
    }


}
