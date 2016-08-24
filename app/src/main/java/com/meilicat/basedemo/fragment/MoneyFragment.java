package com.meilicat.basedemo.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.activity.moneyactivity.MoneyMangerBlance;
import com.meilicat.basedemo.activity.moneyactivity.MoneyMangerOrder;
import com.meilicat.basedemo.base.BaseApplication;
import com.meilicat.basedemo.base.BaseFragment;
import com.meilicat.basedemo.base.LoadingPager;
import com.meilicat.basedemo.bean.SupplierDetailBean;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.UIUtils;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cj on 2016/1/23.
 */
public class MoneyFragment extends BaseFragment {

    @Bind(R.id.money_manger_blance)
    TextView mMoneyMangerBlance;
    @Bind(R.id.money_manger_goblance)
    RelativeLayout mMoneyMangerGoblance;
    @Bind(R.id.money_manger_order)
    RelativeLayout mMoneyMangerOrder;
    private View mRootView;
    private  SupplierDetailBean mDetailBean;

    @Override
    public View initSuccessView() {
        mRootView = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.fragment_money, null);
        LogUtils.i("hero", "初始化money界面");
        ButterKnife.bind(this, mRootView);
        if (mDetailBean != null){

            double money = mDetailBean.data.supplier.money;
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
            // 把转换后的货币String类型返回
            String numString = format.format(money);
            String num = numString.substring(1, numString.length());
            mMoneyMangerBlance.setText(num);
        }


        initEvent();

        return mRootView;
    }



    /**
     * 初始化事件
     * */
    private void initEvent() {
        mMoneyMangerGoblance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //去到余额

                Intent intent = new Intent();
                intent.setClass(UIUtils.getContext(), MoneyMangerBlance.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        mMoneyMangerOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //去到账单
                Intent intent = new Intent();
                intent.setClass(UIUtils.getContext(), MoneyMangerOrder.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public LoadingPager.LoadedResult initData() {
        HttpManager manger = new HttpManager(UIUtils.getContext());

        try {
            String response = manger.run(Constants.URLS.SUPPLIER_TOTAL);
            LogUtils.i("余额第一个接口请求的结果-------"+response);
            Gson gson = new Gson();
            try{
                mDetailBean = gson.fromJson(response, SupplierDetailBean.class);
            }catch (Exception e){
                return LoadingPager.LoadedResult.ERROR;
            }

            if (mDetailBean.msg == 1){
                if (mDetailBean.data != null){

                    BaseApplication.getInstance().setSupplierDetail(mDetailBean);
                    return LoadingPager.LoadedResult.SUCCESS;
                }else {
                    return LoadingPager.LoadedResult.EMPTY;
                }

            }else {

                return LoadingPager.LoadedResult.ERROR;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return LoadingPager.LoadedResult.ERROR;
        }
//        return LoadingPager.LoadedResult.SUCCESS;
    }
}
