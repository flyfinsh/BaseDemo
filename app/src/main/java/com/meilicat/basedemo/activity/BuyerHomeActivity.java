package com.meilicat.basedemo.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.Toast;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseActivity;
import com.meilicat.basedemo.base.BaseFragment;
import com.meilicat.basedemo.fragment.TakeGoodsFragment;
import com.meilicat.basedemo.utils.SPUtils;
import com.meilicat.basedemo.utils.UIUtils;

import butterknife.ButterKnife;

/**
 * Created by cj on 2016/2/1.
 *
 */
public class BuyerHomeActivity extends BaseActivity {
    BaseFragment takeGoodsFrag;
    private SPUtils mSpUtils;

    @Override
    public void setContent() {
        setContentView(R.layout.activity_home_buyer);
        ButterKnife.bind(this);

        mSpUtils = new SPUtils(UIUtils.getContext());
        mSpUtils.putBoolean("lastIsBuyer", true);
        initEvent();
    }

    @Override
    public void initTitle() {
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
    public void initView() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction mFt = fm.beginTransaction();
        if (takeGoodsFrag == null){
            takeGoodsFrag = new TakeGoodsFragment();
        }
        mFt.replace(R.id.home_buyer_vp, takeGoodsFrag);
        mFt.commit();
    }

    private void initEvent() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
