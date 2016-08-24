package com.meilicat.basedemo.activity.commonditymanager;

import com.meilicat.basedemo.bean.ClientColorSizeBean;
import com.meilicat.basedemo.bean.GoodsProductSkuListEntity;

import java.util.ArrayList;

/**
 * Created by lizhiming on 2016/3/10.
 */
public class GoodsModelUtils {

    public static ArrayList<ClientColorSizeBean> appendSizeData(ArrayList<ClientColorSizeBean> tempCSDatas, GoodsProductSkuListEntity childItem) {
        for (ClientColorSizeBean csItem : tempCSDatas) {
            if (childItem.getColorName().equals(csItem.colorName)) {
                csItem.sizeName += " " + childItem.getSizeName();
            }
        }
        return tempCSDatas;
    }


    public static boolean isHaveColor(ArrayList<ClientColorSizeBean> tempCSDatas, GoodsProductSkuListEntity childItem) {
        if (tempCSDatas == null || tempCSDatas.isEmpty()) {
            return false;
        }

        for (ClientColorSizeBean csItem : tempCSDatas) {
            if (childItem.getColorName().equals(csItem.colorName)) {
                return true;
            }
        }
        return false;
    }

}
