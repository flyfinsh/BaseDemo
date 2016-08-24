package com.meilicat.basedemo.utils.cookiemanger;

import com.meilicat.basedemo.utils.LogUtils;
import com.meilicat.basedemo.utils.UIUtils;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by cj on 2016/2/25.
 */
public class CookiesManger implements CookieJar{
    private final PersistentCookieStore cookieStore = new PersistentCookieStore(UIUtils.getContext());
    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
        if (list != null && list.size() > 0) {
            for (Cookie item : list) {
                LogUtils.i("url--"+ httpUrl+"--------------cookie----"+item.toString());
                cookieStore.add(httpUrl, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        List<Cookie> cookies = cookieStore.get(httpUrl);
        return cookies;
    }
}
