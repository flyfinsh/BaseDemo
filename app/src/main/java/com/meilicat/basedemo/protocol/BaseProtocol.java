package com.meilicat.basedemo.protocol;

import android.text.TextUtils;
import android.util.LruCache;

import com.meilicat.basedemo.base.BaseApplication;
import com.meilicat.basedemo.utils.DiskLruCache;
import com.meilicat.basedemo.utils.IOUtils;
import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.MD5Util;
import com.meilicat.basedemo.utils.StringUtils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by cj on 2016/2/26.
 * 这是进行网络请求和图片缓存的基类
 */
public  class BaseProtocol {
    private static BaseProtocol mProtocol;

    private BaseProtocol(){

    }

    public static BaseProtocol getInstance(){
        if (mProtocol == null){
            synchronized (BaseProtocol.class){
                if (mProtocol == null){
                    mProtocol = new BaseProtocol();
                }
            }
        }
        return mProtocol;
    }
    public String loadData(String url){

        String key = generateKey(url);


        String memJsonString = loadDataFromCache(key);
        if (!TextUtils.isEmpty(memJsonString)){
            return memJsonString;
        }

        String diskJsonString = loadDataFromDisk(key);
        if (!TextUtils.isEmpty(diskJsonString)){
            return diskJsonString;
        }

        return null;
    }

    public void saveData(String url,String result){
        String key = generateKey(url);
        DiskLruCache diskCache = BaseApplication.getInstance().getDiskCache();
        OutputStream outputStream = null;
        try {
            DiskLruCache.Editor edit = diskCache.edit(key);
            outputStream = edit.newOutputStream(0);
            LogUtils.i("将数据保存到磁盘");
            outputStream.write(result.getBytes());
            edit.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(outputStream);
        }

        LruCache<String, String> map = BaseApplication.getInstance().getProtocolMap();
        LogUtils.i("将数据保存到内存");
        map.put(key,result);
    }



    //从磁盘加载数据
    protected  String loadDataFromDisk(String key){
        DiskLruCache diskCache = BaseApplication.getInstance().getDiskCache();

        try {
            DiskLruCache.Snapshot snapshot = diskCache.get(key);

            if (snapshot != null){
                String string = StringUtils.streamToString(snapshot.getInputStream(0));
                return string;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String loadDataFromCache(String key) {

        LruCache<String, String> lruCache = BaseApplication.getInstance().getProtocolMap();

       return lruCache.get(key);
    }


    private String generateKey(String url) {
        return MD5Util.md5(url);
    }

}
