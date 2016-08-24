package com.meilicat.basedemo.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.google.gson.Gson;
import com.meilicat.basedemo.R;
import com.meilicat.basedemo.base.BaseApplication;
import com.meilicat.basedemo.base.MeiliCatSettings;
import com.meilicat.basedemo.bean.UserInfoBean;
import com.meilicat.basedemo.conf.Constants;
import com.meilicat.basedemo.factory.ThreadPoolProxyFactory;
import com.meilicat.basedemo.utils.HttpManager;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.SPUtils;
import com.meilicat.basedemo.utils.T;
import com.meilicat.basedemo.utils.UIUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by lizhiming on 2016/2/19.
 */
public class SplashActivity extends Activity {

    private MeiliCatSettings mSettings;
    private boolean isFirstRun;
    private SPUtils mSpUtils;
    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = View.inflate(this, R.layout.splash_layout, null);

        initWindow();

        //设置亮度的动画，实现渐变显示，从0.3到1.0(全亮)
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(3000);         //设置渐变时间
        view.startAnimation(aa);          //开始一个动画

        setContentView(view);

        mSpUtils = new SPUtils(UIUtils.getContext());
        String cookie = mSpUtils.getString("cookie", "");
        if (TextUtils.isEmpty(cookie)){
            LogUtils.i("cookie------是null的");
            isLogin = false;
        }else {
            LogUtils.i("cookie------不是null的");
            isLogin = true;
        }

        mSettings = BaseApplication.getSettings(this);
        isFirstRun = mSettings.IS_FIRST_RUN.getValue();

        //设置动画监听器，当动画结束的时候，启动新的Activity
        aa.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(isFirstRun) {
                    mSettings.IS_FIRST_RUN.setValue(false);
                    startGuideActivity();
                } else {
                    if (isLogin){
                        ThreadPoolProxyFactory.createNormalThreadPoolProxy().execute(new Runnable() {
                            @Override
                            public void run() {
                                if (mSpUtils.getBoolean("lastIsBuyer", false)){
                                    LogUtils.i("上一个用户是---buyer");
                                    startBuyerActivity();
                                }else {
                                    LogUtils.i("上一个用户是---不是buyer");
                                    if (getUserInfo()) {
                                        //说明获取成功
                                        startHomeActivity();
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                T.showShort(UIUtils.getContext(), "登录信息失效，请重新登录");
                                            }
                                        });

                                        startMainActivity();
                                    }
                                }

                            }
                        });
                    }else {
                        startMainActivity();
                    }
                }
            }

        });
    }
    private boolean getUserInfo() {
        HttpManager manger = new HttpManager(UIUtils.getContext());

        try {

            String response = manger.postSync(Constants.URLS.SUPPLIER_USERINFO, "");
            Gson gson = new Gson();
            UserInfoBean userBean = null;
            try {
                LogUtils.i("获取账号信息--------" + response);
                userBean = gson.fromJson(response, UserInfoBean.class);
            } catch (Exception e) {

                e.printStackTrace();
                return false;
            }
            if (userBean == null){

                return false;
            }
            LogUtils.i("获取userBean成功-------");
            BaseApplication.getInstance().setUserInfo(userBean);
            return true;
        }catch (Exception e){

            e.printStackTrace();
            return false;

        }
    }

    private void startGuideActivity() {
        Intent intent = new Intent(this,GuideActivity.class);
        startActivity(intent);
        finish();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this,LoginAcitivity.class);
        startActivity(intent);
        finish();
    }
    private void startHomeActivity() {
        Intent intent = new Intent(this,HomeActivity2.class);
        startActivity(intent);
        finish();
    }

    private void startBuyerActivity(){
        Intent intent = new Intent(this,BuyerHomeActivity.class);
        startActivity(intent);
        finish();
    }
    private SystemBarTintManager tintManager;

    //TODO 设置沉浸式状态栏
    @TargetApi(19)
    private void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.login_btn_pink));
            tintManager.setStatusBarTintEnabled(true);
        }
    }
}
