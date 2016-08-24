package com.meilicat.basedemo.base;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author cj
 * @描述 这是用于保存一些app的基础信息
 *
 * */

public class BaseMainApp {

    private static BaseMainApp instance;

    private List<Activity> activityList;

    private String mainHostUrl;

    /**
     * 是否登录的标志位
     */
    public boolean isLogin = false;
    /**
     * 订单列表页刷新的标识位
     */
    public boolean isOrderListRefresh = false;
    public boolean goHome = false;
    /**
     * 商户是否登录的标志位
     */
    public boolean isMerLogin = false;
    /**
     * 用户MID
     */
    public String mid = "";

    /**
     * 绑定店铺ID
     * */
    public String bindingSellBid="";

    /**
     * 商户MID
     */
    public String cid = "";
    /**
     * 商户名称
     */
    public String mer_account = "";
    /**
     * 账户安全—账户信息
     */
//    public UserInfo userInfo = null;

    /**
     * 订单列表item对应图片的数量
     */
    public int imageNum = 0;


    public List<Map<String, Object>> brandData = null;// 品牌分类的数据

//	public Basics basics;



    public static synchronized BaseMainApp getInstance() {
        if (null == instance) {
            instance = new BaseMainApp();
        }

        return instance;

    }

    public void addActivity(Activity activity) {
        if (null == activityList) {
            activityList = new LinkedList<Activity>();
        }

        activityList.add(activity);
    }

    /**
     * 清除用户登录信息
     */
    public void resetUserInfo() {
        isLogin = false;
        mid = "";
        // user = null;
        // accountInfo = null;
    }

    /**
     * 清除商户登录信息
     */
    public void resetMerInfo() {
        isMerLogin = false;
        cid = "";
        mer_account = "";
    }



    /**
     * 遍历所有Activity并finish
     */
    public void exit() {
        // 释放资源(调用finish方法可以调用activity相关资源释放接口)
        if (activityList != null) {
            for (Activity activity : activityList) {

                activity.finish();
            }

        }
    }

    public void exitApp() {
        mid = "";
        // user = null;
        isLogin = false;
        resetUserInfo();
        resetMerInfo();
        exit();
        resetInstance();
    }

    /**
     * App退出时擦除对象
     */
    public void resetInstance() {
        if (null != instance) {
            instance = null;
        }
    }
}
