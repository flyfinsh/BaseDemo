package com.meilicat.basedemo.base;

import android.os.Bundle;
import android.view.View;

import com.meilicat.basedemo.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 类作用：抽取的一个activity的基类
 * 定义了退出所有activity的方法
 * 该类集成了zhy的屏幕的百分比适配
 * Created by cj on 15/12/27.
 */
public abstract class BaseAutoActivity extends AutoLayoutActivity {

    /**
     * 记录处于前台的Activity
     */
    private static BaseAutoActivity mForegroundActivity = null;
    /**
     * 记录所有活动的Activity
     */
    private static final List<BaseAutoActivity> mActivities = new LinkedList<BaseAutoActivity>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivities.add(this);

        setContent();
        initTitle();
        initView();
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
        List<BaseAutoActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<BaseAutoActivity>(mActivities);
        }
        for (BaseAutoActivity activity : copy) {
            activity.finish();
        }
    }

    /**
     * 关闭所有Activity，除了参数传递的Activity
     */
    public static void finishAll(BaseAutoActivity except) {
        List<BaseAutoActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<BaseAutoActivity>(mActivities);
        }
        for (BaseAutoActivity activity : copy) {
            if (activity != except)
                activity.finish();
        }
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
    public static BaseAutoActivity getForegroundActivity() {
        return mForegroundActivity;
    }

    /**
     * 获取当前处于栈顶的activity，无论其是否处于前台
     */
    public static BaseAutoActivity getCurrentActivity() {
        List<BaseAutoActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<BaseAutoActivity>(mActivities);
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
