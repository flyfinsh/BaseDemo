package com.meilicat.basedemo.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.activity.commonditymanager.CommodityPublishActivity;
import com.meilicat.basedemo.base.BaseActivity;
import com.meilicat.basedemo.base.BaseFragment;
import com.meilicat.basedemo.fragment.GoodsFragment;
import com.meilicat.basedemo.fragment.MoneyFragment;
import com.meilicat.basedemo.fragment.OrderFragment;
import com.meilicat.basedemo.fragment.UserFragment;
import com.meilicat.basedemo.utils.SPUtils;
import com.meilicat.basedemo.utils.UIUtils;
import com.meilicat.basedemo.view.CustomButton;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by cj on 2016/1/23.
 */
public class HomeActivity2 extends BaseActivity implements View.OnClickListener {


    BaseFragment orderFrag;
    Fragment goodsFrag;
    Fragment userFrag;
    BaseFragment moneyFrag;
    @Bind(R.id.home_vp)
    FrameLayout mHomeVp;
    @Bind(R.id.home_ordermanger)
    CustomButton mHomeOrdermanger;
    @Bind(R.id.home_goods)
    CustomButton mHomeGoods;
    @Bind(R.id.home_money)
    CustomButton mHomeMoney;
    @Bind(R.id.home_user)
    CustomButton mHomeUser;
    @Bind(R.id.home_ordermanger_bg)
    RelativeLayout mHomeOrdermangerBg;
    @Bind(R.id.home_goods_bg)
    RelativeLayout mHomeGoodsBg;
    @Bind(R.id.home_add_bg)
    RelativeLayout mHomeAddBg;
    @Bind(R.id.home_money_bg)
    RelativeLayout mHomeMoneyBg;
    @Bind(R.id.home_user_bg)
    RelativeLayout mHomeUserBg;


    @Override
    public void setContent() {
        setContentView(R.layout.activity_home2);

        ButterKnife.bind(this);
        mHomeOrdermanger.setText("订单管理");

        mHomeGoods.setText("商品管理");
        mHomeGoods.setIcon(R.string.icon004);
        mHomeMoney.setText("余额");
        mHomeMoney.setIcon(R.string.icon003);
        mHomeUser.setText("个人中心");
        mHomeUser.setIcon(R.string.icon006);

        SPUtils spUtils = new SPUtils(UIUtils.getContext());
        spUtils.putBoolean("lastIsBuyer", false);

        initEvent();

//        initFrag();
    }

    private static boolean isExit = false;

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(UIUtils.getContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }



    @Override
    public void onClick(View v) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction mFt = fm.beginTransaction();

        switch (v.getId()) {
            case R.id.home_ordermanger:
                curentPosition = 0;
                if (orderFrag == null) {
                    orderFrag = new OrderFragment();
                }

                mFt.replace(R.id.home_vp, orderFrag);
                break;

            case R.id.home_goods:
                curentPosition = 1;
                if (goodsFrag == null) {
                    goodsFrag = new GoodsFragment();
                }
                mFt.replace(R.id.home_vp, goodsFrag);
                break;

            case R.id.home_money:
                curentPosition = 2;
                if (moneyFrag == null) {
                    moneyFrag = new MoneyFragment();
                }
                mFt.replace(R.id.home_vp, moneyFrag);
                break;

            case R.id.home_user:
                curentPosition = 3;
                if (userFrag == null) {
                    userFrag = new UserFragment();
                }
                mFt.replace(R.id.home_vp, userFrag);
                break;

            case R.id.home_add_bg:
                Intent intent = new Intent(this, CommodityPublishActivity.class);
                startActivity(intent);
                break;
        }
        changeButtonColor();
        mFt.commit();

    }

    private void initEvent() {
        mHomeGoods.setOnClickListener(this);
        mHomeOrdermanger.setOnClickListener(this);
        mHomeUser.setOnClickListener(this);
        mHomeMoney.setOnClickListener(this);
        mHomeAddBg.setOnClickListener(this);
        changeButtonColor();
        if (orderFrag == null) {
            orderFrag = new OrderFragment();
        }

        onClick(mHomeOrdermanger);
    }

    @Override
    public void initTitle() {

    }

    @Override
    public void initView() {

    }

    private int curentPosition = 0;

    private void changeButtonColor() {
        if (curentPosition == 0) {
            mHomeOrdermanger.changeColor(true);

            mHomeOrdermangerBg.setSelected(true);
            mHomeGoodsBg.setSelected(false);
            mHomeAddBg.setSelected(false);
            mHomeMoneyBg.setSelected(false);
            mHomeUserBg.setSelected(false);

            mHomeGoods.changeColor(false);
            mHomeMoney.changeColor(false);
            mHomeUser.changeColor(false);

        } else if (curentPosition == 1) {
            mHomeOrdermanger.changeColor(false);
            mHomeGoods.changeColor(true);
            mHomeMoney.changeColor(false);
            mHomeUser.changeColor(false);

            mHomeOrdermangerBg.setSelected(false);
            mHomeGoodsBg.setSelected(true);
            mHomeAddBg.setSelected(false);
            mHomeMoneyBg.setSelected(false);
            mHomeUserBg.setSelected(false);

        } else if (curentPosition == 2) {
            mHomeOrdermanger.changeColor(false);
            mHomeGoods.changeColor(false);
            mHomeMoney.changeColor(true);
            mHomeUser.changeColor(false);

            mHomeOrdermangerBg.setSelected(false);
            mHomeGoodsBg.setSelected(false);
            mHomeAddBg.setSelected(false);
            mHomeMoneyBg.setSelected(true);
            mHomeUserBg.setSelected(false);

        } else if (curentPosition == 3) {
            mHomeOrdermanger.changeColor(false);
            mHomeGoods.changeColor(false);
            mHomeMoney.changeColor(false);
            mHomeUser.changeColor(true);

            mHomeOrdermangerBg.setSelected(false);
            mHomeGoodsBg.setSelected(false);
            mHomeAddBg.setSelected(false);
            mHomeMoneyBg.setSelected(false);
            mHomeUserBg.setSelected(true);
        }
    }


    public double getNewVersion() {
        double newVersion = 2.00;
        return newVersion;
    }
}
