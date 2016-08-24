package com.meilicat.basedemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.utils.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cj on 2016/1/26.
 * 这是自定义的订单列表的条目能共用的view
 * 提供了一系列设置文本和颜色的方法
 *
 */
public class CutomOrderItemView extends LinearLayout {

    @Bind(R.id.item_order_common_view_number)
    TextView mItemOrderCommonViewNumber;
    @Bind(R.id.item_order_common_view_state)
    TextView mItemOrderCommonViewState;
    @Bind(R.id.item_order_common_view_paytime)
    TextView mItemOrderCommonViewPaytime;
    @Bind(R.id.item_order_common_view_finishtime)
    TextView mItemOrderCommonViewFinishtime;
    @Bind(R.id.item_order_common_view_clothiamge)
    ImageView mItemOrderCommonViewClothiamge;
    @Bind(R.id.item_order_common_view_firstimage)
    ImageView mItemOrderCommonViewFirstimage;
    @Bind(R.id.item_order_common_view_goodsname)
    TextView mItemOrderCommonViewGoodsname;
    @Bind(R.id.item_order_common_view_goodsnum)
    TextView mItemOrderCommonViewGoodsnum;
    @Bind(R.id.item_order_common_view_clothcolor)
    TextView mItemOrderCommonViewClothcolor;
    @Bind(R.id.item_order_common_view_clothsize)
    TextView mItemOrderCommonViewClothsize;
    @Bind(R.id.item_order_common_view_clothnum)
    TextView mItemOrderCommonViewClothnum;
    @Bind(R.id.item_order_common_view_clothprice)
    TextView mItemOrderCommonViewClothprice;

    public CutomOrderItemView(Context context) {
        this(context, null);
    }

    public CutomOrderItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CutomOrderItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_order_common_view, this);

        ButterKnife.bind(this);

    }
    /**********提供一系列的方法用于外部设置*********/
    /**
     * 设置订单编号
     * */
    public void setOrderNumber(String number){
        mItemOrderCommonViewNumber.setText( "订单号："+number);
    }

    /**
     * 设置订单的状态
     * */
    public void setOrderState(boolean isHandler){
        if (!isHandler){
            //说明是已处理的
            mItemOrderCommonViewState.setText("未处理");
            mItemOrderCommonViewState.setTextColor(getResources().getColor(R.color.title_red));
        }else {
            mItemOrderCommonViewState.setText("已处理");
            mItemOrderCommonViewState.setTextColor(getResources().getColor(R.color.gray_line));
        }
    }

    /**
     *设置付款的时间
     * */
    public void setPayTime(String time){
        mItemOrderCommonViewPaytime.setText(time);
    }

    /**
     * 设置货物需要备齐的时间
     * */
    public void setFinishTime(String time){
        mItemOrderCommonViewFinishtime.setText(time);
    }
    /**
     * 设置货物需要备齐的时间可见与不可见
     * */
    public void setFinishTimeVisi(boolean isVisiable){
        if (isVisiable){
            //说明让他可见
            mItemOrderCommonViewFinishtime.setVisibility(View.VISIBLE);
        }else {
            mItemOrderCommonViewFinishtime.setVisibility(View.GONE);
        }
    }

    /**
     * 设置货物需备齐的时间的颜色
     * */
    public void setFinishTimeColor(boolean isRed){
        if (isRed){
            //说明是红色的
            mItemOrderCommonViewFinishtime.setTextColor(getResources().getColor(R.color.title_red));
        }else {
            //
            mItemOrderCommonViewFinishtime.setTextColor(getResources().getColor(R.color.gray_line));
        }
    }

    /**
     * 返回衣服图片的对象，让调用者去加载
     * */
    public ImageView getClothImageView(){
        return mItemOrderCommonViewClothiamge;
    }

    /**
     * 返回首单ImageView的对象
     * */
    public ImageView getFirstImageView(){
        return mItemOrderCommonViewFirstimage;
    }

    /**
     * 设置衣服的名字
     * */
    public void setClothName(String name){
        mItemOrderCommonViewGoodsname.setText(name);
    }
    /**
     * 设置货号
     * */
    public void setClothNum(String number){
        mItemOrderCommonViewGoodsnum.setText(number);
    }

    /**
     *设置衣服的颜色
     * */
    public void setClothColor(String color){
        mItemOrderCommonViewClothcolor.setText(color);
    }

    /**
     * 设置衣服的尺码
     * */
    public void setClothSize(String size){
        mItemOrderCommonViewClothsize.setText(size);
    }

    /**
     * 设置衣服的总数量
     * */
    public void setClothSumNum(String num){
        mItemOrderCommonViewClothnum.setText("X "+num+"（件）");
    }
    /**
     * 设置总价格
     * */
    public void setClothPrice(String price){
        mItemOrderCommonViewClothprice.setText("￥ "+price);
    }

}
