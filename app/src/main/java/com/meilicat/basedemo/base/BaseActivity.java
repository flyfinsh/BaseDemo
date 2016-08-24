package com.meilicat.basedemo.base;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.meilicat.basedemo.R;
import com.meilicat.basedemo.utils.T;
import com.meilicat.basedemo.view.MyLoadingDialog;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 类作用：抽取的一个activity的基类
 * 定义了退出所有activity的方法
 * Created by cj on 15/12/27.
 */
public abstract class BaseActivity extends MonitoredActivity {

    public static final int PRASSAGE_DIALOG = 0x01;
    private MyLoadingDialog mLoadDialog;

    /**
     * 记录处于前台的Activity
     */
    private static BaseActivity mForegroundActivity = null;
    /**
     * 记录所有活动的Activity
     */
    private static final List<BaseActivity> mActivities = new LinkedList<BaseActivity>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mActivities.add(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initWindow();
        setContent();
        initTitle();
        initView();
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

    /**
     * 取消加载中对话框
     */
    @SuppressWarnings("deprecation")
    public void dismissLoadDialog() {
        if (mLoadDialog != null && mLoadDialog.isShowing()) {
            dismissDialog(PRASSAGE_DIALOG);
        }
    }

    /**
     * 显示加载中对话框
     */
    @SuppressWarnings("deprecation")
    public void showLoadDialog() {
        showDialog(PRASSAGE_DIALOG);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case PRASSAGE_DIALOG:
                // mLoadDialog = new Dialog(this, R.style.dialogTheme);
                // mLoadDialog.setContentView(R.layout.dailog_loading_layout);
                // mLoadDialog.setCancelable(true);
                mLoadDialog = new MyLoadingDialog(this, R.style.dialogTheme);
                return mLoadDialog;
        }
        return super.onCreateDialog(id);
    }

    /**
     * 作用： 设置布局xml
     * 作者：cj
     * 时间：
     */
    public abstract void setContent();

    /**
     * 作用：初始化标题
     * 作者：cj
     * 时间：
     */
    public abstract void initTitle();

    /**
     * 作用：初始化控件
     * 作者：cj
     * 时间：
     */
    public abstract void initView();


    @Override
    protected void onResume() {
        mForegroundActivity = this;
        super.onResume();
    }

    @Override
    protected void onPause() {
        mForegroundActivity = null;
        super.onPause();
    }

    /**
     * 关闭所有Activity
     */
    public static void finishAll() {
        List<BaseActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<BaseActivity>(mActivities);
        }
        for (BaseActivity activity : copy) {
            activity.finish();
        }
    }

    /**
     * 关闭所有Activity，除了参数传递的Activity
     */
    public static void finishAll(BaseActivity except) {
        List<BaseActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<BaseActivity>(mActivities);
        }
        for (BaseActivity activity : copy) {
            if (activity != except)
                activity.finish();
        }
    }

   /* public void onBackPressed() {
        cancelToast();
        super.onBackPressed();
    }*/

    @Override
    public void onBackPressed() {
        T.cancelToast();
        super.onBackPressed();
    }

    /**
     * 是否有启动的Activity
     */
    public static boolean hasActivity() {
        return mActivities.size() > 0;
    }

    /**
     * 获取当前处于前台的activity
     */
    public static BaseActivity getForegroundActivity() {
        return mForegroundActivity;
    }

    /**
     * 获取当前处于栈顶的activity，无论其是否处于前台
     */
    public static BaseActivity getCurrentActivity() {
        List<BaseActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<BaseActivity>(mActivities);
        }
        if (copy.size() > 0) {
            return copy.get(copy.size() - 1);
        }
        return null;
    }

    /**
     * 退出应用
     */
    public void exitApp() {
        finishAll();

    }

    /**
     * 作用：退出当前界面，返回上一级界面
     * 作者：cj
     * 时间：
     */
    public void back (View view){
        finish();
    }


}
