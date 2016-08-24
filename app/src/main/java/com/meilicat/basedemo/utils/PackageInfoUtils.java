package com.meilicat.basedemo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @创建者: cj
 * @创建时间:
 * @描述: 这是获得app当前版本号的工具类
 * @更新者: $$Author$$
 * @更新时间:
 * @更新描述:
 */
public class PackageInfoUtils {
    //获得了应用程序的包名
    public static String getPackgeVersion(Context context) {

        //获得版本信息

        //拿到 包管理器 , 任何一个应用程序 都有一个唯一的包名
        PackageManager pm = context.getPackageManager();

        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "解析包名失败";
        }


    }
}
