package com.meilicat.basedemo.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.activity.orderactivity.OrderCommontActivty;
import com.meilicat.basedemo.base.BaseApplication;
import com.meilicat.basedemo.base.BaseFragment;
import com.meilicat.basedemo.base.LoadingPager;
import com.meilicat.basedemo.bean.UserInfoBean;
import com.meilicat.basedemo.bean.UserInfoSupplierEntity;
import com.meilicat.basedemo.bean.order.OrderMain;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.ImageLoaderUtil;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.T;
import com.meilicat.basedemo.utils.UIUtils;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cj on 2016/1/23.
 */
public class OrderFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.order_user_logo)
    CircleImageView mOrderHeadLogo;
    @Bind(R.id.order_user_name)
    TextView mOrderUserName;
    @Bind(R.id.order_user_type)
    TextView mOrderUserType;
    @Bind(R.id.order_user_number)
    TextView mOrderUserNumber;
    @Bind(R.id.order_manger_1)
    TextView mOrderManger1;
    @Bind(R.id.order_manger_unhandler)
    RelativeLayout mOrderMangerUnhandler;
    @Bind(R.id.order_manger_2)
    TextView mOrderManger2;
    @Bind(R.id.order_manger_unprepare)
    RelativeLayout mOrderMangerUnprepare;
    @Bind(R.id.order_manger_3)
    TextView mOrderManger3;
    @Bind(R.id.order_manger_waitchoice)
    RelativeLayout mOrderMangerWaitchoice;
    @Bind(R.id.order_manger_4)
    TextView mOrderManger4;
    @Bind(R.id.order_manger_new)
    RelativeLayout mOrderMangerNew;
    @Bind(R.id.order_manger_5)
    TextView mOrderManger5;
    @Bind(R.id.order_manger_pay)
    RelativeLayout mOrderMangerPay;
    @Bind(R.id.order_manger_6)
    TextView mOrderManger6;
    @Bind(R.id.order_manger_refund)
    RelativeLayout mOrderMangerRefund;
    private View mRootView;
    private OrderMain.DataEntity mData;
    private UserInfoBean mUserInfo;
    private Gson mGson;

    @Override
    public View initSuccessView() {
        mRootView = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.fragment_order, null);

        ButterKnife.bind(this, mRootView);

        initView();
        initEvent();


        return mRootView;
    }

    private void initEvent() {
        mOrderMangerUnhandler.setOnClickListener(this);
        mOrderMangerUnprepare.setOnClickListener(this);
        mOrderMangerWaitchoice.setOnClickListener(this);

        mOrderMangerNew.setOnClickListener(this);
        mOrderMangerPay.setOnClickListener(this);
        mOrderMangerRefund.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        if (mData == null){
        }else {
            refreshData();
        }
        super.onResume();
    }

    private void refreshData(){
        //刷新数据
        HttpManager manger = new HttpManager(UIUtils.getContext()){
            @Override
            protected void onFail() {
                T.showShort(UIUtils.getContext(),"刷新数据失败");
            }
            @Override
            protected void onTimeOut() {
                T.showShort(UIUtils.getContext(),"刷新数据超时");
            }
            @Override
            protected void onSuccess(Object obj) {
                if (obj != null){
                    OrderMain main = null;
                    try{
                        main = mGson.fromJson(obj + "", OrderMain.class);
                    }catch (Exception e){
                        T.showShort(UIUtils.getContext(), "刷新数据失败");
                        e.printStackTrace();
                    }
                    if (main == null){
                        T.showShort(UIUtils.getContext(),"刷新数据失败");
                        return;
                    }
                    if (main.msg == 0){
                        T.showShort(UIUtils.getContext(),"刷新数据失败");
                    }else {
                        if (main.data == null){
                            T.showShort(UIUtils.getContext(),"刷新数据失败");
                            return ;
                        }else {
                            mData = main.data;
                            LogUtils.i("-----我是数据---"+mData.waitOrderCount+"----------"+mData.waitBeReadyCount);
                            initView();
                        }
                    }
                }else {
                    T.showShort(UIUtils.getContext(),"刷新数据失败");
                }
            }
        };

        manger.get(Constants.URLS.ORDER_MAIN);
    }

    private void initView() {
        LogUtils.i("刷新数量--------------"+mData.waitOrderCount+"----"+mData.waitBeReadyCount+"------"+mData.waitGoodsCount);
        mOrderManger1.setText(mData.waitOrderCount+"");
        mOrderManger2.setText(mData.waitBeReadyCount+"");
        mOrderManger3.setText(mData.waitGoodsCount+"");
        mOrderManger4.setText(mData.nowAddOrderCount+"");
        mOrderManger5.setText(mData.nowStatOrderCount+"");
        mOrderManger6.setText(mData.nowReturnOrderCount+"");

        if (mUserInfo != null){
            UserInfoSupplierEntity supplier = mUserInfo.getData().getSupplier();
            String imageURL = supplier.getAvatarImageURL();
            if (mOrderHeadLogo == null){
                LogUtils.i("logo----null");
            }else {
                ImageLoaderUtil.getInstance(UIUtils.getContext()).displayImage(imageURL,mOrderHeadLogo);
                String name = supplier.getName();
                String supName = supplier.getSupAccount();
                int isSign = supplier.getIsSign();
                if (isSign == 1){
                    mOrderUserType.setVisibility(View.VISIBLE);
                }else {
                    mOrderUserType.setVisibility(View.GONE);
                }

                mOrderUserNumber.setText(supName);
                mOrderUserName.setText(name);
            }
        }else {
            LogUtils.i("mUserInfo是null的----------");
        }

    }

    @Override
    public LoadingPager.LoadedResult initData() {

        mUserInfo = BaseApplication.getInstance().getUserInfo();


        try {
            HttpManager manger = new HttpManager(UIUtils.getContext());

            String response = manger.run(Constants.URLS.ORDER_MAIN);
            LogUtils.i("response--------------------"+response);
            mGson = new Gson();

            if (TextUtils.isEmpty(response)){
                return LoadingPager.LoadedResult.EMPTY;
            }
            OrderMain orderMain = null;
            try{

                orderMain = mGson.fromJson(response, OrderMain.class);
            }catch (Exception e){
                e.printStackTrace();
            }
            if (orderMain == null){
                return LoadingPager.LoadedResult.ERROR;
            }

            if (orderMain.msg == 0){
                return LoadingPager.LoadedResult.ERROR;
            }else {
                if (orderMain.data == null){
                    return LoadingPager.LoadedResult.EMPTY;
                }else {

                    mData = orderMain.data;

                    return LoadingPager.LoadedResult.SUCCESS;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

            return LoadingPager.LoadedResult.ERROR;
        }

    }



    @Override
    public void onClick(View v) {
        int state = -1;
        int which = -1;
        int type = -1;

        switch (v.getId()) {
            case R.id.order_manger_unhandler:
                state = 1;
                type = 1;
                break;

            case R.id.order_manger_unprepare:
                state = 1;
                type = 2;
                break;

            case R.id.order_manger_waitchoice:
                state = 1;
                type = 3;
                break;

            case R.id.order_manger_new:
                state = 2;
                which = 1;
                type = 4;
                break;

            case R.id.order_manger_pay:
                state = 3;
                which = 2;
                type = 5;
                break;
            case R.id.order_manger_refund:
                state = 3;
                which = 3;
                type = 6;
                break;

        }
        Intent intent = new Intent(getActivity(), OrderCommontActivty.class);
        intent.putExtra("state", state);
        intent.putExtra("which", which);
        intent.putExtra("type",type);
        startActivity(intent);
    }


}
