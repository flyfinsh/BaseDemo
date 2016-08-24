package com.meilicat.basedemo.utils;

import android.content.Context;
import android.graphics.Typeface;

public class FontsManager {

    private static Typeface typeface = null;

    public static Typeface getTypeface(Context context) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/iconfont.ttf");
        }
        return typeface;
    }
}