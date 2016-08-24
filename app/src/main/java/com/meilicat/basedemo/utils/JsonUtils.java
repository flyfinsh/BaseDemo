package com.meilicat.basedemo.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * Created by cj on 2016/1/22.
 * 这是json数据的一个工具类
 */
public class JsonUtils {

    /**
     * 该方法用于将map数据转出json格式的String
     * */
    public static String map2Json(Map map) throws JSONException {
        JSONObject json = new JSONObject();
        Set<String> keySet = map.keySet();
        for(String key:keySet)
        {
            json.put(key,map.get(key));
        }

        return json.toString();
    }
}
