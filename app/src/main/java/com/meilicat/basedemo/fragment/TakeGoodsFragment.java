package com.meilicat.basedemo.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.activity.BuyerHomeActivity;
import com.meilicat.basedemo.activity.LoginAcitivity;
import com.meilicat.basedemo.activity.buyeractivity.BuyerHandlerActivity;
import com.meilicat.basedemo.activity.buyeractivity.BuyerUnhandlerActivityOther;
import com.meilicat.basedemo.base.BaseFragment;
import com.meilicat.basedemo.base.LoadingPager;
import com.meilicat.basedemo.bean.LoginBean;
import com.meilicat.basedemo.bean.purchaser.PurchaserDetail;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.IntentUtil;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.SPUtils;
import com.meilicat.basedemo.utils.T;
import com.meilicat.basedemo.utils.UIUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class TakeGoodsFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.buyer_user_logo)
    CircleImageView mBuyerUserLogo;
    @Bind(R.id.buyer_user_name)
    TextView mBuyerUserName;
    @Bind(R.id.order_user_number)
    TextView mOrderUserNumber;
    @Bind(R.id.buyer_manger_1)
    TextView mBuyerManger1;
    @Bind(R.id.buyer_manger_unhandler)
    RelativeLayout mBuyerMangerUnhandler;
    @Bind(R.id.buyer_manger_2)
    TextView mBuyerManger2;
    @Bind(R.id.buyer_manger_unprepare)
    RelativeLayout mBuyerMangerUnprepare;
    @Bind(R.id.buyer_out_login)
    TextView mBuyerOutLogin;
    private PurchaserDetail mDetail;
    private SPUtils mSPUtils;

    @Override
    public View initSuccessView() {
        mRootView = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.fragment_buyer_takegoods, null);

        mSPUtils = new SPUtils(UIUtils.getContext());

        ButterKnife.bind(this, mRootView);
        initEvent();

        initView();

        return mRootView;
    }


    private void initView() {
        if (mDetail != null) {
            if (mDetail.data.purchaser != null) {
                mOrderUserNumber.setText(mDetail.data.purchaser.account);
                mBuyerUserName.setText(mDetail.data.purchaser.fullName);

                mBuyerManger1.setText(mDetail.data.purchaser.dBHProductNum + "件");
                mBuyerManger2.setText(mDetail.data.purchaser.yTHProductNum + "件");
            }
        }
    }

    private void initEvent() {
        mBuyerMangerUnhandler.setOnClickListener(this);
        mBuyerMangerUnprepare.setOnClickListener(this);
        mBuyerOutLogin.setOnClickListener(this);
    }

    @Override
    public LoadingPager.LoadedResult initData() {
        LogUtils.i("加载数据---------init");
        HttpManager manager = new HttpManager(UIUtils.getContext());
        mSPUtils = new SPUtils(UIUtils.getContext());

        try {
            String response = manager.run(Constants.URLS.BUYER_BUYERDETAIL);
            Gson gson = new Gson();
            try {
                mDetail = gson.fromJson(response, PurchaserDetail.class);
            } catch (Exception e) {
                LogUtils.i("异常其实在这里-------------");
                mSPUtils.putString("cookie","");

                return LoadingPager.LoadedResult.ERROR;
            }

            if (mDetail != null) {
                if (mDetail.msg == 1) {
                    if (mDetail.data != null) {

                        return LoadingPager.LoadedResult.SUCCESS;
                    } else {
                        mSPUtils.putString("cookie", "");
                        return LoadingPager.LoadedResult.EMPTY;
                    }
                } else {
                    mSPUtils.putString("cookie", "");
                    return LoadingPager.LoadedResult.ERROR;
                }
            } else {
                return LoadingPager.LoadedResult.EMPTY;
            }

        } catch (IOException e) {
            e.printStackTrace();
            UIUtils.postTaskSafely(new Runnable() {
                @Override
                public void run() {
                    T.showShort(UIUtils.getContext(), "登录信息过期，请重新登录");
                }
            });

            mSPUtils.putString("cookie", "");

            IntentUtil.startActivity(getActivity(), LoginAcitivity.class);

            return LoadingPager.LoadedResult.ERROR;
        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buyer_manger_unhandler:
                IntentUtil.startActivity(getActivity(), BuyerUnhandlerActivityOther.class);
                break;
            case R.id.buyer_manger_unprepare:
                IntentUtil.startActivity(getActivity(), BuyerHandlerActivity.class);
                break;
            case R.id.buyer_out_login:
                outLogin();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDetail == null){
         return;
        }
        if(mDetail.data == null){
        }else {
            LogUtils.i("刷新数据-------------onResume");
            refreshData();
        }
    }


    private void refreshData() {
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
                Gson mGson = new Gson();
                if (obj != null){
                    try{
                        mDetail = mGson.fromJson(obj + "", PurchaserDetail.class);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (mDetail == null){
                        T.showShort(UIUtils.getContext(),"刷新数据失败");
                        return;
                    }
                    if (mDetail.msg == 0){
                        T.showShort(UIUtils.getContext(),"刷新数据失败");
                    }else {
                        if (mDetail.data == null){
                            T.showShort(UIUtils.getContext(),"刷新数据失败");
                            return ;
                        }else {
                            LogUtils.i("刷新视图");
                            LogUtils.i("------"+ mDetail.data.purchaser.dBHProductNum+"--------------"+ mDetail.data.purchaser.yTHProductNum);

                            initView();
                        }
                    }
                }else {
                    T.showShort(UIUtils.getContext(),"刷新数据失败");
                }
            }
        };

        manger.get(Constants.URLS.BUYER_BUYERDETAIL);
    }

    private void outLogin(){
        HttpManager manger = new HttpManager(UIUtils.getContext()){
            @Override
            protected void onFail() {
                T.showShort(UIUtils.getContext(), "退出登录失败");
            }

            @Override
            protected void onSuccess(Object obj) {
                Gson gson = new Gson();
                LoginBean bean = null;
                try{
                    bean = gson.fromJson(obj + "", LoginBean.class);
                }catch (Exception e){
                    e.printStackTrace();
                    T.showShort(UIUtils.getContext(), "退出登录失败");
                }
                if (bean == null){
                    T.showShort(UIUtils.getContext(), "退出登录失败");
                    return;
                }

                if (bean.msg == 0){
                    T.showShort(UIUtils.getContext(),"退出登录失败");
                }else {
                    T.showShort(UIUtils.getContext(),"退出登录成功");

                    mSPUtils.putString("cookie", "");

                    IntentUtil.startActivity(getActivity(), LoginAcitivity.class);
                    ((BuyerHomeActivity)getActivity()).finishAll();
                }
            }
        };
        Map<String,String> map = new HashMap<>();

        manger.post(Constants.URLS.OUTLOGIN, map);
    }

}
