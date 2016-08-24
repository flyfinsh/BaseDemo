package com.meilicat.basedemo.activity.moneyactivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseActivity;
import com.meilicat.basedemo.bean.SupplierInAccountDetail;
import com.meilicat.basedemo.bean.SupplierOutAccountDetail;
import com.meilicat.basedemo.utils.LogUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cj on 2016/2/17.
 */
public class MoneyMangerOrderInner extends BaseActivity {
    @Bind(R.id.money_manger_order_title_back)
    RelativeLayout mMoneyMangerOrderTitleBack;
    @Bind(R.id.money_manger_order_data)
    TextView mMoneyMangerOrderDate;
    @Bind(R.id.money_manger_order_number)
    TextView mMoneyMangerOrderNumber;
    @Bind(R.id.money_manger_order_sizecolor)
    TextView mMoneyMangerOrderSizecolor;
    @Bind(R.id.money_manger_order_goodsprice)
    TextView mMoneyMangerOrderGoodsprice;
    @Bind(R.id.money_manger_order_sumnumber)
    TextView mMoneyMangerOrderSumnumber;
    @Bind(R.id.money_manger_order_totalprice)
    TextView mMoneyMangerOrderTotalprice;
    @Bind(R.id.money_manger_order_blance)
    TextView mMoneyMangerOrderBlance;
    @Bind(R.id.money_manger_order_inner_title_name)
    TextView mMoneyMangerOrderInnerTitleName;
    @Bind(R.id.money_manger_order_date_name)
    TextView mMoneyMangerOrderDateName;
    @Bind(R.id.money_manger_order_number_name)
    TextView mMoneyMangerOrderNumberName;
    @Bind(R.id.money_manger_order_sizecolor_name)
    TextView mMoneyMangerOrderSizecolorName;
    @Bind(R.id.money_manger_order_goodsprice_name)
    TextView mMoneyMangerOrderGoodspriceName;
    @Bind(R.id.money_manger_order_sumnumber_name)
    TextView mMoneyMangerOrderSumnumberName;
    @Bind(R.id.money_manger_order_blance_name)
    TextView mMoneyMangerOrderBlanceName;
    @Bind(R.id.money_manger_order_totalprice_name)
    TextView mMoneyMangerOrderTotalpriceName;

    SupplierOutAccountDetail.DataEntity.SupplierEntity outData;
    SupplierInAccountDetail.DataEntity.SupplierEntity inData;

    @Override
    public void setContent() {
        setContentView(R.layout.activity_money_order_inner);
        Bundle bundle =  getIntent().getExtras();
        int type = bundle.getInt("type");
        Parcelable detailss = bundle.getParcelable("detailss");

        ButterKnife.bind(this);

        LogUtils.i("type----" + type);
        initData(type, detailss);
        initView(type);

    }


    private void initData(int type,Parcelable details) {
         switch (type){
             case 0:

                 outData = (SupplierOutAccountDetail.DataEntity.SupplierEntity) details;

                 break;
             case 1:

                 inData = (SupplierInAccountDetail.DataEntity.SupplierEntity) details;

                 break;
             default:
             break;
          }
    }


    public void initView(int type) {
        switch (type) {
            case 1:
                mMoneyMangerOrderInnerTitleName.setText("入账详情");
                mMoneyMangerOrderDateName.setText("入账日期：");
                mMoneyMangerOrderNumberName.setText("商品货号：");
                mMoneyMangerOrderSizecolorName.setText("颜色/尺码：");
                mMoneyMangerOrderGoodspriceName.setText("单价：");
                mMoneyMangerOrderSumnumberName.setText("数量：");
                mMoneyMangerOrderTotalpriceName.setText("小计金额：");
                mMoneyMangerOrderTotalprice.setTextColor(Color.BLACK);
                mMoneyMangerOrderBlanceName.setVisibility(View.VISIBLE);
                mMoneyMangerOrderBlance.setVisibility(View.VISIBLE);

                LogUtils.i("入账时间----"+inData.createTime);
                mMoneyMangerOrderDate.setText(inData.createTime);
                mMoneyMangerOrderNumber.setText(inData.productNumber);
                mMoneyMangerOrderSizecolor.setText(inData.colorName+" "+inData.sizeAbbr);
                mMoneyMangerOrderGoodsprice.setText(inData.price+"元");
                mMoneyMangerOrderSumnumber.setText(inData.qty+"");
                mMoneyMangerOrderTotalprice.setText("+"+inData.moneyDetail);
                mMoneyMangerOrderBlance.setText(inData.balance+"");
                break;
            case 0:
                mMoneyMangerOrderInnerTitleName.setText("提现详情");
                mMoneyMangerOrderDateName.setText("提现时期：");
                mMoneyMangerOrderNumberName.setText("开户行：");
                mMoneyMangerOrderSizecolorName.setText("卡号：");
                mMoneyMangerOrderGoodspriceName.setText("提现金额：");
                mMoneyMangerOrderSumnumberName.setText("账户余额：");
                mMoneyMangerOrderTotalpriceName.setText("提现状态：");
                mMoneyMangerOrderTotalprice.setTextColor(Color.parseColor("#FF0099"));
                mMoneyMangerOrderBlanceName.setVisibility(View.GONE);
                mMoneyMangerOrderBlance.setVisibility(View.GONE);

                if (mMoneyMangerOrderDate == null){
                    LogUtils.i("view是null的--------");
                }
                mMoneyMangerOrderDate.setText(outData.TradeTime);
                mMoneyMangerOrderNumber.setText(outData.bankName);
                mMoneyMangerOrderSizecolor.setText(outData.bankNumber);
                mMoneyMangerOrderGoodsprice.setText("- "+outData.moneyDetail);
                mMoneyMangerOrderSumnumber.setText(outData.balance+"");

                String currentState = "";
                 switch (outData.status){
                     case 0:
                         currentState = "待审核";

                         break;
                     case 1:
                         currentState = "已通过";
                         break;
                     case 2:
                         currentState = "未通过";
                         break;
                      default:
                      break;
                  }
                mMoneyMangerOrderTotalprice.setText(currentState);

                break;
        }
    }

    @Override
    public void initTitle() {
        mMoneyMangerOrderTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initView() {
    }

}
